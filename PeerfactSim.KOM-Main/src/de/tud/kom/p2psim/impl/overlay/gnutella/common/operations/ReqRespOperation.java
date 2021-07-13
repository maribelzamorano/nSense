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


package de.tud.kom.p2psim.impl.overlay.gnutella.common.operations;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.api.transport.TransMessageListener;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.AbstractGnutellaLikeNode;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.AbstractGnutellaMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.messages.SeqMessage;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;

/**
 * Abstract implementation of a request/response message exchange.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 *
 * @param &lt;Result&gt;: the type of the result returned
 * @version 05/06/2011
 */
public abstract class ReqRespOperation<TContact extends GnutellaLikeOverlayContact, TNode extends AbstractGnutellaLikeNode, Result> extends
		AbstractOperation<TNode, Result> {

	private TNode component;

	private TContact to;

	int seqNr;

	private Listener listener = null;

	Timeout timeout;

	public ReqRespOperation(TNode component,
			TContact to, OperationCallback<Result> callback) {
		super(component, callback);
		this.component = component;
		this.to = to;
	}

	@Override
	protected void execute() {

		TransLayer trans = component.getHost().getTransLayer();

		if (listener != null)
			trans.removeTransMsgListener(listener, component.getPort());

		seqNr = getNewSequenceNumber();

		SeqMessage request = createReqMessage();
		request.setSeqNumber(seqNr);

		trans.send(request, to.getTransInfo(), to.getTransInfo().getPort(),
				TransProtocol.UDP);
		trans.addTransMsgListener(listener = this.new Listener(), component
				.getPort());

		timeout = this.new Timeout();
		timeout.scheduleWithDelay(getTimeout());
	}
	
	protected int getNewSequenceNumber() {
		return component.getNewSequenceNumber();
	}

	/**
	 * Returns the sequence number used by this operation
	 * @return
	 */
	protected int getSequenceNumber() {
		return seqNr;
	}

	/**
	 * Creates the request message sent to the receiver at the beginning.
	 * @return
	 */
	protected abstract SeqMessage createReqMessage();

	/**
	 * Returns the timeout after which this operation is stopped unsuccessfully.
	 * @return
	 */
	protected abstract long getTimeout();

	/**
	 * Returns the receiver of the request
	 * @return
	 */
	public TContact getTo() {
		return to;
	}

	class Listener implements TransMessageListener {

		@Override
		public void messageArrived(TransMsgEvent receivingEvent) {
			Message msg = receivingEvent.getPayload();
			if (msg instanceof SeqMessage) {
				SeqMessage resp = (SeqMessage) msg;
				if (resp.getSeqNumber() == seqNr) {
					if (gotResponse(resp)) {
						new StopListening().scheduleAtTime(0); // TODO:
																// Workaround
					}
				}
			}

		}

	}

	/**
	 * Stops listening for a reply
	 */
	public void stopListening() {
		component.getHost().getTransLayer().removeTransMsgListener(listener,
				component.getPort());
	}

	public class Timeout implements SimulationEventHandler {

		boolean listeningStopped = false;

		public void scheduleWithDelay(long delay) {
			long time = Simulator.getCurrentTime() + delay;
			scheduleAtTime(time);
		}

		public void scheduleAtTime(long time) {
			time = Math.max(time, Simulator.getCurrentTime());
			Simulator.scheduleEvent(this, time, this,
					SimulationEvent.Type.TIMEOUT_EXPIRED);
		}

		@Override
		public void eventOccurred(SimulationEvent se) {
			if (!listeningStopped) {
				stopListening();
				timeout.listeningStopped = true;
				timeoutOccured();
			}
		}

	}

	/**
	 * Workaround to avoid ConcurrentModificationException while the
	 * TransMsgListeners are called.
	 * 
	 * @author
	 * 
	 */
	public class StopListening implements SimulationEventHandler {

		public void scheduleWithDelay(long delay) {
			long time = Simulator.getCurrentTime() + delay;
			scheduleAtTime(time);
		}

		public void scheduleAtTime(long time) {
			time = Math.max(time, Simulator.getCurrentTime());
			Simulator.scheduleEvent(this, time, this,
					SimulationEvent.Type.TIMEOUT_EXPIRED);
		}

		@Override
		public void eventOccurred(SimulationEvent se) {
			stopListening();
			timeout.listeningStopped = true;
		}

	}

	/**
	 * Returns true if the response was the correct one and the handler shall
	 * stop listening.
	 * 
	 * @param response
	 * @return
	 */
	protected abstract boolean gotResponse(SeqMessage response);

	/**
	 * Called when the timeout for the reply occured.
	 */
	protected abstract void timeoutOccured();
	
	/**
	 * Simply sends a message.
	 * @param msg
	 * @param to
	 */
	protected void sendMessage(AbstractGnutellaMessage msg, GnutellaLikeOverlayContact to) {
		TransInfo info = to.getTransInfo();
		getComponent().getHost().getTransLayer().send(msg, info,  component.getPort(), TransProtocol.UDP);
	}

}
