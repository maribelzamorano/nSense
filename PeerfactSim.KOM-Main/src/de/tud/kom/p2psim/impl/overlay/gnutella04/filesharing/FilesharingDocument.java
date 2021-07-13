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


package de.tud.kom.p2psim.impl.overlay.gnutella04.filesharing;

import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.api.storage.Document;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class FilesharingDocument implements Document {

	private OverlayKey key;

	private Integer rank;

	public FilesharingDocument(Integer rank) {
		this.rank = rank;
		key = new FilesharingKey(rank);
	}

	public OverlayKey getKey() {
		return this.key;
	}

	public int getPopularity() {
		return rank;
	}

	public long getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setKey(OverlayKey key) {
		// TODO Auto-generated method stub

	}

	public void setPopularity(int popularity) {
		// TODO Auto-generated method stub

	}

	public void setSize(long newSize) {
		// TODO Auto-generated method stub

	}

	public void setState(State state) {
		// TODO Auto-generated method stub

	}

	public String toString() {
		return rank.toString();
	}

}
