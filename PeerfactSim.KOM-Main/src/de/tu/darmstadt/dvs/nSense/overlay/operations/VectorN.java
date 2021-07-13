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
 * A float precision, one dimensional array vector class to describe a node's
 * position.
 * 
 * @author Maribel Zamorano
 * @version 24/11/2013
 * 
 */
public class VectorN extends TupleN implements Serializable {

	private static final long serialVersionUID = -8586957802117903534L;

	public VectorN(float vector[]) {
		super(vector);
	}

	public final VectorN cross(VectorN v1, VectorN v2) {
		final int n = v1.getSize();
		for (int i = 0; i < n; i++) {
			this.setValue(
					i,
					(v1.getValue((i + 1) % n) * v2.getValue((i + 2) % n))
							- (v1.getValue((i + 2) % n) * v2.getValue((i + 1)
									% n)));
		}
		return (this);
	}

	public final float dot(VectorN v2) {
		if (this.getSize() != v2.getSize())
			throw new IllegalArgumentException("this.size:" + this.getSize()
					+ " != v1.size:" + v2.getSize());

		float sum = 0.0f;
		for (int i = 0; i < getSize(); ++i)
			sum += this.getValue(i) * v2.getValue(i);

		return (sum);
	}

	public final float getNorm() {
		return (float) (Math.sqrt(getNormSquared()));
	}

	public final float getNormSquared() {
		float s = 0.0f;
		for (int i = 0; i < coordinates.length; i++) {
			s += coordinates[i] * coordinates[i];
		}
		return (s);
	}

	public float length() {
		return (getNorm());
	}

	public float lengthSquared() {
		return (getNormSquared());
	}

	public final VectorN normalize() {
		final float len = getNorm();
		for (int i = 0; i < coordinates.length; i++)
			divValue(i, len);
		return (this);
	}

	public final VectorN normalize(float v[]) {
		set(v);
		normalize();
		return (this);
	}

	public double distance(VectorN target) {
		float sum = 0;
		for (int i = 0; i < this.getSize(); i++) {
			sum += ((this.getValue(i) - target.getValue(i)) * (this.getValue(i) - target
					.getValue(i)));
		}
		return Math.sqrt(sum);
	}

}
