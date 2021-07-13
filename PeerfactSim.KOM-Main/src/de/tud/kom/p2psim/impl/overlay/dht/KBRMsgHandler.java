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


package de.tud.kom.p2psim.impl.overlay.dht;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.KBR;
import de.tud.kom.p2psim.api.overlay.KBRForwardInformation;
import de.tud.kom.p2psim.api.overlay.KBRListener;
import de.tud.kom.p2psim.api.overlay.KBRLookupMessage;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.api.transport.TransMessageListener;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This is the message listener for KBR layers
 * 
 * @author Yue Sheng (improved by Julius Rueckert) <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <T>
 *            the overlay's implementation of the <code>OverlayID</code>
 * @param <S>
 *            the overlay's implementation of the <code>OvrlayContact</code>
 * 
 * @version 05/06/2011
 */
public class KBRMsgHandler<T extends OverlayID, S extends OverlayContact<T>>
		implements TransMessageListener {

	private final static Logger log = SimLogger.getLogger(KBRMsgHandler.class);

	private final KBR<T, S> masterAsKBR;

	private final AbstractOverlayNode masterAsOverlayNode;

	private final List<KBRListener> listeners = new LinkedList<KBRListener>();

	private KBRLookupProvider<T, S> lookupProvider;

	/**
	 * Info: If you want to create an instance of this class inside an overlay
	 * node which inherits from AbstractOverlayNode and realizes the interface
	 * KBR you have to pass for the first and second parameter "this".
	 * 
	 * @param kbr
	 * @param overlayNode
	 * @param listener
	 */
	public KBRMsgHandler(KBR<T, S> kbr, AbstractOverlayNode overlayNode,
			KBRListener listener) {
		this.masterAsKBR = kbr;
		this.masterAsOverlayNode = overlayNode;
		listeners.add(listener);

		/*
		 * Register the handler at the transport layer
		 */
		overlayNode.getHost().getTransLayer()
				.addTransMsgListener(this, overlayNode.getPort());
	}

	@Override
	public void messageArrived(TransMsgEvent receivingEvent) {
		Message msg = receivingEvent.getPayload();
		if (msg instanceof ForwardMsg) {
			ForwardMsg fm = (ForwardMsg) msg;
			OverlayKey key = fm.getKey();
			Message appMsg = fm.getPayload();
			OverlayContact nextHop = null;
			int numberOfHops = fm.getHops();
			numberOfHops++;

			/*
			 * Inform the KBR node about the direct contact to another node
			 */
			masterAsKBR.hadContactTo(masterAsKBR.getOverlayContact(
					fm.getSender(), receivingEvent.getSenderTransInfo()));

			if (key == null || masterAsKBR.isRootOf(key)) {

				if (appMsg instanceof KBRLookupMessage) {
					/*
					 * In this case the message contains to a lookup operation
					 * for the node responsible for a key. This message is
					 * delivered to the <code>KBRLookupProvider</code> that
					 * handles the lookup procedure.
					 */

					if (lookupProvider == null)
						lookupProvider = new KBRLookupProvider<T, S>(
								masterAsKBR);

					if (appMsg instanceof KBRLookupMsg) {
						lookupProvider
								.lookupRequestArrived((KBRLookupMsg<T, S>) appMsg);
					} else if (appMsg instanceof KBRLookupReplyMsg) {
						lookupProvider
								.lookupReplyArrived((KBRLookupReplyMsg<T, S>) appMsg);
					}

				} else {

					/*
					 * Deliver the message to the application if the key is null
					 * (it is a direct message to that node) or the node is the
					 * root of the key
					 */
					for (KBRListener listener : listeners) {
						listener.deliver(key, appMsg);
					}

					if (key != null)
						log.debug("Delivered a query that was routed towards a key.");
					/*
					 * Inform monitors about delivery
					 */
					Simulator.getMonitor().overlayMessageDelivered(
							masterAsKBR.getLocalOverlayContact(), msg,
							numberOfHops);
				}
				return;
			} else {
				List<S> posNextHops = masterAsKBR.local_lookup(key, 1);
				if (posNextHops.size() > 0) {
					nextHop = posNextHops.get(0);

					if (nextHop != null) {
						KBRForwardInformation info = new KBRForwardInformationImpl(key, appMsg, nextHop);
						for (KBRListener listener : listeners) {
							listener.forward(info);
						}
						key = info.getKey();
						appMsg = info.getMessage();
						nextHop = info.getNextHopAgent();
						
						if (nextHop != null) {
							fm = new ForwardMsg(masterAsOverlayNode.getOverlayID(),
									nextHop.getOverlayID(), key, appMsg,
									numberOfHops);
							masterAsOverlayNode.getTransLayer().send(fm,
									nextHop.getTransInfo(),
									masterAsOverlayNode.getPort(),
									TransProtocol.UDP);

							/*
							 * Inform monitors about forwarding
							 */
							Simulator.getMonitor().overlayMessageForwarded(
									masterAsKBR.getLocalOverlayContact(), nextHop,
									msg, numberOfHops);
						}
					}
				} else {
					/*
					 * No next hop could be determined. The message is dropped.
					 */
					Simulator.getMonitor().queryFailed(
							masterAsKBR.getLocalOverlayContact(), appMsg);
				}
			}
		}
	}

	/**
	 * Add a <code>KBRListener</code> to the list of listeners.
	 * 
	 * @param listener
	 */
	public void addKBRListener(KBRListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a <code>KBRListener</code> to the list of listeners.
	 * 
	 * @param listener
	 */
	public void removeKBRListener(KBRListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return the lookup provider, to perform lookups of keys
	 */
	public KBRLookupProvider<T, S> getLookupProvider() {
		if (lookupProvider == null)
			lookupProvider = new KBRLookupProvider<T, S>(masterAsKBR);

		return lookupProvider;
	}

}
