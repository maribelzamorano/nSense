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


package de.tud.kom.p2psim.impl.util.filesharing2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import de.tud.kom.p2psim.SimulatorRunner;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class UNIXShellScriptGenerator {

	static final String PATH_TO_CFGS = "config/filesharing2/";
	static final String PATH_TO_LOGS = "logging/filesharing2/";
	static final Class RUN_CLASS = SimulatorRunner.class;
	
	static final List<String> OVERLAYS = Arrays.asList("Kademlia2", "Gia", "Chord2", "Gnutella");
	static final List<String> PARAMS = Arrays.asList("32", "100", "316", "1000", "3162", "10000", "10000-300d", "10000-voip", "Stability");
	static final String VM_ARGS = "-Xms1G -Xmx20G";
	static final String CLASSPATH = "lib/*:bin";
	static final String NETLAYER2USE = "Mod";
	static final int REPETITIONS = 4;
	
	static Random rand = new Random();
	
	public static void main(String[] args) {
		try {
			generateShellScript(new FileWriter("config/filesharing2/runAll.sh"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void generateShellScript(Writer os) throws IOException {
		BufferedWriter buf = new BufferedWriter(os);
		
		buf.write("#/bin/bash\n\nNETLAYER=" + NETLAYER2USE + "\nVMARGS='" + VM_ARGS + "'\n\n");
		
		for (int i = 1; i <= REPETITIONS; i++) {	
			for (String overlay : OVERLAYS) {
				for (String param : PARAMS) {
					
					String confTag = "FS" + overlay + "-" + param;
					
					File f = new File(PATH_TO_CFGS + confTag + ".xml");
					
					if (!f.exists()) throw new IllegalStateException("The file " + f + " does not exist.");
				
				
					buf.write("date\n");
					buf.write("echo \"Simulating "  + confTag +  ", round" + i + "\"\n");
					
					String logfile = PATH_TO_LOGS + confTag + "_r" + i +".log";
					
					buf.write("java $VMARGS -cp " + CLASSPATH + " " + RUN_CLASS.getName() + " "
							+ f.getPath() + " seed=" + rand.nextInt() + " NetLayer=$NETLAYER > " + logfile + "\n");
				
				}
			}
		}
		
		buf.flush();
		buf.close();
		
	}
	
}
