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

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.KBR;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This <code>Operation</code> is used to perform lookups for keys within a
 * <code>KBR</code> overlay based on the route functionality of <code>KBR</code>
 * . The use of this operation is encapsulated in the class
 * <code>KBRLookupProvider</code> which should be used as interface for all the
 * lookups based on <code>KBR</code>.
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @param <T>
 *            the overlay's implementation of the <code>OverlayID</code>
 * @param <S>
 *            the overlay's implementation of the <code>OvrlayContact</code>
 * @version 05/06/2011
 */
public class KBRLookupOperation<T extends OverlayID, S extends OverlayContact<T>>
		extends AbstractOperation<KBR<T, S>, S> {

	private final static Logger log = SimLogger
			.getLogger(KBRLookupOperation.class);

	/**
	 * The timeout for the lookup operation
	 */
	private final long TIMEOUT = 10 * Simulator.SECOND_UNIT;

	/**
	 * The <code>KBR</code> node that owns the instance of the operation
	 */
	private KBR<T, S> kbrNode;

	/**
	 * The <code>OverlayKey</code> that is looked up with this lookup operation
	 */
	private OverlayKey keyToLookup;

	/**
	 * The result of the lookup. It is <code>null</code> until the operation is
	 * finished
	 */
	private S resultOfLookup;

	protected KBRLookupOperation(KBR<T, S> kbrNode, OverlayKey key,
			OperationCallback<S> callback) {
		super(kbrNode, callback);

		this.kbrNode = kbrNode;
		this.keyToLookup = key;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void execute() {
		scheduleOperationTimeout(TIMEOUT);

		/*
		 * Generate <code>KBRLookupLeyMsg</code> and route it towards key
		 */
		KBRLookupMsg<T, S> lookupMsg = new KBRLookupMsg<T, S>((S) kbrNode
				.getLocalOverlayContact(), this.getOperationID());

		kbrNode.route(keyToLookup, lookupMsg, null);

		log.debug(kbrNode.getOverlayID()
				+ " - Lookup for key within KBR was executed.");
	}

	@Override
	public S getResult() {
		return resultOfLookup;
	}

	protected void answerArrived(S result) {
		resultOfLookup = result;
		operationFinished(true);

		log.debug(kbrNode.getOverlayID()
				+ " - The result of a key lookup within KBR has arrived.");
	}

}
