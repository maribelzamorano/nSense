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


package de.tud.kom.p2psim.impl.vis.ui.common.toolbar.elements;

import javax.swing.ImageIcon;

import de.tud.kom.p2psim.impl.vis.controller.Controller;
import de.tud.kom.p2psim.impl.vis.controller.commands.PlayPause;
import de.tud.kom.p2psim.impl.vis.controller.player.PlayerEventListener;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class PlayPauseButton extends SimpleToolbarButton implements
		PlayerEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3631493274100117920L;

	static ImageIcon iconPlay = new ImageIcon("images/icons/PlayButton.png");

	static ImageIcon iconPause = new ImageIcon("images/icons/PauseButton.png");

	public PlayPauseButton() {
		this.setIcon(iconPlay);
		// this.setText("Play");
		this.setToolTipText("Abspielen");

		Controller.getPlayer().addEventListener(this);

		this.addCommand(new PlayPause());

	}

	public void setPlay() {
		this.setIcon(iconPlay);
	}

	@Override
	public void forward() {
		//Nothing to do
	}

	@Override
	public void pause() {
		this.setIcon(iconPlay);

	}

	@Override
	public void play() {
		this.setIcon(iconPause);

	}

	@Override
	public void reverse() {
		//Nothing to do
	}

	@Override
	public void stop() {
		this.setIcon(iconPlay);

	}

	@Override
	public void speedChange(double speed) {
		//Nothing to do
	}

	@Override
	public void quantizationChange(double quantization) {
		// TODO Auto-generated method stub

	}

}
