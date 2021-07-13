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


package de.tud.kom.p2psim.impl.vis.ui.common.config.gnuplot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.tud.kom.p2psim.impl.vis.util.Config;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class GnuplotExecutableChooser extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7315226523509468698L;

	/**
	 * Initialwert für den Pfad der Gnuplot-Executable. Wird durch config.xml
	 * überschrieben.
	 */
	private static final String INITIAL_VALUE = "gnuplot";

	private static final String CONF_PATH = "Gnuplot/ExecPath";

	JButton browseButton;

	JTextField tf;

	public GnuplotExecutableChooser() {
		this.setLayout(new BorderLayout());
		tf = new JTextField();
		tf.setText(getInitialValue());
		tf.setPreferredSize(new Dimension(300, 20));
		this.add(tf, BorderLayout.CENTER);
		browseButton = new JButton("Durchsuchen...");
		browseButton.addActionListener(this);
		this.add(browseButton, BorderLayout.EAST);
	}

	private String getInitialValue() {
		return Config.getValue(CONF_PATH, INITIAL_VALUE);
	}

	protected File askFileDialog() {
		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}

	public void commitChanges() {
		Config.setValue(CONF_PATH, tf.getText());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == browseButton) {
			File exe = askFileDialog();
			if (exe != null) {
				tf.setText(exe.getAbsolutePath());
			}
		}
	}

}
