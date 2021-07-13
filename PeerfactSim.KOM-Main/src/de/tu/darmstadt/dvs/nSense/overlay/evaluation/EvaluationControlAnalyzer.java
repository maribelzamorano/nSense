/*
 * Copyright (c) 2012-2013 Open Source Community - <http://www.peerfact.org>
 * Copyright (c) 2011-2012 University of Paderborn - UPB
 * Copyright (c) 2005-2011 KOM - Multimedia Communications Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package de.tu.darmstadt.dvs.nSense.overlay.evaluation;

import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import de.tu.darmstadt.dvs.nSense.application.IDONodeNDim;
import de.tu.darmstadt.dvs.nSense.application.IDOOracleNDim;
import de.tud.kom.p2psim.api.analyzer.Analyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;
import de.tud.kom.p2psim.impl.util.oracle.GlobalOracle;

/**
 * This class functions as analyzer to collect relevant data during a
 * simulation. At the moment its main focus lies on the gathering of netlayer
 * data, such as a history of all send and received messages.
 * 
 * @author Christoph Muenker
 * @author Maribel Zamorano
 * @version 12/20/2013
 */
public class EvaluationControlAnalyzer implements NetAnalyzer, Analyzer {
	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger
			.getLogger(EvaluationControlAnalyzer.class);

	/**
	 * Generate the metrics in time intervals and write out
	 */
	private StatisticGenerationEvent event;

	/**
	 * The time interval to generate the metrics.
	 */
	private long measurementInterval;

	/**
	 * A data structure for incoming messages per peer
	 */
	private LinkedHashMap<OverlayID, LinkedList<Message>> receivedMsgsPerPeer = new LinkedHashMap<OverlayID, LinkedList<Message>>();

	/**
	 * A data structure for outgoing messages per peer
	 */
	private LinkedHashMap<OverlayID, LinkedList<Message>> sentMsgsPerPeer = new LinkedHashMap<OverlayID, LinkedList<Message>>();

	/**
	 * The IDO oracle for the IDO, that can be used to determined the ideal set
	 * of neighbors
	 */
	private IDOOracleNDim oracle;

	/**
	 * Flag, if the analyzer is active.
	 */
	private boolean start = false;

	/**
	 * Starts the generation to sample the metrics.
	 */
	@Override
	public void start() {
		event = new StatisticGenerationEvent(measurementInterval, this, oracle);
		this.start = true;
		event.writerStarted();
		event.scheduleImmediatly();
	}

	/**
	 * Stops the generation to sample the metrics.
	 */
	@Override
	public void stop(Writer output) {
		this.start = false;
		event.writerStopped();
	}

	@Override
	public void netMsgSend(NetMessage netMsg, NetID netId) {
		if (start) {
			processNetMsg(netMsg, netId, sentMsgsPerPeer);
		}
	}

	@Override
	public void netMsgReceive(NetMessage netMsg, NetID netId) {
		if (start) {
			processNetMsg(netMsg, netId, receivedMsgsPerPeer);
		}
	}

	/**
	 * Stores the netlayer message in the given datastructure.
	 * 
	 * @param netMsg
	 *            The message from the netlayer.
	 * @param netId
	 *            The netID of the sender/receiver.
	 * @param sentMsgsPerPeer2
	 *            The datastructure, that stores the message for peer.
	 */
	private static void processNetMsg(NetMessage netMsg, NetID netId,
			LinkedHashMap<OverlayID, LinkedList<Message>> sentMsgsPerPeer2) {
		if (netMsg != null) {
			Message transMsg = netMsg.getPayload();
			if (transMsg != null) {
				Message olMsg = transMsg.getPayload();
				if (olMsg != null) {

					OverlayNode node = GlobalOracle.getHostForNetID(netId)
							.getOverlay(IDONodeNDim.class);
					if (node != null) {
						OverlayID<?> recID = node.getOverlayID();

						if (!sentMsgsPerPeer2.containsKey(recID)) {
							sentMsgsPerPeer2.put(recID,
									new LinkedList<Message>());
						}
						sentMsgsPerPeer2.get(recID).add(netMsg);
					}
				}
			}
		}
	}

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		// until now, not used
	}

	/**
	 * Gets the received messages per peer.
	 * 
	 * @return per Peer the received messages.
	 */
	public LinkedHashMap<OverlayID, LinkedList<Message>> getReceivedMsgsPerPeer() {
		return receivedMsgsPerPeer;
	}

	/**
	 * Gets the sent messages per peer.
	 * 
	 * @return per peer the sent messages.
	 */
	public LinkedHashMap<OverlayID, LinkedList<Message>> getSentMsgsPerPeer() {
		return sentMsgsPerPeer;
	}

	/**
	 * Sets the IDO oracle
	 * 
	 * @param oracle
	 *            An instance of the IDO oracle.
	 */
	public void setIDOOracleNDim(IDOOracleNDim oracle) {
		this.oracle = oracle;
	}

	/**
	 * Sets the measurement interval.
	 * 
	 * @param interval
	 *            The time interval between two samples.
	 */
	public void setMeasurementInterval(long interval) {
		this.measurementInterval = interval;
	}

}
