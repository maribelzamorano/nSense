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


package de.tud.kom.p2psim.impl.vis.ui.common.dialogs;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.model.VisDataModel;
import de.tud.kom.p2psim.impl.vis.util.Config;

/**
 * Dateiauswahldialog, mit dem neue Aufzeichnungen geladen und gespeichert
 * werden können.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class RecordFileChooser extends JFileChooser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1308955789418633424L;

	static final String CFG_LAST_PATH = "UI/Dialogs/LoadSave/lastPath";

	static final String FILE_EXTENSION = "peerfact";

	public RecordFileChooser() {
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Visualisierungsaufzeichnungen", FILE_EXTENSION);
		this.setFileFilter(filter);

		this.setCurrentDirectory(new File(Config.getValue(CFG_LAST_PATH, "")));
	}

	/**
	 * Fragt nach einer zu öffnenden Datei. Wird eine Datei ausgewählt, wird
	 * sie geöffnet und bei Erfolg true zurückgegeben, sonst false.
	 * 
	 * @return
	 */
	public boolean askForOpen() {
		if (this.showOpenDialog(Controller.getUIMainWindow()) == JFileChooser.APPROVE_OPTION) {
			this.openFile(this.getSelectedFile());
			setLastDirectory();
			return true;
		}
		return false;
	}

	/**
	 * Fragt, wo die Datei gespeichert werden soll. Wird eine Datei ausgewählt,
	 * wird sie geöffnet und bei Erfolg true zurückgegeben, sonst false.
	 * 
	 * @return
	 */
	public boolean askForSave() {
		if (this.showSaveDialog(Controller.getUIMainWindow()) == JFileChooser.APPROVE_OPTION) {
			this.saveFile(this.validateFileName(this.getSelectedFile()));
			setLastDirectory();
			return true;
		}
		return false;
	}

	private void setLastDirectory() {
		Config.setValue(CFG_LAST_PATH, this.getCurrentDirectory()
				.getAbsolutePath());
	}

	private void saveFile(File selectedFile) {
		try {
			Controller.getModel().saveTo(selectedFile);
			Controller.getUIMainWindow().setTitleFileName(
					selectedFile.getName());
		} catch (IOException e) {
			System.out.println("Exception" + e.getMessage()
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void openFile(File selectedFile) {
		try {
			Controller.loadModelFrontend(VisDataModel.fromFile(selectedFile));
			Controller.getModel().setName(selectedFile.getName());
		} catch (IOException e) {
			System.out.println("Exception");
			e.printStackTrace();
		}
	}

	private File validateFileName(final File file) {

		FileFilter filter = this.getFileFilter();

		if (filter.accept(file)) {
			return file;
		}
		String fileName = file.getName();
		final int index = fileName.lastIndexOf(".");
		if (index > 0) {
			fileName = fileName.substring(0, index);
		}

		final String newFileName = fileName + "." + FILE_EXTENSION;

		return new File(file.getParent(), newFileName);
	}

}
