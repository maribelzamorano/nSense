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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.scenario.Configurable;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.operations.LookupBegin;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordBootstrapManager;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordConfiguration;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordID;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordIDFactory;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.operations.LookupOperation;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.util.ChordOverlayUtil;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class is used to periodically start random lookup request
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class LookupGenerator implements Configurable {

	private static Logger log = SimLogger.getLogger(LookupGenerator.class);

	protected static ArrayList<Integer> lookupList = new ArrayList<Integer>();

	/**
	 * [0] : sum of lookup [1] : failed/loss lookup [2] : receive reply lookup
	 * [3] : correct lookup
	 */
	protected static int[] lookupCount = new int[4];

	public void startRandomLookup(ChordNode starter) {
		log.debug("lookup counter " + Arrays.toString(lookupCount));
		lookupCount[0]++;
		long randomLong = Simulator.getRandom().nextLong();
		String token = Long.toString(Math.abs(randomLong), 32);
		ChordID key = ChordIDFactory.getInstance().getChordID(token);
		int lookupID = starter.overlayNodeLookup(key, new MyCallback());

		log.debug("started lookup request key = " + key + " id = " + lookupID
				+ " from = " + starter);
		lookupList.add(lookupID);
	}

	public void setStart(long start) {

		new LookupBegin().scheduleWithDelay(start);
	}

	class MyCallback implements OperationCallback<List<ChordContact>> {

		@Override
		public void calledOperationFailed(Operation op) {
			// do nothing
			lookupCount[1]++;
		}

		@Override
		public void calledOperationSucceeded(Operation op) {
			// do nothing
			lookupCount[2]++;
			LookupOperation lookupOp = (LookupOperation) op;
			Integer lookup = new Integer(lookupOp.getLookupId());
			lookupList.remove(lookup);
			log.trace("unfinished lookup "
					+ Arrays.toString(lookupList.toArray()));
			ChordID target = lookupOp.getTarget();
			ChordID result = lookupOp.getResult().get(0).getOverlayID();
			if (ChordConfiguration.DO_CHORD_EVALUATION)
				analyzeLookupResult(target, result);
		}

	}

	protected void analyzeLookupResult(ChordID target, ChordID result) {
		ChordNode responder = ChordOverlayUtil.getResponsibleNode(
				ChordBootstrapManager.getInstance(target).getAvailableNodes(),
				target.getValue());
		boolean valid = responder.getOverlayID().equals(result);
		if (!valid) {
			log.error("incorrect lookup result" + " key = " + target
					+ " correct responder " + responder + " found = " + result);
		} else {
			// +1 correct lookup
			lookupCount[3]++;
		}
	}

	public static boolean containLookupId(int lookupId) {

		return lookupList.contains(lookupId);
	}

}
