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


package de.tud.kom.p2psim.impl.analyzer.csvevaluation.derived;

import java.io.File;
import java.io.IOException;

import de.tud.kom.p2psim.impl.analyzer.csvevaluation.derived.lib.IScale;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.derived.lib.LinearScale;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class Stability extends ConfidenceIntervalsPrinter {

	static final String[] overlayDirs = new String[] { "fs_gnutella",
			"fs_chord", "fs_gia", "fs_kademlia" };

	static String inputFileName = "/stab_defaults_time_";

	static String resName;

	static IScale SCALE;

	// 0Time (ms) 1Hosts 2Messages/sec 3StaleContacts(%) 4StaleMsgs(%)
	// 5avgUpstreamBW 6peakUpstreamBW 7avgDownstreamBW 8peakDownstreamBW
	// 9AvgDnBWCons(%)
	// 10AvgUpBWCons(%) 11PeakDnBWCons(%) 12PeakUpBWCons(%) 13avgStaleBW
	// 14peakStaleBW
	// 15AvgQueryTime 16QuerySuccess(%) 17AvgNHops

	public static void main(String[] args) {
		resName = "lowRes";
		SCALE = new LinearScale(30);
		doCalculation();
		resName = "medRes";
		SCALE = new LinearScale(12);
		doCalculation();
		resName = "highRes";
		SCALE = new LinearScale(6);
		doCalculation();

	}

	public static void doCalculation() {
		for (String dirName : overlayDirs) {
			String dir = "outputs/" + dirName;

			Stability sc = new Stability();

			sc.calculateStaleMessagesWithTime(dir);
			sc.calculateQuerySuccessWithTime(dir);
		}
	}

	public void calculateStaleMessagesWithTime(String dir) {

		File inputFile = new File(dir + inputFileName + resName);
		File outputFile = new File(dir + "/stab_StaleMessagesWithTime_"
				+ resName);
		try {
			new ConfidenceIntervalsPrinter().printToFile(inputFile, outputFile,
					0, 4, SCALE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public void calculateQuerySuccessWithTime(String dir) {

		File inputFile = new File(dir + inputFileName + resName);
		File outputFile = new File(dir + "/stab_QuerySuccessWithTime_"
				+ resName);
		try {
			new ConfidenceIntervalsPrinter().printToFile(inputFile, outputFile,
					0, 16, SCALE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
