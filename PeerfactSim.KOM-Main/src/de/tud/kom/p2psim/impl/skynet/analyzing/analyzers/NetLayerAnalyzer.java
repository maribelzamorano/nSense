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


package de.tud.kom.p2psim.impl.skynet.analyzing.analyzers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.Constants;
import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.api.service.skynet.SkyNetConstants;
import de.tud.kom.p2psim.impl.network.IPv4NetID;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.skynet.analyzing.AbstractSkyNetAnalyzer;
import de.tud.kom.p2psim.impl.skynet.analyzing.analyzers.postProcessing.NetLayerPostProcessor;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class performs two tasks, which are explained in the following. Since
 * <code>NetLayerAnalyzer</code> implements {@link NetAnalyzer}, it keeps track
 * of the sent, received and dropped messages. As every SkyNet-node needs the
 * statistics of these messages, this analyzer provides every node with the
 * messages, that it sent or received during a predefined period (Out of the
 * submitted messages, the node gets its required statistics).<br>
 * Besides this functionality, <code>NetLayerAnalyzer</code> also monitors the
 * complete amount of all sent, received and dropped messages during a
 * simulation. The collected data is periodically written in files via
 * Serialization to avoid a large amount of data within a simulation (The files
 * can be found in the netLayerData-directory). The interval for the
 * Serialization is determined by <code>writeInterval</code>.<br>
 * In case of the Serialization, it is important to maintain the writing-order
 * of the objects to avoid, that the corresponding post-processor-class (
 * {@link NetLayerPostProcessor} in this case) cannot read the objects and
 * throws an exception.<br>
 * The order for the Serialization is as follows: <li>
 * <code>oos.writeObject(droppedMsgs);</code> <li>
 * <code>oos.writeObject(sentMsgs);</code> <li>
 * <code>oos.writeObject(receivedMsgs);</code>
 * 
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 */

public class NetLayerAnalyzer extends AbstractSkyNetAnalyzer implements
		NetAnalyzer {

	private static Logger log = SimLogger.getLogger(NetLayerAnalyzer.class);

	private static String DATA_PATH = Constants.TMP_DIR + File.separator
			+ "netLayerData";

	private long writeInterval = Simulator.MINUTE_UNIT * 5;

	private int sentCounter;

	private long sentSize;

	private int recCounter;

	private long recSize;

	private int dropCounter;

	private long dropSize;

	private HashMap<Long, Vector<NetLayerAnalyzerEntry>> droppedMsgs;

	private HashMap<Long, Vector<NetLayerAnalyzerEntry>> sentMsgs;

	private HashMap<Long, Vector<NetLayerAnalyzerEntry>> receivedMsgs;

	private HashMap<Long, Vector<NetMessage>> deltaReceivedMsgs;

	private HashMap<Long, Vector<NetMessage>> deltaSentMsgs;

	private static IPv4NetID serverNetId;

	public NetLayerAnalyzer() {
		super();
		sentCounter = 0;
		sentSize = 0;
		recCounter = 0;
		recSize = 0;
		dropCounter = 0;
		dropSize = 0;
	}

	@Override
	protected void initialize() {
		Simulator.scheduleEvent(null,
				writeInterval + Simulator.getCurrentTime(), this,
				SimulationEvent.Type.STATUS);
		initWriteDirectory(DATA_PATH, true);
	}

	private void printSum() {
		log.fatal("SentCounter = " + sentCounter);
		log.fatal("SentCounter = " + sentSize);
		log.fatal("recCoutner = " + recCounter);
		log.fatal("recCoutner = " + recSize);
		log.fatal("DropCounter = " + dropCounter);
		log.fatal("DropCounter = " + dropSize);
	}

	@Override
	protected void finish() {
		long time = Simulator.getCurrentTime();

		// writing down the data-Maps
		long delta = System.currentTimeMillis();
		File f = new File(DATA_PATH + File.separatorChar + "temp-"
				+ (time / SkyNetConstants.DIVISOR_FOR_SECOND) + ".dat");
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(f));
			log.warn("@ " + Simulator.getFormattedTime(time)
					+ " Started to write the maps");
			oos.writeObject(droppedMsgs);
			oos.writeObject(sentMsgs);
			oos.writeObject(receivedMsgs);
			oos.close();
			log.warn("@ " + Simulator.getFormattedTime(time)
					+ " Finished to write the maps in "
					+ (System.currentTimeMillis() - delta) + "ms");
			droppedMsgs.clear();
			sentMsgs.clear();
			receivedMsgs.clear();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		printSum();
	}

	// method for generating data of dropped msgs
	public void netMsgDrop(NetMessage msg, NetID id) {
		if (runningAnalyzer && compareIPWithServerIP(id)) {
			dropCounter++;
			dropSize += msg.getSize();
			long timestamp = Simulator.getCurrentTime();
			IPv4NetID ip = (IPv4NetID) id;

			Vector<NetLayerAnalyzerEntry> vec = droppedMsgs.remove(ip.getID());
			if (vec == null) {
				vec = new Vector<NetLayerAnalyzerEntry>();
			}
			Message overlayMsg = msg.getPayload().getPayload();
			Class c = overlayMsg.getClass();
			long size = msg.getSize();
			vec.add(new NetLayerAnalyzerEntry(timestamp, c, size));
			droppedMsgs.put(ip.getID(), vec);
		}
	}

	// method for generating data of received msgs
	public void netMsgReceive(NetMessage msg, NetID id) {
		if (runningAnalyzer && compareIPWithServerIP(id)) {
			long timestamp = Simulator.getCurrentTime();
			IPv4NetID ip = (IPv4NetID) id;
			recCounter++;
			recSize += msg.getSize();
			// add the received message to the message-vector of the
			// receiving peer
			if (deltaReceivedMsgs.containsKey(ip.getID())) {
				Vector<NetMessage> vec = deltaReceivedMsgs.remove(ip.getID());
				vec.add(msg);
				deltaReceivedMsgs.put(ip.getID(), vec);
			} else {
				Vector<NetMessage> vec = new Vector<NetMessage>();
				vec.add(msg);
				deltaReceivedMsgs.put(ip.getID(), vec);
			}

			Vector<NetLayerAnalyzerEntry> vec = receivedMsgs.remove(ip.getID());
			if (vec == null) {
				vec = new Vector<NetLayerAnalyzerEntry>();
			}
			Message overlayMsg = msg.getPayload().getPayload();
			Class c = overlayMsg.getClass();
			long size = msg.getSize();
			vec.add(new NetLayerAnalyzerEntry(timestamp, c, size));
			receivedMsgs.put(ip.getID(), vec);
		}
	}

	// method for generating data of sent msgs
	public void netMsgSend(NetMessage msg, NetID id) {
		if (runningAnalyzer && compareIPWithServerIP(id)) {
			long timestamp = Simulator.getCurrentTime();
			IPv4NetID ip = (IPv4NetID) id;
			sentCounter++;
			sentSize += msg.getSize();
			// add the sent message to the message-vector of the sending
			// peer
			if (deltaSentMsgs.containsKey(ip.getID())) {
				Vector<NetMessage> vec = deltaSentMsgs.remove(ip.getID());
				vec.add(msg);
				deltaSentMsgs.put(ip.getID(), vec);
			} else {
				Vector<NetMessage> vec = new Vector<NetMessage>();
				vec.add(msg);
				deltaSentMsgs.put(ip.getID(), vec);
			}

			Vector<NetLayerAnalyzerEntry> vec = sentMsgs.remove(ip.getID());
			if (vec == null) {
				vec = new Vector<NetLayerAnalyzerEntry>();
			}
			Message overlayMsg = msg.getPayload().getPayload();
			Class c = overlayMsg.getClass();
			long size = msg.getSize();
			vec.add(new NetLayerAnalyzerEntry(timestamp, c, size));
			sentMsgs.put(ip.getID(), vec);
		}
	}

	// ----------------------------------------------------------------------
	// methods for getting the collected data of this analyzer
	// ----------------------------------------------------------------------

	/**
	 * This method returns all messages, which the specified host sent during a
	 * predefined interval.
	 * 
	 * @param id
	 *            contains the {@link NetID} of the host, for which all sent
	 *            messages shall be returned
	 * @return a <code>Vector</code> with all sent messages
	 */
	public Vector<NetMessage> getSentMsgs(NetID id) {
		IPv4NetID ip = (IPv4NetID) id;
		return deltaSentMsgs.remove(ip.getID());
	}

	/**
	 * This method returns all messages, which the specified host received
	 * during a predefined interval.
	 * 
	 * @param id
	 *            contains the {@link NetID} of the host, for which all received
	 *            messages shall be returned
	 * @return a <code>Vector</code> with all received messages
	 */
	public Vector<NetMessage> getReceivedMsgs(NetID id) {
		IPv4NetID ip = (IPv4NetID) id;
		return deltaReceivedMsgs.remove(ip.getID());
	}

	public void setSimulationSize(int size) {
		double capacity = Math.ceil(size / 0.75d);
		droppedMsgs = new HashMap<Long, Vector<NetLayerAnalyzerEntry>>(
				(int) capacity);
		sentMsgs = new HashMap<Long, Vector<NetLayerAnalyzerEntry>>(
				(int) capacity);
		receivedMsgs = new HashMap<Long, Vector<NetLayerAnalyzerEntry>>(
				(int) capacity);
		deltaReceivedMsgs = new HashMap<Long, Vector<NetMessage>>(
				(int) capacity);
		deltaSentMsgs = new HashMap<Long, Vector<NetMessage>>((int) capacity);
	}

	public void eventOccurred(SimulationEvent se) {
		if (se.getType().equals(SimulationEvent.Type.STATUS)) {
			long time = Simulator.getCurrentTime();

			// writing down the data-Maps
			long delta = System.currentTimeMillis();
			File f = new File(DATA_PATH + File.separatorChar + "temp-"
					+ (time / SkyNetConstants.DIVISOR_FOR_SECOND) + ".dat");
			try {
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(f));
				log.warn("@ " + Simulator.getFormattedTime(time)
						+ " Started to write the maps");
				oos.writeObject(droppedMsgs);
				oos.writeObject(sentMsgs);
				oos.writeObject(receivedMsgs);
				oos.close();
				log.warn("@ " + Simulator.getFormattedTime(time)
						+ " Finished to write the maps in "
						+ (System.currentTimeMillis() - delta) + "ms");
				droppedMsgs.clear();
				sentMsgs.clear();
				receivedMsgs.clear();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Simulator.scheduleEvent(null, writeInterval + time, this,
					SimulationEvent.Type.STATUS);
		}
	}

	/**
	 * If a centralized server is used within the overlay, this server is not
	 * included in the analyzing-process. Therefore, this method is used to
	 * determine the {@link NetID} of the server in order to exclude him.
	 * 
	 * @param serverID
	 *            contains the IP-address of the server
	 */
	public static void setServerNetId(IPv4NetID serverID) {
		serverNetId = serverID;
	}

	private boolean compareIPWithServerIP(NetID id) {
		if (serverNetId != null) {
			if (((IPv4NetID) id).getID() != serverNetId.getID()) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

}
