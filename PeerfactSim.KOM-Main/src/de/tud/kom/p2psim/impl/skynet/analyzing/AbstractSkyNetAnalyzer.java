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

import java.io.Writer;

import de.tud.kom.p2psim.api.analyzer.Analyzer;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public abstract class AbstractSkyNetAnalyzer extends AbstractSkyNetWriter
		implements Analyzer {

	protected boolean runningAnalyzer;

	public AbstractSkyNetAnalyzer() {
		runningAnalyzer = false;
	}

	@Override
	public void start() {
		runningAnalyzer = true;
		initialize();
	}

	protected abstract void initialize();

	protected abstract void finish();

	@Override
	public void stop(Writer output) {
		runningAnalyzer = false;
		finish();
	}
}
