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


package de.tud.kom.p2psim.impl.overlay.gnutella.gia.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class QueryDebugger {

	static QueryDebugger inst;
	
	public static QueryDebugger getInstance() {
		if (inst == null) inst = new QueryDebugger();
		return inst;
	}

	private BufferedWriter buf;
	
	public QueryDebugger() {
		try {
			buf = new BufferedWriter(new FileWriter(new File("outputs/GiaQueryDebug")));
			buf.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void write(Object text) {
		try {
			buf.write(getTime() + " - " + ((text==null)?"null":text.toString() + "\r\n"));
			buf.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void close() {
		try {
			buf.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	String getTime() {
		return Simulator.getFormattedTime(Simulator.getCurrentTime());
	}
	
}
