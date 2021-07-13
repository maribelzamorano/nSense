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


package de.tud.kom.p2psim.impl.vis.ui.common.gnuplot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.controller.player.Player;
import de.tud.kom.p2psim.impl.vis.gnuplot.IResultFileWriter;
import de.tud.kom.p2psim.impl.vis.gnuplot.PLTFileBuilder_linespoints;
import de.tud.kom.p2psim.impl.vis.gnuplot.ResultTable;
import de.tud.kom.p2psim.impl.vis.gnuplot.SimpleGnuplotFileWriter;
import de.tud.kom.p2psim.impl.vis.util.Config;
import de.tud.kom.p2psim.impl.vis.util.gui.JConfigCheckBox;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public abstract class MetricVsTimeBasisPanel extends JPanel implements
		ActionListener {
	private static final long serialVersionUID = -7946040448823203271L;

	public BeginEndIntervalPanel timeChoicePanel;

	protected CreateGnuplotFileButtons createGnuplotFileButtons;

	protected JConfigCheckBox generatePLTFile = new JConfigCheckBox(
			"PLT-Datei erzeugen", "Gnuplot/GeneratePLTFile");

	protected JConfigCheckBox generateGraphics = new JConfigCheckBox(
			"Ausgabegrafik rendern", "Gnuplot/generateGraphics");

	public MetricVsTimeBasisPanel() {
		createTimeChoice();
		createGnuplotFileButtons.getGnuplotButton().addActionListener(this);
		long max_end = Controller.getTimeline().getMaxTime()
				/ Player.TIME_UNIT_MULTIPLICATOR;
		timeChoicePanel.setLowerandUpperBound(0, 0, 1, Long.MAX_VALUE, max_end,
				Long.MAX_VALUE);
	}

	private void createTimeChoice() {
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(600, 300));
		timeChoicePanel = new BeginEndIntervalPanel();
		timeChoicePanel.setBounds(319, 58, 217, 66);

		JPanel sideContainer = new JPanel();
		sideContainer.setLayout(new FlowLayout());
		sideContainer.setPreferredSize(new Dimension(200, 200));
		sideContainer.add(timeChoicePanel);

		sideContainer.add(generatePLTFile);
		sideContainer.add(generateGraphics);

		this.add(sideContainer, BorderLayout.EAST);

		createGnuplotFileButtons = new CreateGnuplotFileButtons();
		this.add(createGnuplotFileButtons, BorderLayout.SOUTH);
		// createGnuplotFileButton.setBounds(326, 184, 221, 50);
	}

	public long[] checkTimeChoiceValue() {
		long start = timeChoicePanel.getStartValue();
		if (start == Long.MIN_VALUE) {
			return null;
		}
		long end = timeChoicePanel.getEndValue();
		if (end == Long.MIN_VALUE) {
			return null;
		}
		long interval = timeChoicePanel.getIntervalValue();
		if (interval == Long.MIN_VALUE) {
			return null;
		}
		if (start >= end) {
			JOptionPane
					.showMessageDialog(this,
							"Der Startzeitpunkt muss kleiner sein als der Endzeitpunkt");
			return null;
		}
		return new long[] { start, end, interval };
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o == createGnuplotFileButtons.getGnuplotButton()) {
			long[] values = checkTimeChoiceValue();
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					values[i] *= Player.TIME_UNIT_MULTIPLICATOR;
				}

				generatePLTFile.saveSettings();
				generateGraphics.saveSettings();

				File saveFile = new GnuplotFileChooser(this).askWhereToSave();
				if (saveFile != null) {

					ResultTable results = createTable(values);
					IResultFileWriter fw = new SimpleGnuplotFileWriter();
					try {
						// File .dat
						fw.writeToFile(saveFile, results);

						if (generatePLTFile.isSelected()) {
							// File .plt
							System.out.println("PLT-Datei erzeugen...");
							String pltFileName = PLTFileBuilder_linespoints
									.writePLTFile(saveFile, results);
							if (generateGraphics.isSelected()) {
								System.out.println("Aufruf von Gnuplot...");
								try {
									runGnuplot(pltFileName);
								} catch (IOException ex_GenerateGraphics) {
									JOptionPane
											.showMessageDialog(
													this,
													"Pfad zur Gnuplot-Executable nicht gefunden "
															+ "bitte korigieren in der Datei config.xml",
													"Ausgabegrafik",
													JOptionPane.PLAIN_MESSAGE);
								}
							}
						}
						System.out.println("Fertig!");

						JOptionPane.showMessageDialog(this,
								"Export erfolgreich", "Gnuplot-Export",
								JOptionPane.PLAIN_MESSAGE);
					} catch (IOException ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(this,
								"E/A-Fehler beim Export: " + ex.getMessage(),
								"Gnuplot-Export", JOptionPane.ERROR_MESSAGE);
					}

				}
			}
		}
	}

	protected abstract ResultTable createTable(long[] values);

	private void runGnuplot(String pltFileName) throws IOException {

		String[] command = { Config.getValue("Gnuplot/ExecPath", "gnuplot"),
				pltFileName };
		// String command = Config.getValue("Gnuplot/ExecPath", "gnuplot") +
		// " '" + pltFileName + "'";
		// String command = "pwd";

		// System.out.println(new
		// File(pltFileName).getParentFile().getAbsolutePath());

		System.out.println("Befehl: \"" + command[0] + " " + command[1] + "\"");
		Process process = Runtime.getRuntime().exec(command, null,
				new File(pltFileName).getParentFile());

		String text = ""; // Lesepuffer
		PrintWriter out = new PrintWriter(System.out);
		BufferedReader in = new BufferedReader(new InputStreamReader(process
				.getInputStream()));
		// Alle Zeichen aus dem Stream auslesen und
		// auf der Standardausgabe ausgeben
		while ((text = in.readLine()) != null) {
			out.println(text);
			out.flush();
		}

		String text2 = ""; // Lesepuffer
		PrintWriter out2 = new PrintWriter(System.out);
		BufferedReader in2 = new BufferedReader(new InputStreamReader(process
				.getErrorStream()));
		// Alle Zeichen aus dem Stream auslesen und
		// auf der Standardausgabe ausgeben
		while ((text2 = in2.readLine()) != null) {
			out2.println("ERROR: " + text2);
			out2.flush();
		}
	}

}
