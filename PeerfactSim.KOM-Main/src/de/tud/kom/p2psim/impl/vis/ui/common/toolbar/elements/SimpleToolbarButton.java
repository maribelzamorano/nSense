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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import de.tud.kom.p2psim.impl.vis.controller.commands.Command;

/**
 * Diese Klasse stellt einen Knopf dar, den man drücken kann, damit er
 * ein oder mehrere Kommandos, implementiert nach dem Interface
 * controller.commands.Command, ausführt.
 * Einfach das Kommando mittels addCommand(Command c) anhängen, und
 * es wird ausgeführt, sobald man auf den Knopf drückt.
 * @author Leo <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class SimpleToolbarButton extends JButton{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7278102175284474958L;
	
	public SimpleToolbarButton() { 
		super();
	}
	
	public SimpleToolbarButton(ImageIcon icon) { 
		super(icon);
	}
	
	public void addCommand (Command c) {
		this.addActionListener(new CommandAdapter(c));
	}
	
	public class CommandAdapter implements ActionListener{
		
		Command adaptedCommand;
		
		public CommandAdapter(Command c) {
			this.adaptedCommand = c;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {			
			this.adaptedCommand.execute();
		}
		
	}
	
}
