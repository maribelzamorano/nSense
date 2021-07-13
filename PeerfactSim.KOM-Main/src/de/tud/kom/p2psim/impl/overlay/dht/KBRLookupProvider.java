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

import java.util.HashMap;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.KBR;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayKey;

/**
 * This abstract class augments the original <code>KBR</code> interface with a
 * generic key lookup mechanism based on the interface's methods.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <T>
 *            the overlay's implementation of the <code>OverlayID</code>
 * @param <S>
 *            the overlay's implementation of the <code>OvrlayContact</code>
 * @version 05/06/2011
 */
public class KBRLookupProvider<T extends OverlayID, S extends OverlayContact<T>> {

	private HashMap<Integer, KBRLookupOperation<T, S>> openLookupRequest = new HashMap<Integer, KBRLookupOperation<T, S>>();

	private KBR<T, S> kbrNode;

	/**
	 * @param kbrNode
	 *            the <code>KBR</code> node that belongs to the instance of this
	 *            class and wants to use the lookup functionality.
	 */
	public KBRLookupProvider(KBR<T, S> kbrNode) {
		this.kbrNode = kbrNode;
	}

	/**
	 * This method enables a lookup of the node responsible for an
	 * <code>OverlayKey</code>. To perform the lookup the possibility of
	 * <code>KBR</code> compatible overlays are used. The result of the lookup
	 * is handed over to the caller through the given callback.
	 * 
	 * @param key
	 *            the key to be looked up
	 * @param callback
	 *            the callback for handing over the result of the lookup
	 */
	public void lookupKey(OverlayKey key, OperationCallback<S> callback) {

		KBRLookupOperation<T, S> op = new KBRLookupOperation<T, S>(kbrNode,
				key, callback);
		op.scheduleImmediately();

		/*
		 * Add this request to the list of open requests
		 */
		openLookupRequest.put(op.getOperationID(), op);

	}

	@SuppressWarnings("unchecked")
	protected void lookupRequestArrived(KBRLookupMsg<T, S> msg) {

		S olContact = msg.getSenderContact();

		KBRLookupReplyMsg<T, S> replyMsg = new KBRLookupReplyMsg<T, S>(
				(S) kbrNode.getLocalOverlayContact(), msg.getOperationID());

		/*
		 * Route the reply direct to the sender by using the method "route" and
		 * giving <code>null</code> as key and the senders contact as hint. This
		 * results in a direct message.
		 */
		kbrNode.route(null, replyMsg, olContact);
	}

	protected void lookupReplyArrived(KBRLookupReplyMsg<T, S> msg) {

		S olContact = msg.getSenderContact();

		KBRLookupOperation<T, S> pendingOp = openLookupRequest.get(msg
				.getOperationID());

		if (pendingOp != null)
			pendingOp.answerArrived(olContact);
	}

}
