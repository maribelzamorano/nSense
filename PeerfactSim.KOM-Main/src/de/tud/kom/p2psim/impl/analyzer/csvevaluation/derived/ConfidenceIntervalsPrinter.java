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

import de.tud.kom.p2psim.impl.analyzer.csvevaluation.derived.lib.ConfidenceIntervals;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.derived.lib.IScale;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.derived.lib.IYValueSetFactory;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.derived.lib.Parser;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.derived.lib.Printer;
import de.tud.kom.p2psim.impl.analyzer.csvevaluation.derived.lib.Sorter;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class ConfidenceIntervalsPrinter {

	public void printToFile(File input, File output, int colX, int colY,
			IScale scale) throws IOException {

		System.out.println("Preparing...");

		Parser parser = new Parser(input, colX, colY);

		IYValueSetFactory<ConfidenceIntervals> factory = new ConfidenceIntervals.ConfidenceIntervalsFactory(
				0.95f, 4);

		System.out.println("Reading in and sorting...");

		Sorter<ConfidenceIntervals> s = new Sorter<ConfidenceIntervals>(parser,
				scale, factory);

		s.sortIn();

		System.out.println("Outputting results...");

		Printer printer = new Printer(s.getResultStream());

		printer.print(output);

		System.out.println(s.printStats());

	}

}
