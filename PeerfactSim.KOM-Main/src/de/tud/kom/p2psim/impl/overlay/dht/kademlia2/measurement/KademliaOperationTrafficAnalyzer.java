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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.measurement;

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.measurement.KademliaAnalyzers.KademliaMessageTrafficAnalyzer;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations.AbstractKademliaOperation;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.HKademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.messages.KademliaMsg;

/**
 * Analyses the messages sent and received by an AbstractKademliaOperation and
 * distinguishes by the target/source cluster.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class KademliaOperationTrafficAnalyzer extends FileAnalyzer implements
		KademliaMessageTrafficAnalyzer {

	// /** The expected number of characters per output file line. */
	// private static final int APPROX_CHARS_PER_LINE = 100;
	//
	// /** The expected number of lines. */
	// private static final int APPROX_NUM_OF_LINES = 1000; // 000;

	/**
	 * An approximate of the number of operations that are concurrently running.
	 */
	private static final int APPROX_CONCURRENT_OPERATIONS = 40;

	/**
	 * A Map that contains for each operation-ID a Map with cluster IDs as keys
	 * and the number of sent messages as values.
	 */
	private Map<Integer, Map<BigInteger, Integer>> runningOps;

	// public KademliaOperationTrafficAnalyzer() {
	// super(APPROX_CHARS_PER_LINE * APPROX_NUM_OF_LINES);
	// }

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void started() {
		runningOps = new HashMap<Integer, Map<BigInteger, Integer>>(
				APPROX_CONCURRENT_OPERATIONS);
		appendToFile("# OPERATION_ID OWN_CLUSTER (CLUSTER_ID=NUMBER_OF_SENT_MESSAGES)*");
		appendNewLine();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void operationInitiated(final AbstractKademliaOperation op) {
		if (!isStarted()) {
			return;
		}
		runningOps.put(op.getOperationID(), new HashMap<BigInteger, Integer>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void messageSent(final KademliaMsg<?> msg,
			final AbstractKademliaOperation op) {
		if (!isStarted()) {
			return;
		}
		final Map<BigInteger, Integer> sentMsgs = runningOps.get(op
				.getOperationID());
		if (sentMsgs == null) {
			return; // operation unknown
		}
		final BigInteger destCluster = ((KademliaMsg<HKademliaOverlayID>) msg)
				.getDestination().getCluster();
		Integer sentSoFar = sentMsgs.get(destCluster);
		if (sentSoFar == null) {
			sentSoFar = 1;
		} else {
			sentSoFar++;
		}
		sentMsgs.put(destCluster, sentSoFar);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void operationFinished(final AbstractKademliaOperation op) {
		if (!isStarted()) {
			return;
		}
		final Map<BigInteger, Integer> sentMsgs = runningOps.remove(op
				.getOperationID());
		if (sentMsgs == null) {
			return; // operation unknown
		}
		appendToFile(op.getOperationID());
		appendSeparator();
		appendClusterID(((HKademliaOverlayID) op.getComponent()
				.getTypedOverlayID()).getCluster());
		appendSeparator();
		for (final Map.Entry<BigInteger, Integer> entry : sentMsgs.entrySet()) {
			appendClusterID(entry.getKey());
			appendToFile('=');
			appendToFile(entry.getValue());
			appendToFile(", ");
		}
		appendNewLine();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void stopped(final Writer output) throws IOException {
		output.write(" -- KademliaOperationTrafficAnalyzer: ");
		output.write("see output file. --\n");
	}

}
