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


package de.tud.kom.p2psim.impl.util.guirunner.progress;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import de.tud.kom.p2psim.impl.util.LiveMonitoring;
import de.tud.kom.p2psim.impl.util.toolkits.TimeToolkit;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public abstract class RichProgressView extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -682537688746137796L;

	JProgressBar progress;

	ValuesTable values;

	JButton cancelBtn;

	String jobName = null;

	public long timeOperationStarted;

	public int lastProgress = -1;

	private boolean finished = false;

	JLabel jobNameLabel;

	JLabel progressLabel;

	JLabel elapsedTimeLabel;

	JLabel estTimeLabel;

	String title;
	
	JPanel buttonPanel;
	
	TimeToolkit timeTk = new TimeToolkit(1);

	public RichProgressView() {
		this.setSize(500, 400);

		this.setLayout(new BorderLayout());

		progress = new JProgressBar(SwingConstants.HORIZONTAL);
		jobNameLabel = new JLabel();
		progressLabel = new JLabel();
		elapsedTimeLabel = new JLabel();
		estTimeLabel = new JLabel();

		JPanel progressPane = new JPanel();

		populateProgressPane(progressPane);

		this.getContentPane().add(progressPane, BorderLayout.NORTH);

		values = new ValuesTable();
		this.getContentPane().add(new JScrollPane(values), BorderLayout.CENTER);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		cancelBtn = new JButton("Abbrechen");
		cancelBtn.addActionListener(this);
		buttonPanel.add(cancelBtn);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		timeOperationStarted = System.currentTimeMillis();
	}

	private void populateProgressPane(JPanel progressPane) {
		GridBagLayout gbl = new GridBagLayout();
		// progressPane.setBorder(new LineBorder(Color.BLACK, 1));
		GridBagConstraints c = new GridBagConstraints();
		progressPane.setLayout(gbl);

		Insets padding = new Insets(3, 3, 3, 3);

		c.gridx = 0;
		c.gridy = 0;
		c.insets = padding;
		c.anchor = GridBagConstraints.LINE_START;
		progressPane.add(jobNameLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.insets = padding;
		c.anchor = GridBagConstraints.LINE_END;
		progressPane.add(progressLabel, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = padding;
		progressPane.add(progress, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.insets = padding;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		progressPane.add(elapsedTimeLabel, c);

		c.gridx = 1;
		c.gridy = 2;
		c.insets = padding;
		c.anchor = GridBagConstraints.LINE_END;
		progressPane.add(estTimeLabel, c);

	}

	public void setMaximum(int max) {
		progress.setMaximum(max);
	}

	public void setTitle(String title) {
		this.title = title;
		refreshTitle();
	}

	public void refreshTitle() {
		String title = this.title + " - " + getValuePercent() + "%";
		if (jobName != null && !jobName.trim().isEmpty())
			title += " - " + jobName;
		super.setTitle(title);
	}

	public void rebuildProgressValues() {
		// values.doLayout();
		values.updateUI();
		// values.repaint();
		// System.out.println("RowCount: " + values.getRowCount());
	}

	public void update() {
		int prog = getProgress();

		if (lastProgress != prog) {
			jobName = getActualJobName();
			progress.setValue(prog);
			refreshTitle();
			jobNameLabel.setText(jobName);
			progressLabel.setText(getValuePercent() + "%");
			elapsedTimeLabel.setText(timeTk.richTimeStringFromLong(getElapsedTime())
					.toString());

			long timeMs = getEstimatedTime();

			estTimeLabel.setText("Verbleibend: "
					+ ((timeMs > -1) ? timeTk.richTimeStringFromLong(timeMs) : "Unbekannt"));

			repaint();
		}
	}

	public long getElapsedTime() {
		return System.currentTimeMillis() - timeOperationStarted;
	}

	/**
	 * Returns the estimated time to finish in milliseconds
	 * @return
	 */
	public abstract long getEstimatedTime();

	public abstract void onCancel(boolean cancelled);

	public abstract int getProgress();

	public abstract String getActualJobName();

	public class ValuesTable extends JTable {

		private static final long serialVersionUID = 3327940387606547379L;

		public ValuesTable() {
			this.setModel(new ProgressValuesListModel(LiveMonitoring
					.getProgressValues()));
		}

	}

	public int getValuePercent() {
		return (int) Math.round((double) progress.getValue()
				/ (double) progress.getMaximum() * 100);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelBtn) {
			if (!finished) {
				int n = JOptionPane.showConfirmDialog(this,
						"Wirklich beenden?", "Simulation beenden",
						JOptionPane.YES_NO_OPTION);

				if (n != JOptionPane.OK_OPTION)
					return;

			}
			this.setVisible(false);
			onCancel(!finished);
		}
	}

	public void setFinished() {
		cancelBtn.setText("Fenster schließen");
		finished = true;
	}

}
