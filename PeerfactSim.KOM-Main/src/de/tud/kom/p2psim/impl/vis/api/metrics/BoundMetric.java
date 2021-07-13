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


package de.tud.kom.p2psim.impl.vis.api.metrics;

/**
 * Eine gebundene Metrik ist eine Metrik, die ihren Wert ausspuckt für einen
 * Knoten/eine Kante, an die sie gebunden wurde, und damit ohne eine explizite
 * Angabe eines Knotens/einer Kante auskommt.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public abstract class BoundMetric extends Metric {

	protected Metric m;

	public BoundMetric(Metric m) {
		this.m = m;
	}

	/**
	 * Gibt den Wert der Metrik zum aktuellen Zeitpunkt zurück, abhängig vom
	 * Objekt, an das sie gebunden wurde.
	 * 
	 * @return
	 */
	public abstract String getValue();

	@Override
	public String getName() {
		return m.getName();
	}

	@Override
	public boolean isActivated() {
		return m.isActivated();
	}

	@Override
	public void setActivated(boolean activated) {
		m.setActivated(activated);
	}

	@Override
	public String getUnit() {
		return m.getUnit();
	}

	@Override
	public boolean isNumeric() {
		return m.isNumeric();
	}

}
