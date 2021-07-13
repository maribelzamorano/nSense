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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.measurement.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads measurement data from files.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public class MeasurementReader {

	private Map<Integer, OpDelay> opDelay;

	private Map<Integer, OpTraffic> opTraffic;

	private Map<Integer, DataLookupQuality> dataQuality;

	private Map<Integer, NodeLookupQuality> nodeQuality;

	/**
	 * Reads operation delay information from the given file.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public final void readOpDelay(final String path) throws IOException {
		opDelay = new HashMap<Integer, OpDelay>();
		FileReader fr = new FileReader(path);
		BufferedReader in = new BufferedReader(fr);
		String line = null;
		OpDelay tmp;
		in.readLine(); // ignore first line
		while ((line = in.readLine()) != null) {
			tmp = OpDelay.fromString(line);
			opDelay.put(tmp.getOperationID(), tmp);
		}
	}

	/**
	 * Reads operation traffic information from the given file.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public final void readOpTraffic(final String path) throws IOException {
		opTraffic = new HashMap<Integer, OpTraffic>();
		FileReader fr = new FileReader(path);
		BufferedReader in = new BufferedReader(fr);
		String line = null;
		OpTraffic tmp;
		in.readLine(); // ignore first line
		while ((line = in.readLine()) != null) {
			tmp = OpTraffic.fromString(line);
			opTraffic.put(tmp.getOperationID(), tmp);
		}
	}

	/**
	 * Reads data lookup quality information from the given file.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public final void readDataQuality(final String path) throws IOException {
		dataQuality = new HashMap<Integer, DataLookupQuality>();
		FileReader fr = new FileReader(path);
		BufferedReader in = new BufferedReader(fr);
		String line = null;
		DataLookupQuality tmp;
		in.readLine(); // ignore first line
		while ((line = in.readLine()) != null) {
			tmp = DataLookupQuality.fromString(line);
			// dataQuality.put(tmp.getOperationID(), tmp); // FIXME
		}
	}

	/**
	 * Reads node lookup quality information from the given file.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public final void readNodeQuality(final String path) throws IOException {
		nodeQuality = new HashMap<Integer, NodeLookupQuality>();
		FileReader fr = new FileReader(path);
		BufferedReader in = new BufferedReader(fr);
		String line = null;
		NodeLookupQuality tmp;
		in.readLine(); // ignore first line
		while ((line = in.readLine()) != null) {
			tmp = NodeLookupQuality.fromString(line);
			nodeQuality.put(tmp.getOperationID(), tmp);
		}
	}

}
