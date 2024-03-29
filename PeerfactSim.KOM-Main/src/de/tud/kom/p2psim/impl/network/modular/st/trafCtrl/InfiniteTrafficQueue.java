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


package de.tud.kom.p2psim.impl.network.modular.st.trafCtrl;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.impl.network.modular.st.TrafficControlStrategy;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.LiveMonitoring;
import de.tud.kom.p2psim.impl.util.livemon.AvgAccumulatorTime;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * A traffic queue that enqueues packets, if the sender attempts to send too
 * fast, or if the receiver receives packets too fast. This component may
 * increase the total latency of the message delivery. The size of the queue is
 * unlimited. The advantage is that no packet ever gets dropped by this
 * component, but mind that this may cause large message latencies and a high
 * memory consumption during simulation.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class InfiniteTrafficQueue implements TrafficControlStrategy {

	private static Logger log = SimLogger.getLogger(InfiniteTrafficQueue.class);

	static AvgAccumulatorTime qSizeStat = new AvgAccumulatorTime(
			"Net: Traffic Control Queue Size", 50);

	public InfiniteTrafficQueue() {
		LiveMonitoring.addProgressValueIfNotThere(qSizeStat);
	}

	@Override
	public void onSendRequest(final ISendContext ctx, final NetMessage msg,
			NetID receiver) {
		TrafficControlMeta meta = getTrafficControlMeta(ctx);

		long cur = Simulator.getCurrentTime();
		long nx = meta.getNextFreeSendingTime();

		long sendTime = Math.max(cur, nx);

		if (isTooLongSend(sendTime - cur)) {
			ctx.dropMessage(msg);
		} else {
			Simulator.scheduleEvent(null, sendTime,
					new SimulationEventHandler() {

						@Override
						public void eventOccurred(SimulationEvent se) {
							ctx.sendSubnet(msg);
						}

					}, SimulationEvent.Type.MESSAGE_AT_SUBNET);

			long nextFreeSendingTime = sendTime
					+ Math.round(msg.getSize() / ctx.getMaxBW().getUpBW()
							* Simulator.SECOND_UNIT);

			meta.setNextFreeSendingTime(nextFreeSendingTime);

			long delayTime = (sendTime - cur);

			log.debug("Delaying message " + msg
					+ " at the sender's traffic queue for " + delayTime
					+ ". The next message may be sent at "
					+ nextFreeSendingTime);

			qSizeStat.newVal(delayTime);
		}

	}

	/**
	 * Returns whether the sending traffic queue is too long
	 * 
	 * @param waitTime
	 * @return
	 */
	protected boolean isTooLongSend(long waitTime) {
		return false;
	}

	@Override
	public void onReceive(final IReceiveContext ctx, final NetMessage message) {
		TrafficControlMeta meta = getTrafficControlMeta(ctx);

		long cur = Simulator.getCurrentTime();
		long nx = meta.getNextFreeArrivingTime();

		long arriveTime = Math.max(cur, nx);

		long msgTransfFinishTimeLocal = arriveTime
				+ Math.round(message.getSize() / ctx.getMaxBW().getDownBW()
						* Simulator.SECOND_UNIT);

		long msgTransfFinishTimeGlobal = arriveTime
				+ Math.round(message.getSize()
						/ Math.min(ctx.getBandwidthOfSender().getUpBW(), ctx
								.getMaxBW().getDownBW())
						* Simulator.SECOND_UNIT);
		// The time the message would be transferred by the receiver and the
		// sender, assuming a stream.

		if (isTooLongRcv(arriveTime - cur)) {
			ctx.dropMessage(message);
		} else {
			Simulator.scheduleEvent(null, msgTransfFinishTimeGlobal,
					new SimulationEventHandler() {

						@Override
						public void eventOccurred(SimulationEvent se) {
							ctx.arrive(message);
						}

					}, SimulationEvent.Type.MESSAGE_RECEIVED);

			long delayTime = (msgTransfFinishTimeGlobal - cur);

			log.debug("Delaying message "
					+ message
					+ " at the receiver's traffic queue for "
					+ delayTime
					+ " (w.r.t. to the time it is fully transferred to the endpoint). "
					+ "The next message may be received at "
					+ msgTransfFinishTimeLocal);

			meta.setNextFreeArrivingTime(msgTransfFinishTimeLocal);

			qSizeStat.newVal(delayTime);

		}

	}

	protected boolean isTooLongRcv(long waitTime) {
		return false;
	}

	private TrafficControlMeta getTrafficControlMeta(IContext ctx) {
		TrafficControlMeta result = (TrafficControlMeta) ctx
				.getTrafCtrlMetadata();
		if (result == null) {
			result = new TrafficControlMeta();
			ctx.setTrafCtrlMetadata(result);
		}
		return result;
	}

	static class TrafficControlMeta {
		long nextFreeSendingTime = -1;

		long nextFreeArrivingTime = -1;

		public long getNextFreeSendingTime() {
			return nextFreeSendingTime;
		}

		public void setNextFreeSendingTime(long nextFreeSendingTime) {
			this.nextFreeSendingTime = nextFreeSendingTime;
		}

		public long getNextFreeArrivingTime() {
			return nextFreeArrivingTime;
		}

		public void setNextFreeArrivingTime(long nextFreeArrivingTime) {
			this.nextFreeArrivingTime = nextFreeArrivingTime;
		}
	}

	@Override
	public void writeBackToXML(BackWriter bw) {
		// No simple/complex types to write back
	}

}
