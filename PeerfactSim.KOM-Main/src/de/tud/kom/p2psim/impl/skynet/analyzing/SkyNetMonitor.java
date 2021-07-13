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


package de.tud.kom.p2psim.impl.skynet.analyzing;

import java.util.Iterator;

import de.tud.kom.p2psim.api.analyzer.Analyzer.ChurnAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.ConnectivityAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.KBROverlayAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.TransAnalyzer;
import de.tud.kom.p2psim.impl.common.DefaultMonitor;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.measurement.KademliaMonitor;

/**
 * This class extends the currently used monitor-class with the functionality,
 * defined by the <code>SkyNetMonitor</code>-interface. The definition of the
 * new methods is given in {@link de.tud.kom.p2psim.api.skynet.SkyNetMonitor},
 * while {@link DefaultMonitor} introduces the general mode of operation of a
 * monitor.
 * 
 * @author Dominik Stingl, Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public class SkyNetMonitor extends KademliaMonitor implements
		de.tud.kom.p2psim.api.service.skynet.SkyNetMonitor {

	public ChurnAnalyzer getChurnAnalyzers(Class clazz) {
		Iterator<ChurnAnalyzer> iter = churnAnalyzers.iterator();
		while (iter.hasNext()) {
			ChurnAnalyzer a = iter.next();
			if (a.getClass().equals(clazz)) {
				return a;
			}
		}
		return null;
	}

	public ConnectivityAnalyzer getConnectivityAnalyzer(Class clazz) {
		Iterator<ConnectivityAnalyzer> iter = connAnalyzers.iterator();
		while (iter.hasNext()) {
			ConnectivityAnalyzer a = iter.next();
			if (a.getClass().equals(clazz)) {
				return a;
			}
		}
		return null;
	}

	public NetAnalyzer getNetAnalyzer(Class clazz) {
		Iterator<NetAnalyzer> iter = netAnalyzers.iterator();
		while (iter.hasNext()) {
			NetAnalyzer a = iter.next();
			if (a.getClass().equals(clazz)) {
				return a;
			}
		}
		return null;
	}

	public OperationAnalyzer getOperationAnalyzer(Class clazz) {
		Iterator<OperationAnalyzer> iter = opAnalyzers.iterator();
		while (iter.hasNext()) {
			OperationAnalyzer a = iter.next();
			if (a.getClass().equals(clazz)) {
				return a;
			}
		}
		return null;
	}

	public TransAnalyzer getTransAnalyzers(Class clazz) {
		return null;
	}

	@Override
	public KBROverlayAnalyzer getKBROverlayAnalyzer(Class clazz) {
		Iterator<KBROverlayAnalyzer> iter = overlayAnalyzers.iterator();
		while (iter.hasNext()) {
			KBROverlayAnalyzer a = iter.next();
			if (a.getClass().equals(clazz)) {
				return a;
			}
		}
		return null;
	}

}
