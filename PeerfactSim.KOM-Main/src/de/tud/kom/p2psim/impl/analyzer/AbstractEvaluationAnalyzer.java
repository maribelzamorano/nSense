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


package de.tud.kom.p2psim.impl.analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.tud.kom.p2psim.Constants;
import de.tud.kom.p2psim.api.analyzer.Analyzer;
import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.oracle.GlobalOracle;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public abstract class AbstractEvaluationAnalyzer implements Analyzer,
		SimulationEventHandler {

	private boolean finalEvalDone = false;

	/*
	 * Fields for GnuPlot output
	 */

	private String outputFolderName = "Eval";

	private String outputFileName = "Data.dat";

	private BufferedWriter output;

	private boolean flushEveryLine = false;

	private long beginOfAnalyzing = 0 * Simulator.MINUTE_UNIT;

	private long endOfAnalyzing = Simulator.getEndTime() + 1;

	private long timeBetweenAnalyzeSteps = 1 * Simulator.MINUTE_UNIT;

	/*
	 * Analyzer methods
	 */

	@Override
	public void start() {
		initDatFiles();

		if (Simulator.getCurrentTime() >= beginOfAnalyzing) {
			try {
				doEvaluation();
			} catch (IOException e) {
				e.printStackTrace();
			}
			scheduleWithDelay(timeBetweenAnalyzeSteps);
		} else {
			scheduleAtTime(beginOfAnalyzing);
		}

	}

	@Override
	public void stop(Writer SysOutput) {
		try {
			doFinalEvaluation();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * SimulationEventHandler methods
	 */

	@Override
	public void eventOccurred(SimulationEvent se) {

		if (Simulator.getCurrentTime() <= endOfAnalyzing) {
			if (!finalEvalDone) {
				try {
					doEvaluation();
				} catch (IOException e) {
					e.printStackTrace();
				}
				scheduleWithDelay(timeBetweenAnalyzeSteps);
			}
		}
	}

	/*
	 * Private class methods
	 */

	private void scheduleAtTime(long time) {
		time = Math.max(time, Simulator.getCurrentTime());
		Simulator.scheduleEvent(this, time, this,
				SimulationEvent.Type.OPERATION_EXECUTE);
	}

	private void scheduleWithDelay(long delay) {
		scheduleAtTime(Simulator.getCurrentTime() + delay);
	}

	private void doEvaluation() throws IOException {
		// Output the generated line string
		output.write(generateEvaluationMetrics() + "\n");

		// Flush output after each line if needed
		if (flushEveryLine)
			output.flush();
	}

	private void doFinalEvaluation() throws IOException {
		finalEvalDone = true;
		doEvaluation();
	}

	/*
	 * GnuPlot Output methods
	 */

	/**
	 * Override this method to generate a own unique folder name for the
	 * outputs.
	 * 
	 * @return the folder name
	 */
	protected String getUniqueFolderName() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String name = df.format(cal.getTime());
		if (GlobalOracle.getHosts().size() > 0)
			name += "_size" + GlobalOracle.getHosts().size();
		name += "_seed" + Simulator.getSeed();

		return name + File.separator;
	}

	private void initDatFiles() {
		String dirName = Constants.OUTPUTS_DIR + File.separator
				+ outputFolderName + File.separator + getUniqueFolderName();
		File statDir = new File(dirName);
		if (!statDir.exists() || !statDir.isDirectory()) {
			statDir.mkdirs();
		}

		try {
			File outputFile = new File(dirName + outputFileName);

			output = new BufferedWriter(new FileWriter(outputFile));

			// Write head line
			output.write(generateHeadlineForMetrics() + "\n");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Optional to use setters
	 */

	protected void setFolderName(String outputFolderName) {
		this.outputFolderName = outputFolderName;
	}

	protected void setFlushEveryLine(boolean flushEveryLine) {
		this.flushEveryLine = flushEveryLine;
	}

	protected void setBeginOfAnalyzing(long beginOfAnalyzing) {
		this.beginOfAnalyzing = beginOfAnalyzing;
	}

	protected void setEndOfAnalyzing(long endOfAnalyzing) {
		this.endOfAnalyzing = endOfAnalyzing;
	}

	protected void setTimeBetweenAnalyzeSteps(long timeBetweenAnalyzeSteps) {
		this.timeBetweenAnalyzeSteps = timeBetweenAnalyzeSteps;
	}

	protected void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	/*
	 * Abstract methods
	 */

	/**
	 * Generates a string that is prepended to the metric file
	 * 
	 * NOTE: Could be used to include a description of the data columns.
	 * 
	 * @return the string to be prepended
	 */
	protected abstract String generateHeadlineForMetrics();

	/**
	 * Generates a string, containing the metrics to be plotted.
	 * 
	 * NOTE: Values should be separated by whitespace or tabulators (\t). The
	 * string should not contain any line breaks.
	 * 
	 * @return the metrics string
	 */
	protected abstract String generateEvaluationMetrics();

}
