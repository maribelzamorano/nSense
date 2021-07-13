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


package de.tud.kom.p2psim.impl.skynet.analyzing.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.Constants;
import de.tud.kom.p2psim.api.service.skynet.SkyNetConstants;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.skynet.SkyNetPropertiesReader;
import de.tud.kom.p2psim.impl.skynet.analyzing.AbstractSkyNetWriter;
import de.tud.kom.p2psim.impl.skynet.analyzing.analyzers.NetLayerAnalyzer;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class Coor2RootWriter extends AbstractSkyNetWriter {

	private static Logger log = SimLogger.getLogger(NetLayerAnalyzer.class);

	private static Coor2RootWriter instance;

	private static String DATA_PATH = Constants.TMP_DIR + File.separator
			+ "coordinatorRootData";

	private static String FILE_PREFIX = "Level";

	private static String FILE_POSTFIX = ".dat";

	private HashMap<Integer, File> fileStorage;

	private HashMap<Integer, Long> timestampStorage;

	private Vector<String> namespace;

	private boolean namespaceCreated;

	private Coor2RootWriter() {
		fileStorage = new HashMap<Integer, File>();
		timestampStorage = new HashMap<Integer, Long>();
		namespace = new Vector<String>();
		namespaceCreated = false;
		initWriteDirectory(DATA_PATH, true);
	}

	public static Coor2RootWriter getInstance() {
		if (instance == null) {
			instance = new Coor2RootWriter();
		}
		return instance;
	}

	public boolean checkForNewData(SkyNetNodeInfo nodeInfo,
			long sysStatsTimestamp) {
		Long timestamp = timestampStorage.get(new Integer(nodeInfo.getLevel()));
		if (timestamp == null) {
			return true;
		} else if (sysStatsTimestamp > timestamp.longValue()) {
			return true;
		} else {
			return false;
		}
	}

	public void writeData(SkyNetNodeInfo nodeInfo,
			HashMap<String, Coor2RootEntry> data, long sysStatsTimestamp) {
		int level = nodeInfo.getLevel();
		Long timestamp = timestampStorage.remove(new Integer(level));

		// check if there already exists a file for the given level
		if (timestamp == null) {
			File file = fileStorage.remove(new Integer(level));
			if (file != null) {
				log.fatal("This my not happen:"
						+ " If there exists no timestamp for a certain level,"
						+ " a file may neither exist!!!");
			} else {
				file = writeDataInFile(data, file, true, level,
						sysStatsTimestamp);
				fileStorage.put(new Integer(level), file);
				timestampStorage.put(new Integer(level), new Long(
						sysStatsTimestamp));
			}
		} else if (sysStatsTimestamp > timestamp.longValue()) {
			File file = fileStorage.remove(new Integer(level));
			if (file == null) {
				log.fatal("This my not happen:"
						+ " If there exists a timestamp for a certain level,"
						+ " a file must also exist!!!");
			} else {
				file = writeDataInFile(data, file, false, level,
						sysStatsTimestamp);
				fileStorage.put(new Integer(level), file);
				timestampStorage.put(new Integer(level), new Long(
						sysStatsTimestamp));
			}

		}
	}

	private File writeDataInFile(HashMap<String, Coor2RootEntry> data,
			File file, boolean init, int level, long timestamp) {
		if (init) {
			file = new File(DATA_PATH + File.separator + FILE_PREFIX + level
					+ FILE_POSTFIX);
		}
		if (!namespaceCreated) {
			namespaceCreated = true;
			Iterator<String> nameIterator = data.keySet().iterator();
			String name = null;

			while (nameIterator.hasNext()) {
				name = nameIterator.next();
				if (!name.equals("NodeCount")) {
					namespace.add(name);
				}
			}
			Collections.sort(namespace);
			namespace.add(0, "NodeCount");
		}

		// if a new file is created, write the header
		if (init) {
			createHeader(file);
		}
		// print the data
		try {
			PrintWriter bw = new PrintWriter(new FileWriter(file, true));
			bw.write((timestamp / SkyNetConstants.DIVISOR_FOR_SECOND) + "\t");
			for (int i = 0; i < namespace.size(); i++) {
				if (i == 0) {
					bw.print(data.get(namespace.get(i))
							.getCoordinatorAverageValue()
							+ "\t");
					bw.print(data.get(namespace.get(i)).getRootAverageValue()
							+ "\t");
					bw.print(data.get(namespace.get(i))
							.getInterpolationFactor()
							+ "\t");
					double interpolated = (data.get(namespace.get(i))
							.getCoordinatorAverageValue() * Math
							.pow(
									SkyNetPropertiesReader
											.getInstance()
											.getIntProperty(
													"SkyNetTreeBranchingFactor"),
									level));
					bw.print(interpolated + "\t");
					double error = (data.get(namespace.get(i))
							.getRootAverageValue() - interpolated);
					bw.print(error + "\t");
					bw.print((error / data.get(namespace.get(i))
							.getRootAverageValue())
							+ "\t");
				} else {
					bw.print(data.get(namespace.get(i))
							.getCoordinatorAverageValue()
							+ "\t");
					bw.print(data.get(namespace.get(i)).getRootAverageValue()
							+ "\t");
					bw.print(data.get(namespace.get(i)).getAbsoluteError()
							+ "\t");
					bw.print(data.get(namespace.get(i)).getRelativeError()
							+ "\t");
				}
			}
			bw.println();
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;
	}

	private void createHeader(File file) {
		try {
			PrintWriter bw = new PrintWriter(new FileWriter(file, true));
			bw.println("# Time");
			for (int i = 0; i < namespace.size(); i++) {
				if (i == 0) {
					bw.println("# " + namespace.get(i) + "-Coordinator");
					bw.println("# " + namespace.get(i) + "-Root");
					bw.println("# " + namespace.get(i) + "-Co2Root-Factor");
					bw.println("# " + namespace.get(i)
							+ "-InterpolatedCoordinator");
					bw.println("# " + namespace.get(i) + "-ErrorAbs");
					bw.println("# " + namespace.get(i) + "-ErrorRel");
				} else {
					bw.println("# " + namespace.get(i) + "-AvgCoordinator");
					bw.println("# " + namespace.get(i) + "-AvgRoot");
					bw.println("# " + namespace.get(i) + "-ErrorAbs");
					bw.println("# " + namespace.get(i) + "-ErrorRel");
				}
			}
			bw.write("#Time\t");
			for (int i = 0; i < namespace.size(); i++) {
				if (i == 0) {
					bw.print(namespace.get(i) + "-Coordinator\t");
					bw.print(namespace.get(i) + "-Root\t");
					bw.print(namespace.get(i) + "-Co2Root-Factor\t");
					bw.print(namespace.get(i) + "-InterpolatedCoordinator\t");
					bw.print(namespace.get(i) + "-ErrorAbs\t");
					bw.print(namespace.get(i) + "-ErrorRel\t");
				} else {
					bw.print(namespace.get(i) + "-AvgCoordinator\t");
					bw.print(namespace.get(i) + "-AvgRoot\t");
					bw.print(namespace.get(i) + "-ErrorAbs\t");
					bw.print(namespace.get(i) + "-ErrorRel\t");
				}
			}
			bw.println();
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		// not needed within this class
	}

}
