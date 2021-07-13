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

package de.tu.darmstadt.dvs.nSense.overlay.operations;

import java.io.Serializable;

/**
 * This is a base-class for float tuple VectorN
 * 
 * @author Maribel Zamorano
 * @version 24/11/2013
 * 
 */
public abstract class TupleN implements Serializable {

	private static final long serialVersionUID = -2497404838665880151L;
	protected float[] coordinates;
	protected final int N;

	public TupleN(float coordinates[]) {
		this.coordinates = coordinates;
		this.N = coordinates.length;
	}

	public final float divValue(int i, float v) {
		coordinates[i] /= v;
		return (this.coordinates[i]);
	}

	public final void get(float coordinates[]) {
		coordinates = this.coordinates;
	}

	public final int getSize() {
		return (N);
	}

	public final float getValue(int i) {
		return (this.coordinates[i]);
	}

	public final void set(float coordinates[]) {
		this.coordinates = coordinates;
	}

	public final void setValue(int i, float d) {
		this.coordinates[i] = d;
	}

	

}