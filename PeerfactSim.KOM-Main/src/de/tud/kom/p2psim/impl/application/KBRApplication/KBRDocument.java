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


package de.tud.kom.p2psim.impl.application.KBRApplication;

import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.api.storage.Document;

/**
 * Document used in the KBRApplication
 * 
 * @author Julius Ruckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class KBRDocument implements Document {

	OverlayKey key;

	/**
	 * @param key
	 *            the key of the document
	 */
	public KBRDocument(OverlayKey key) {
		this.key = key;
	}

	@Override
	public OverlayKey getKey() {
		return key;
	}

	@Override
	public int getPopularity() {
		return 0;
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public State getState() {
		return null;
	}

	@Override
	public void setKey(OverlayKey key) {

	}

	@Override
	public void setPopularity(int popularity) {

	}

	@Override
	public void setSize(long newSize) {

	}

	@Override
	public void setState(State state) {

	}

}
