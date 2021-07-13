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


package de.tud.kom.p2psim.api.service.skynet;

import de.tud.kom.p2psim.api.analyzer.Analyzer.ChurnAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.ConnectivityAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.KBROverlayAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.TransAnalyzer;
import de.tud.kom.p2psim.impl.common.DefaultMonitor;

/**
 * This interface defines the additional functionality of a monitor within a
 * simulation. Since each SkyNet-node will use the analyzers of a simulation to
 * collect information about the own host, this analyzers must be accessible. As
 * the current monitor-class {@link DefaultMonitor} does not allow a reading
 * access of the analyzers, <code>SkyNetMonitor</code> defines the
 * aforementioned functionality for a new monitor-class, which shall extend
 * <code>DefaultMonitor</code>.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public interface SkyNetMonitor {

	/**
	 * This method returns a reference of the actual class, which implements the
	 * <code>NetAnalyzer</code>-interface.
	 * 
	 * @param clazz
	 *            contains the name of the current class, which implements the
	 *            <code>NetAnalyzer</code>-interface.
	 * @return the reference of the implementing class, or <code>null</code>, if
	 *         there is no class <code>clazz</code>.
	 */
	public NetAnalyzer getNetAnalyzer(Class clazz);

	/**
	 * This method returns a reference of the actual class, which implements the
	 * <code>TransAnalyzer</code>-interface.
	 * 
	 * @param clazz
	 *            contains the name of the current class, which implements the
	 *            <code>TransAnalyzer</code>-interface.
	 * @return the reference of the implementing class, or <code>null</code>, if
	 *         there is no class <code>clazz</code>.
	 */
	public TransAnalyzer getTransAnalyzers(Class clazz);

	/**
	 * This method returns a reference of the actual class, which implements the
	 * <code>OperationAnalyzer</code>-interface.
	 * 
	 * @param clazz
	 *            contains the name of the current class, which implements the
	 *            <code>OperationAnalyzer</code>-interface.
	 * @return the reference of the implementing class, or <code>null</code>, if
	 *         there is no class <code>clazz</code>.
	 */
	public OperationAnalyzer getOperationAnalyzer(Class clazz);

	/**
	 * This method returns a reference of the actual class, which implements the
	 * <code>ChurnAnalyzer</code>-interface.
	 * 
	 * @param clazz
	 *            contains the name of the current class, which implements the
	 *            <code>ChurnAnalyzer</code>-interface.
	 * @return the reference of the implementing class, or <code>null</code>, if
	 *         there is no class <code>clazz</code>.
	 */
	public ChurnAnalyzer getChurnAnalyzers(Class clazz);

	/**
	 * This method returns a reference of the actual class, which implements the
	 * <code>ConnectivityAnalyzer</code>-interface.
	 * 
	 * @param clazz
	 *            contains the name of the current class, which implements the
	 *            <code>ConnectivityAnalyzer</code>-interface.
	 * @return the reference of the implementing class, or <code>null</code>, if
	 *         there is no class <code>clazz</code>.
	 */
	public ConnectivityAnalyzer getConnectivityAnalyzer(Class clazz);

	public KBROverlayAnalyzer getKBROverlayAnalyzer(Class clazz);

}
