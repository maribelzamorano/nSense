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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.Constants;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric.StabilizeEvaluator;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordBootstrapManager;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordRoutingTable;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class collects the needed information for statistic and store it in an
 * <code>Scenario</code> object. The <code>Scenario</code> object after that
 * will be stored in an object output stream.
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordStructurStatsWriter {

	public static final String ROOT_PATH = Constants.TMP_DIR + File.separator
			+ "Chord2AnalyzerResult";

	public static final String FILE_NAME_PREFIX = "ChordOverlay";

	private final String fileType = "dat";

	private static int count = 0;

	private static Logger log = SimLogger
			.getLogger(ChordStructurStatsWriter.class);

	public void writeStats() {

		log.debug("write out Chord Overlay Structure");
		try {
			String formatted = String.format("%02d", count);
			String fileName = FILE_NAME_PREFIX + formatted + "." + fileType;
			count++;

			File output = new File(ROOT_PATH, fileName);
			ObjectOutputStream outputStream = new ObjectOutputStream(
					new FileOutputStream(output));

			Scenario scenario = new Scenario();
			scenario.setTimeStamp(Simulator.getCurrentTime());

			// list of all ChordNode
			List<ChordNode> activeNode = ChordBootstrapManager
					.getAllAvailableNodes();
			ArrayList<ChordContact> contacts = new ArrayList<ChordContact>();
			for (ChordNode chordNode : activeNode) {
				contacts.add(chordNode.getLocalChordContact());
			}

			// hash map ChordNode and ChordRoutingTable
			HashMap<ChordContact, ChordRoutingTable> routingTables = new HashMap<ChordContact, ChordRoutingTable>();
			for (ChordNode chordNode : activeNode) {
				routingTables.put(chordNode.getLocalChordContact(),
						chordNode.getChordRoutingTable());
			}
			StabilizeEvaluator stab = new StabilizeEvaluator();
			stab.setContactList(contacts);
			stab.setRoutingTableMap(routingTables);
			scenario.setStabilizeEvaluator(stab);

			// Metric store, all of singleton classes?

			outputStream.writeObject(scenario);
			outputStream.flush();
			outputStream.close();

		} catch (IOException e) {
			e.printStackTrace(System.err);
		}

	}

	public static void clearOutputDir() {
		File dir = new File(ROOT_PATH);
		deleteDir(dir);
		dir.mkdir();
	}

	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty so delete it
		return dir.delete();
	}
}
