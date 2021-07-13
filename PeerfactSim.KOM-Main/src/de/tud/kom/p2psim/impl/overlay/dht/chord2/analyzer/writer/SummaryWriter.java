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
import java.util.Properties;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.ChordStructurPostProcessing;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This class writes the summary results in a <code>Properties</code>
 * file output.
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class SummaryWriter {

	private final String dir = ChordStructurPostProcessing.ROOT_OUT;
	
	public static final String fileName = "summary.text";

	private static Logger log = SimLogger.getLogger(SummaryWriter.class);
			
	// List of metrics
	public static final String Num_Peer = "Num_Peer";

	public static final String Num_Lookup = "Num_Lookup";

	public static final String Valid_Lookup = "Valid_Lookup";

	public static final String Avg_Lookup_Time = "Avg_Lookup_Time";

	public static final String Data_traffic_Out = "Data_traffic_Out";
	
	public static final String Data_traffic_In = "Data_traffic_In";
	
	public static final String System_traffic_Out = "System_traffic_Out";
	
	
	private Properties properties;

	public SummaryWriter(){
		
		properties = new Properties();
	}
	
	public void putValue(String metric, String value){
		properties.setProperty(metric, value);
	}
	
	public void write() {
		try {
			File output = new File(dir,fileName);
			properties.store(new FileOutputStream(output), null);
		} catch (IOException e) {
			log.error("", e);
		}
	}
}
