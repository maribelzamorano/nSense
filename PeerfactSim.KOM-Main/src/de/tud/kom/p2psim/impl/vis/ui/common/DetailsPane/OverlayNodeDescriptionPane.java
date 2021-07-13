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


package de.tud.kom.p2psim.impl.vis.ui.common.DetailsPane;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.tud.kom.p2psim.impl.vis.model.overlay.VisOverlayNode;

/**
 * Zeigt die Beschreibung eines Knotens (ohne mit ihm verbundene Metriken) an.
 * 
 * @author leo <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class OverlayNodeDescriptionPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4703682668232902573L;

	private static ImageIcon NODE_THUMB = new ImageIcon(
			"images/icons/model/OverlayNode32_32.png");

	VisOverlayNode node;

	JLabel nodetitle = new JLabel();

	public OverlayNodeDescriptionPane(VisOverlayNode node) {
		this.node = node;
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;

		this.add(new JLabel(NODE_THUMB));

		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;

		this.nodetitle.setText(node.getName());
		this.nodetitle.setFont(this.nodetitle.getFont().deriveFont(Font.BOLD));
		this.add(nodetitle, c);

		c.gridx = 1;
		c.gridy = 1;

		this.add(new JLabel("Peer"), c);

	}

}
