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


package de.tud.kom.p2psim.impl.service.aggr.gossip;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.LiveMonitoring;
import de.tud.kom.p2psim.impl.util.LiveMonitoring.ProgressValue;
import de.tud.kom.p2psim.impl.util.MultiSet;
import de.tud.kom.p2psim.impl.util.Tuple;
import de.tud.kom.p2psim.impl.util.toolkits.NumberFormatToolkit;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class NCDebugger {

	static long epoch = -1;

	private static boolean idSet = false;

	private static int id = -1;

	private static int epochC = 0;

	static final File outputBasedir = new File("outputs" + File.separator
			+ "aggr");

	private static List<GossipingNodeCountValue> vals = new ArrayList<GossipingNodeCountValue>();

	static Writer richWr;

	static Writer actualWr;

	static Writer measuredNCsWr;

	static {
		try {
			outputBasedir.mkdir();
			richWr = new BufferedWriter(new FileWriter(new File(outputBasedir,
					"ncRichDebug.txt")));
			actualWr = new BufferedWriter(new FileWriter(new File(
					outputBasedir, "actualNodeCount")));

			actualWr.write("# Epoch\tExactNodesOnline\n");

			LiveMonitoring.addProgressValue(new NCsCalculated());

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						richWr.flush();
						measuredNCsWr = new BufferedWriter(new FileWriter(
								new File(outputBasedir, "measuredNodeCounts")));
						writeNCsCumulative(measuredNCsWr);
						measuredNCsWr.flush();
						measuredNCsWr.close();
						actualWr.flush();
						actualWr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void newEpoch(long newepoch) {
		if (newepoch > epoch) {
			epoch = newepoch;
			log("New epoch: " + epoch);
			idSet = false;
			epochC = 0;

			try {
				actualWr.write(String.valueOf(epoch) + "\t"
						+ getNumHostsOnline() + "\n");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void register(GossipingNodeCountValue val) {
		vals.add(val);
	}

	public static void valueUpdate(GossipingNodeCountValue caller, int theId,
			Double oldVal, Double newVal, Double updVal, String dbgNote,
			long theepoch) {
		if (epoch != theepoch)
			return;
		if (!idSet)
			id = theId;
		idSet = true;
		if (id == theId && epochC < 100) {
			try {
				richWr.write(pad(
						"upd[old="
								+ (oldVal == null ? "-" : NumberFormatToolkit
										.floorToDecimalsString(oldVal, 2))
								+ " new="
								+ (newVal == null ? "-" : NumberFormatToolkit
										.floorToDecimalsString(newVal, 2))
								+ " upd="
								+ (updVal == null ? "-" : NumberFormatToolkit
										.floorToDecimalsString(updVal, 2))
								+ "] " + dbgNote, 45));
				double aggr = 0d;
				for (GossipingNodeCountValue gvalue : vals) {
					Double val = gvalue.getNCVals().get(id);
					if (val != null && gvalue.getEpoch() == epoch)
						aggr += val;
					String valStr = gvalue.getEpoch() == epoch ? (val == null) ? "-"
							: NumberFormatToolkit.floorToDecimalsString(val, 2)
									+ (caller == gvalue ? "#" : "")
							: "x";
					valStr = pad(valStr, 5);
					richWr.write(" " + valStr);
				}
				richWr.write("A: " + aggr);
				richWr.write("\n");
				epochC++;
				richWr.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static String pad(String valStr, int padSz) {
		StringBuilder res = new StringBuilder(valStr);
		int pad = padSz - valStr.length();
		for (int i = 0; i < pad; i++) {
			res.append(' ');
		}
		return res.toString();
	}

	static void log(String log) {
		try {
			richWr.write("# " + log + "\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static int getNumHostsOnline() {
		int hostsOnline = 0;
		for (List<Host> l : Simulator.getInstance().getScenario().getHosts()
				.values()) {
			for (Host host : l) {
				if (host.getNetLayer().isOnline())
					hostsOnline++;
			}
		}
		return hostsOnline;
	}

	static Map<Long, MultiSet<Integer>> ncsCalculated = new HashMap<Long, MultiSet<Integer>>(
			1000);

	static long ncsCalculatedCount = 0;

	static long ncsCalculatedNaNCount = 0;

	public static void onNCCalculated(long epoch, int nc) {
		MultiSet<Integer> setOfEpoch = ncsCalculated.get(epoch);
		if (setOfEpoch == null) {
			setOfEpoch = new MultiSet<Integer>();
			ncsCalculated.put(epoch, setOfEpoch);
		}
		setOfEpoch.addOccurrence(nc);
		ncsCalculatedCount++;
		if (nc < 0)
			ncsCalculatedNaNCount++;
	}

	static void writeNCsCumulative(Writer wr) throws IOException {

		wr.write("# Epoch MeasuredNodeCount Frequency\n");
		for (Entry<Long, MultiSet<Integer>> e : ncsCalculated.entrySet()) {
			for (Tuple<Integer, Integer> val : e.getValue()) {
				wr.write(e.getKey() + " " + val.getA() + " " + val.getB()
						+ "\n");
			}
		}
	}

	static class NCsCalculated implements ProgressValue {

		@Override
		public String getName() {
			return "Gossip: Number of node counts calculated";
		}

		@Override
		public String getValue() {
			return "Total: " + ncsCalculatedCount + ", Undefined: "
					+ ncsCalculatedNaNCount;
		}

	}

}
