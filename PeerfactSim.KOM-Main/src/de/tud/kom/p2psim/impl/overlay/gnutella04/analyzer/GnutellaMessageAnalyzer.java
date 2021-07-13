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


package de.tud.kom.p2psim.impl.overlay.gnutella04.analyzer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.tud.kom.p2psim.api.analyzer.Analyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.TransAnalyzer;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.ConnectMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.OkMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.PingMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.PongMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.PushMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.QueryHitMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.QueryMessage;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.AbstractTransMessage;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class GnutellaMessageAnalyzer implements Analyzer, TransAnalyzer {

	private Map<BigInteger, Integer> queryHopCount = new HashMap<BigInteger, Integer>();

	private Map<BigInteger, Integer> queryHitHopCount = new HashMap<BigInteger, Integer>();

	private Map<BigInteger, Integer> queryHitHopsNeededByQuery = new HashMap<BigInteger, Integer>();

	private List<BigInteger> queryIds = new LinkedList<BigInteger>();

	private List<BigInteger> queryHitIds = new LinkedList<BigInteger>();

	private List<BigInteger> queriesFailed = new LinkedList<BigInteger>();

	private static double connectCounter = 0;

	private static double okCounter = 0;

	private static double pingCounter = 0;

	private static double pongCounter = 0;

	private static double pushCounter = 0;

	private static double queryCounter = 0;

	private static double queryHitCounter = 0;

	private static double connectGesendetCounter = 0;

	private static double okGesendetCounter = 0;

	private static double pingGesendetCounter = 0;

	private static double pongGesendetCounter = 0;

	private static double pushGesendetCounter = 0;

	private static double queryGesendetCounter = 0;

	private static double queryHitGesendetCounter = 0;

	private static double totalMessagesGesendetCounter = 0;

	private static double totalMessagesCounter = 0;

	private double firstCounter = 0;

	private double secCounter = 0;

	private double queryHopsAveraged = 0;

	private double hopsWhenQueryHitAveraged = 0;

	private int queryHops;

	private int queryHitHops;

	public void start() {
		// TODO Auto-generated method stub

	}

	public void stop(Writer output) {
		NumberFormat n = NumberFormat.getInstance();
		n.setMaximumFractionDigits(2);

		double queryPerCent = (100.0 * queryCounter) / totalMessagesCounter;
		double queryHitPerCent = (double) ((100 * queryHitCounter) / totalMessagesCounter);
		double pingPerCent = (double) ((100 * pingCounter) / totalMessagesCounter);
		double pongPerCent = (double) ((100 * pongCounter) / totalMessagesCounter);
		double connectPerCent = (double) ((100 * connectCounter) / totalMessagesCounter);
		double pushPerCent = (double) ((100 * pushCounter) / totalMessagesCounter);
		double okPerCent = (double) ((100 * okCounter) / totalMessagesCounter);

		for (int i = 0; i < queryIds.size(); i++) {
			BigInteger descriptor = queryIds.get(i);
			if (queryHopCount.containsKey(descriptor)) {
				queryHops += queryHopCount.get(descriptor);
				firstCounter++;
			}
		}

		for (int i = 0; i < queryHitIds.size(); i++) {
			BigInteger descriptor = queryHitIds.get(i);
			if (queryHitHopCount.containsKey(descriptor)) {
				queryHitHops += queryHitHopCount.get(descriptor);
				queryHops += queryHopCount.get(descriptor);
				secCounter++;
				firstCounter++;
			}
		}

		if (firstCounter != 0) {
			queryHopsAveraged = queryHops / firstCounter;
		}

		if (secCounter != 0) {
			hopsWhenQueryHitAveraged = queryHitHops / secCounter;
		}

		double queryNotSucceded = (double) ((100 * (double) queriesFailed
				.size()) / ((double) queryIds.size()));
		double querySucceded = (double) ((100 * (double) queryHitIds.size()) / (double) queryIds
				.size());
		int queryErfolgreich = queryIds.size() - queriesFailed.size();

		String f = "Gnutella-Analyzerausgabe2.dat";
		FileWriter fstream = null;
		try {
			fstream = new FileWriter(f);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("	Gnutella-Analyzer	\n");
			out.write("	-----------------------	\n");
			out.write("	\n");
			out.write("	\n");
			out.write("Messages gesendet: \n");
			out
					.write("Gesamt		query		queryhit	ping		pong		connect		OK			push	\n");
			out.write(totalMessagesGesendetCounter + "		"
					+ queryGesendetCounter + "		" + queryHitGesendetCounter
					+ "		" + pingGesendetCounter + "		" + pongGesendetCounter
					+ "		" + connectCounter + "		" + okGesendetCounter + "		"
					+ pushGesendetCounter + " \n");
			out.write("	\n");
			out.write("Messages empfangen: \n");
			out
					.write("Gesamt		query		queryhit	ping		pong		connect		OK			push	\n");
			out.write(totalMessagesCounter + "		" + queryCounter + "		"
					+ queryHitCounter + "		" + pingCounter + "		" + pongCounter
					+ "		" + connectCounter + "		" + okCounter + "		"
					+ pushCounter + " \n");
			out.write("			" + n.format(queryPerCent) + "		"
					+ n.format(queryHitPerCent) + "		" + n.format(pingPerCent)
					+ "		" + n.format(pongPerCent) + "		"
					+ n.format(connectPerCent) + "		" + n.format(okPerCent)
					+ "		" + n.format(pushPerCent) + " \n");
			out.write("	\n");
			out.write("Durchschnittliche Hops:			\n");
			out
					.write("Gesamt	QueryHit		querys gesendet+empfangen		queryHits		query Failed	\n");
			out.write(n.format(queryHopsAveraged) + "		"
					+ n.format(hopsWhenQueryHitAveraged) + "						"
					+ queryIds.size() + "					" + queryErfolgreich + "				"
					+ queriesFailed.size() + "	\n");
			out.write("	\n");
			out
					.write("Prozent query Erfolgreich		Prozent query gescheitert	\n");
			out.write(n.format(100 - queryNotSucceded) + "								"
					+ n.format(queryNotSucceded) + "	\n");
			out.write("	\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void transMsgReceived(AbstractTransMessage msg) {
		totalMessagesCounter++;
		Message message = msg.getPayload();

		if (message instanceof ConnectMessage) {
			connectCounter++;
		} else if (message instanceof OkMessage) {
			okCounter++;
		} else if (message instanceof PingMessage) {
			pingCounter++;
		} else if (message instanceof PongMessage) {
			pongCounter++;
		} else if (message instanceof PushMessage) {
			pushCounter++;
		} else if (message instanceof QueryMessage) {
			queryCounter++;
			QueryMessage queryMessage = (QueryMessage) message.getPayload();
			BigInteger descriptor = queryMessage.getDescriptor();
			int hops = queryMessage.getHops();
			int hopsGespeichert = 0;
			if (!queryIds.contains(descriptor)) {
				queryIds.add(descriptor);
			}
			if (!queriesFailed.contains(descriptor)) {
				queriesFailed.add(descriptor);
			}
			if (queryHopCount.containsKey(descriptor)) {
				hopsGespeichert = queryHopCount.get(descriptor);
			}
			if (hops > hopsGespeichert) {
				queryHopCount.put(descriptor, hops);
			}

		} else if (message instanceof QueryHitMessage) {
			System.out.println(Simulator.getCurrentTime());
			queryHitCounter++;
			QueryHitMessage queryHitMessage = (QueryHitMessage) message
					.getPayload();
			BigInteger descriptor = queryHitMessage.getDescriptor();
			int hops = queryHitMessage.getHops();
			int hopsGespeichert = 0;
			if (!queryHitIds.contains(descriptor)) {
				queryHitIds.add(descriptor);
			}
			if (queriesFailed.contains(descriptor)) {
				queriesFailed.remove(descriptor);
			}
			if (queryHitHopCount.containsKey(descriptor)) {
				hopsGespeichert = queryHitHopCount.get(descriptor);
			}
			if (hops > hopsGespeichert) {
				queryHitHopCount.put(descriptor, hops);
			}
			if (queryHopCount.containsKey(descriptor)) {
				int queryHops = queryHopCount.get(descriptor);
				queryHitHopsNeededByQuery.put(descriptor, queryHops);
			}

		}

	}

	public void transMsgSent(AbstractTransMessage msg) {
		totalMessagesGesendetCounter++;
		Message message = msg.getPayload();

		if (message instanceof ConnectMessage) {
			connectGesendetCounter++;
		} else if (message instanceof OkMessage) {
			okGesendetCounter++;
		} else if (message instanceof PingMessage) {
			pingGesendetCounter++;
		} else if (message instanceof PongMessage) {
			pongGesendetCounter++;
		} else if (message instanceof PushMessage) {
			pushGesendetCounter++;
		} else if (message instanceof QueryMessage) {
			queryGesendetCounter++;
		} else if (message instanceof QueryHitMessage) {
			queryHitGesendetCounter++;
		}
	}

}
