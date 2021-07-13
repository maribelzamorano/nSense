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



package de.tud.kom.p2psim.impl.overlay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import de.tud.kom.p2psim.api.overlay.DHTObject;

/**
 * A wrapper class that adds a timestamp to any value object. The value is
 * internally represented as an Object and can both be returned as a byte array
 * and as an object.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 09/10/07
 */
public class TimestampedDHTObject implements DHTObject {

	private long releaseTimestamp;

	private Object value;

	public TimestampedDHTObject(Object obj, long releaseTimestamp) {
		this.releaseTimestamp = releaseTimestamp;
		this.value = obj;
	}

	/**
	 * Returns the timestamp at which this value has been released
	 * 
	 * @return the release timestamp
	 */
	public long getReleaseTimestamp() {
		return this.releaseTimestamp;
	}

	/**
	 * Returns the value as an Object.
	 * 
	 * @return the value as an Object
	 */
	public Object getObject() {
		return this.value;
	}

	/**
	 * Returns the value as a byte array.
	 * 
	 * @return byte array of the value
	 */
	public byte[] getBytes() {
		// Converts value to a byte array
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oout;
		try {
			oout = new ObjectOutputStream(bout);
			oout.writeObject(value);
			oout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bout.toByteArray();
	}

	public String toString() {
		return "value (releaseTimestamp=" + this.releaseTimestamp + " value="
				+ this.value + ")";
	}

	@Override
	public int getTransmissionSize() {
		return getBytes().length;
	}

}
