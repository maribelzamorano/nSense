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


package de.tud.kom.p2psim.impl.vis.model.overlay;

import javax.swing.ImageIcon;

import de.tud.kom.p2psim.impl.vis.model.ModelIterator;
import de.tud.kom.p2psim.impl.vis.model.flashevents.FlashEvent;
import de.tud.kom.p2psim.impl.vis.util.visualgraph.VisualGraph;

/**
 * Overlay-Verbindung, die theoretisch <b>über keine Dauer verfügt</b>, d.h.
 * nur einmal kurz zu sehen sein soll (aufblitzen soll, daher "Flash"), z.B. um
 * das Versenden von Nachrichten zwischen Knoten zu repräsentieren. Wird nicht
 * ins Datenmodell eingetragen.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 19.10.2008
 * 
 */
public class FlashOverlayEdge extends VisOverlayEdge implements FlashEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1794025198513940542L;

	protected static final ImageIcon REPR_ICON = new ImageIcon(
			"images/icons/model/OverlayEdgeMsg16_16.png");

	/**
	 * Gibt an, ob die Kante gezeichnet wurde. Wird bei jedem Durchlauf
	 * zurückgesetzt.
	 */
	protected boolean painted = false;

	protected boolean removable = false;

	public FlashOverlayEdge(VisOverlayNode a, VisOverlayNode b) {
		super(a, b);
	}

	/*
	 * public void notifyPainted() { if (painted) { System.out.println(this +
	 * ": Edge Removed"); this.remove(); //Beim zweiten Zeichenversuch (der
	 * unterdrückt wird) } //wird die Kante gelöscht. Bis dahin bleibt sie
	 * jedoch //im Graphen um z.B. anklickbar zu sein. else { painted = true;
	 * System.out.println(this + ": painted=true"); } }
	 */

	@Override
	public void setGraph(VisualGraph<VisOverlayNode, VisOverlayEdge> g) {
		System.out
				.println("Warnung: Eine FlashOverlayEdge sollte nicht ins Datenmodell eingesetzt werden.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void iterate(ModelIterator it) {
		it.flashOverlayEdgeVisited(this);
	}

	@Override
	public ImageIcon getRepresentingIcon() {
		return REPR_ICON;
	}
}
