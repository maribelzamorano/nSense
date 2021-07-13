/*
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This file is part of PeerfactSim.KOM.
 * 
 * PeerfactSim.KOM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * PeerfactSim.KOM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerfactSim.KOM.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package de.tud.kom.p2psim.impl.service.aggr.gossip;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.ConnectivityListener;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.JoinLeaveOverlayNode;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.service.aggr.IAggregationMap;
import de.tud.kom.p2psim.api.service.aggr.IAggregationResult;
import de.tud.kom.p2psim.api.service.aggr.IAggregationService;
import de.tud.kom.p2psim.api.service.aggr.NoSuchValueException;
import de.tud.kom.p2psim.impl.application.AbstractApplication;
import de.tud.kom.p2psim.impl.service.aggr.gossip.operations.AggregationResultDummyOp;
import de.tud.kom.p2psim.impl.service.aggr.gossip.operations.ResyncOperation;
import de.tud.kom.p2psim.impl.service.aggr.gossip.operations.UpdateCallerOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.Transformer;
import de.tud.kom.p2psim.impl.util.Tuple;
import de.tud.kom.p2psim.impl.util.functiongenerator.FunctionGenerator;
import de.tud.kom.p2psim.impl.util.functiongenerator.functions.Function;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Realization of IAggregationService that approximates aggregation results by
 * randomly distributing information about the current estimation and the own
 * local values among neighbors. The approximations will converge to the exact
 * results with increasing time.
 * 
 * To be adaptive, this method is split into time intervals called "epochs" with
 * each epoch making its own approximation and resetting the values afterwards.
 * 
 * This aggregation service implementation is based on Márk Jelasity, Alberto
 * Montresor, and Ozalp Babaoglu. 2005. Gossip-based aggregation in large
 * dynamic networks. ACM Trans. Comput. Syst. 23, 3 (August 2005), 219-252.
 * 
 * @see IAggregationService
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class GossipingAggregationService extends AbstractApplication implements
		IAggregationService<Object> {

	static final Logger log = SimLogger
			.getLogger(GossipingAggregationService.class);

	private INeighborDeterminator nds;

	private short port;

	private IConfiguration conf;

	long epoch = 0;

	int cyclesInEpoch = 0;

	Synchronizer sync = new Synchronizer(this);

	UpdateCallerOperation updOp = null;

	Map<Object, GossipingAggregationValue> locAggrVals = new HashMap<Object, GossipingAggregationValue>();

	private JoinLeaveOverlayNode nd;

	private int uid;

	ResyncOperation resyncOp;

	private boolean rpcLocked;

	private GossipingNodeCountValue ncValue;

	private boolean joined = false;

	public GossipingAggregationService(Host host, JoinLeaveOverlayNode nd,
			INeighborDeterminator nds, short port, IConfiguration conf, int uid) {
		super();
		this.setHost(host);
		this.nd = nd;
		this.nds = nds;
		this.port = port;
		this.conf = conf;
		this.uid = uid;
		host.getNetLayer().addConnectivityListener(
				this.new ConnectivityListenerImpl());
		host.getTransLayer()
				.addTransMsgListener(new MessageHandler(this), port);
		if (host.getNetLayer().isOnline())
			startNewUpdateCallerOperation();

		ncValue = new GossipingNodeCountValue(this);

	}

	@Override
	public double setLocalValue(Object identifier, double value)
			throws NoSuchValueException {
		GossipingAggregationValue val = locAggrVals.get(identifier);
		if (val == null) {
			val = new GossipingAggregationValue(value, this);
			locAggrVals.put(identifier, val);
		}
		double oldVal = val.getValue();
		val.updateValue(value);
		return oldVal;
	}

	public int getUID() {
		return uid;
	}

	@Override
	public double getLocalValue(Object identifier) throws NoSuchValueException {
		GossipingAggregationValue val = locAggrVals.get(identifier);
		if (val == null)
			throw new NoSuchValueException(identifier);
		return val.getValue();
	}

	public INeighborDeterminator getNeighborDeterminationStrategy() {
		return nds;
	}

	public short getPort() {
		return port;
	}

	public void reset() {
		sync.reset();
	}

	protected void resetRaw() {
		ncValue.restart();
		for (GossipingAggregationValue val : locAggrVals.values()) {
			val.restart();
		}
	}

	@Override
	public int getAggregationResult(final Object identifier,
			final OperationCallback<IAggregationResult> callback)
			throws NoSuchValueException {
		GossipingAggregationValue val = locAggrVals.get(identifier);
		if (val == null)
			throw new NoSuchValueException(identifier);
		AggregationResultDummyOp op = new AggregationResultDummyOp(this,
				new OperationCallback<IAggregationResult>() {

					@Override
					public void calledOperationFailed(
							Operation<IAggregationResult> op) {
						Simulator.getMonitor().aggregationQueryFailed(
								GossipingAggregationService.this.getHost(),
								identifier, op.getOperationID());
						callback.calledOperationFailed(op);

					}

					@Override
					public void calledOperationSucceeded(
							Operation<IAggregationResult> op) {
						Simulator.getMonitor()
								.aggregationQuerySucceeded(
										GossipingAggregationService.this
												.getHost(), identifier,
										op.getOperationID(), op.getResult());
						callback.calledOperationSucceeded(op);
					}
				}, val.getAggregationResult());
		op.scheduleImmediately();
		Simulator.getMonitor().aggregationQueryStarted(this.getHost(),
				identifier, op.getOperationID());
		return op.getOperationID();
	}

	public List<Tuple<Object, UpdateInfo>> getAllLocalUpdateInfos() {

		return Tuple.transformSecondArgumentInList(
				Tuple.tupleListFromMap(locAggrVals),
				new Transformer<GossipingAggregationValue, UpdateInfo>() {

					@Override
					public UpdateInfo transform(GossipingAggregationValue oldVal) {
						return oldVal.extractInfo();
					}

				});
	}

	public IConfiguration getConf() {
		return conf;
	}

	public void updateLocalValues(List<Tuple<Object, UpdateInfo>> infos,
			UpdateInfoNodeCount foreignNCValue, String dbgNote) {
		ncValue.merge(foreignNCValue, dbgNote);
		for (Tuple<Object, UpdateInfo> t : infos) {
			GossipingAggregationValue val = locAggrVals.get(t.getA());
			if (val != null) {
				val.merge(t.getB(), dbgNote);
			} else if (conf.gatherMode()) {
				val = GossipingAggregationValue.fromInfo(t.getB(), this);
				locAggrVals.put(t.getA(), val);
			}
		}
	}

	/**
	 * To fix Special Case S1: A neighbor sends an update request and itself is
	 * outdated (low epoch). This method must be called after updating the
	 * outdated neighbor.
	 */
	public void updateWhenOutdatedNeighbor(List<Tuple<Object, UpdateInfo>> infos) {

		ncValue.mergeOnOutdatedNeighbor();

		for (Tuple<Object, UpdateInfo> t : infos) {
			GossipingAggregationValue val = locAggrVals.get(t.getA());
			if (val == null && conf.gatherMode()) {
				val = GossipingAggregationValue.fromInfo(t.getB(), this);
				locAggrVals.put(t.getA(), val);
			}
		}
	}

	public Synchronizer getSync() {
		return sync;
	}

	private class ConnectivityListenerImpl implements ConnectivityListener {

		ConnectivityListenerImpl() {
			// Nothing to do
		}

		@Override
		public void connectivityChanged(ConnectivityEvent ce) {
			if (ce.isOffline()) {
				stopUpdateCallerOperation();
			} else {
				resyncOp = new ResyncOperation(GossipingAggregationService.this);
				resyncOp.scheduleImmediately();
			}
		}

	}

	public void stopUpdateCallerOperation() {
		if (resyncOp != null)
			resyncOp.stop();
		if (updOp != null)
			updOp.stop();
		updOp = null;
	}

	public void startNewUpdateCallerOperation() {
		assert getHost().getNetLayer().isOnline() : "Attempted to start update caller operation, but host is offline";
		if (updOp != null)
			throw new IllegalStateException(
					"Update Caller Operation already started at node "
							+ getHost().getNetLayer().getNetID());
		long updPeriod = conf.getUpdatePeriod();
		updOp = new UpdateCallerOperation(this, updPeriod);
		updOp.scheduleWithDelay(Math.round(Simulator.getRandom().nextDouble()
				* updPeriod));
	}

	@Override
	public void join(OperationCallback<Object> cb) {
		nd.join(cb);
		if (joined)
			return;
		log.debug(Simulator.getSimulatedRealtime() + " Started gossip at node "
				+ getHost().getNetLayer().getNetID());
		joined = true;
	}

	@Override
	public void leave(OperationCallback<Object> cb) {
		nd.leave(cb);
		joined = false;
		log.debug(Simulator.getSimulatedRealtime() + " Stopped gossip at node "
				+ getHost().getNetLayer().getNetID());
	}

	public OverlayNode getOverlayUsed() {
		return nd;
	}

	/**
	 * Returns a unique identifier for this instance that may be useful for e.g.
	 * logging
	 * 
	 * @return
	 */
	public Object getIdStr() {
		return this.getHost().getNetLayer().getNetID();
	}

	@Override
	public int getAggregationResultMap(
			OperationCallback<IAggregationMap<Object>> callback) {
		// FIXME Implement me
		return 0;
	}

	@Override
	public List<Object> getIdentifiers() {
		List<Object> result = new Vector<Object>();
		if (locAggrVals != null) {
			result.addAll(locAggrVals.keySet());
		}
		return result;
	}

	@Override
	public IAggregationResult getStoredAggregationResult(Object identifier) {
		GossipingAggregationValue val = locAggrVals.get(identifier);
		if (val == null)
			return null;
		return val.getAggregationResult();
	}

	@Override
	public long getGlobalAggregationReceivingTime(Object identifier) {
		GossipingAggregationValue val = locAggrVals.get(identifier);
		if (val == null)
			return 0;
		return val.getGlobalAggregationTimestamp();
	}

	public boolean isSynced() {
		return updOp != null;
	}

	public void setRPCLocked(boolean rpcLocked) {
		this.rpcLocked = rpcLocked;
	}

	public boolean isRPCLocked() {
		return rpcLocked;
	}

	public void measureAttributes() {
		// Fill the aggregation map with the values of the functionGenerator
		FunctionGenerator generator = FunctionGenerator.getInstance();
		Map<Class<? extends Function>, Double> functionResults = generator
				.getValues();
		if (functionResults != null && functionResults.size() > 0) {
			Iterator<Class<? extends Function>> iter = functionResults.keySet()
					.iterator();
			Class<? extends Function> clazz = null;
			while (iter.hasNext()) {
				clazz = iter.next();
				String name = clazz.getSimpleName();
				try {
					setLocalValue("Function_" + name,
							functionResults.get(clazz));
				} catch (NoSuchValueException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public int getNumberOfMonitoredAttributes() {
		return locAggrVals.size();
	}

	public GossipingNodeCountValue getGossipingNodeCountValue() {
		return ncValue;
	}

}
