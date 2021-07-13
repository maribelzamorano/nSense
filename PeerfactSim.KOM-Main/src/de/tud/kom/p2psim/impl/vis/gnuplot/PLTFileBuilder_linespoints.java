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


package de.tud.kom.p2psim.impl.vis.gnuplot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class PLTFileBuilder_linespoints {
	public static String writePLTFile(File originalDatFile, ResultTable results)
			throws FileNotFoundException {
		String path = originalDatFile.getAbsolutePath();
		String fileName = path.substring(0, path.length() - 3) + "plt";
		String datFile = originalDatFile.getName();

		PrintWriter printWriter = new PrintWriter(fileName);
		String name = datFile.substring(0, datFile.length() - 4);
		int n = results.length;
		long t_max = results.getTimeAt(n - 1);

		double max_value = Double.MIN_VALUE;
		for (int i = 0; i < results.length; i++) {
			for (Object o : results.getObjects()) {
				try {
					String value = results.getValueForAt(o, i);
					double t = Double.parseDouble(value);
					if (t > max_value) {
						max_value = t;
					}
				} catch (NumberFormatException e) {
					System.out.println(e.getMessage() + "Error in ResultTable");
				}

			}
		}
		// write out
		printWriter.println("set term png giant");
		printWriter.println("set output \"" + name + ".png\"");
		printWriter.println("set xlabel \"Zeit\"");
		printWriter.println("set ylabel \"Wert\"");
		printWriter.println("set xrange [0:" + t_max + "]");
		printWriter.println("set yrange [0:" + max_value + "]");

		printWriter.print("plot");
		int count = 1;
		boolean firstPlotEntry = true;
		for (Object o : results.getObjects()) {
			count++;
			if (!firstPlotEntry)
				printWriter.println(",\\");
			printWriter.print(" \"" + datFile + "\" using 1:" + count
					+ " title \"" + o.toString() + "\" with lines");
			firstPlotEntry = false;
		}
		printWriter.println();

		printWriter.println("set terminal postscript eps \"Helvetica\" 24");

		printWriter.println("set output \"" + name + ".eps\"");
		printWriter.println("replot");

		printWriter.close();
		return fileName;

	}
}
