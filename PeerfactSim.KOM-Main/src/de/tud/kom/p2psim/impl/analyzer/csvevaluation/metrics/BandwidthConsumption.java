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


package de.tud.kom.p2psim.impl.analyzer.csvevaluation.metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.network.Bandwidth;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.toolkits.NumberFormatToolkit;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class BandwidthConsumption {

	static final int DECIMALS = 3;

	static final int MULTIPLICATOR = 100; // value in percent.

	Map<NetID, Bandwidth> bwOfHosts = new HashMap<NetID, Bandwidth>();

	Map<NetID, BWAccumulator> accumulators = new HashMap<NetID, BWAccumulator>();

	AvgDn avgDn = new AvgDn();

	PeakDn peakDn = new PeakDn();

	AvgUp avgUp = new AvgUp();

	PeakUp peakUp = new PeakUp();

	private long lastResultGenerationTime = -1;

	private boolean resultGenerated;

	double resultPeakUp;

	double resultPeakDown;

	double resultAvgUp;

	double resultAvgDn;

	public void init() {
		Map<String, List<Host>> hosts = Simulator.getInstance().getScenario()
				.getHosts();
		for (List<Host> group : hosts.values()) {
			for (Host host : group) {
				double upBW = host.getNetLayer().getMaxUploadBandwidth();
				double downBW = host.getNetLayer().getMaxDownloadBandwidth();
				NetID netID = host.getNetLayer().getNetID();
				bwOfHosts.put(netID, new Bandwidth(downBW, upBW));
				System.out.println("Found host " + host);
				accumulators.put(netID, new BWAccumulator());
			}
		}
	}

	public void messageSent(NetID id, Message msg) {
		continueMeasurement();
		accumulators.get(id).incConsumedUp(msg.getSize());
	}

	public void messageReceived(NetID id, Message msg) {
		continueMeasurement();
		accumulators.get(id).incConsumedDown(msg.getSize());
	}

	public Metric getAvgDn() {
		return avgDn;
	}

	public Metric getAvgUp() {
		return avgUp;
	}

	public Metric getPeakDn() {
		return peakDn;
	}

	public Metric getPeakUp() {
		return peakUp;
	}

	public void continueMeasurement() {
		resultGenerated = false;
	}

	public void generateResultCond() {

		if (resultGenerated == true)
			return;

		double peakUp = 0;
		double peakDn = 0;
		double sumUp = 0;
		double sumDn = 0;

		for (Entry<NetID, BWAccumulator> e : accumulators.entrySet()) {
			Bandwidth bw = bwOfHosts.get(e.getKey());
			BWAccumulator a = e.getValue();
			double up = a.getConsumedUp() / bw.getUpBW();
			double down = a.getConsumedDown() / bw.getDownBW();
			if (up > peakUp)
				peakUp = up;
			if (down > peakDn)
				peakDn = down;
			sumUp += up;
			sumDn += down;
			a.reset();
		}

		long currentTime = Simulator.getCurrentTime();

		double timeInterval = (currentTime - lastResultGenerationTime)
				/ (double) Simulator.SECOND_UNIT;

		resultPeakUp = peakUp / timeInterval;
		resultPeakDown = peakDn / timeInterval;
		resultAvgUp = sumUp / timeInterval / accumulators.size();
		resultAvgDn = sumDn / timeInterval / accumulators.size();

		resultGenerated = true;
		this.lastResultGenerationTime = currentTime;
	}

	public class AvgUp implements Metric {

		@Override
		public String getMeasurementFor(long time) {
			generateResultCond();
			return formatNumber(resultAvgUp);
		}

		@Override
		public String getName() {
			return "AvgUpBWCons(%)";
		}

	}

	public class AvgDn implements Metric {

		@Override
		public String getMeasurementFor(long time) {
			generateResultCond();
			return formatNumber(resultAvgDn);
		}

		@Override
		public String getName() {
			return "AvgDnBWCons(%)";
		}

	}

	public class PeakUp implements Metric {

		@Override
		public String getMeasurementFor(long time) {
			generateResultCond();
			return formatNumber(resultPeakUp);
		}

		@Override
		public String getName() {
			return "PeakUpBWCons(%)";
		}

	}

	public class PeakDn implements Metric {

		@Override
		public String getMeasurementFor(long time) {
			generateResultCond();
			return formatNumber(resultPeakDown);
		}

		@Override
		public String getName() {
			return "PeakDnBWCons(%)";
		}

	}

	class BWAccumulator {

		public long getConsumedUp() {
			return consumedUp;
		}

		public void incConsumedUp(long inc) {
			this.consumedUp += inc;
		}

		public long getConsumedDown() {
			return consumedDown;
		}

		public void incConsumedDown(long inc) {
			this.consumedDown += inc;
		}

		public void reset() {
			consumedUp = 0;
			consumedDown = 0;
		}

		long consumedUp = 0;

		long consumedDown = 0;

	}

	String formatNumber(double number) {
		return NumberFormatToolkit.floorToDecimalsString(
				number * MULTIPLICATOR, DECIMALS);
		// return String.valueOf(number*MULTIPLICATOR);
	}

}
