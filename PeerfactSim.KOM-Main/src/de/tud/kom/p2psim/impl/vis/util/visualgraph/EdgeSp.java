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


package de.tud.kom.p2psim.impl.vis.util.visualgraph;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class EdgeSp<TNode> {

	TNode n1;

	TNode n2;

	public EdgeSp(TNode n1, TNode n2) {
		super();
		this.n1 = n1;
		this.n2 = n2;
	}

	public TNode getN1() {
		return n1;
	}

	public TNode getN2() {
		return n2;
	}

	public boolean equals(Object other) {
		if (!(other instanceof EdgeSp))
			return false;
		EdgeSp otherSbn = ((EdgeSp) other);
		return otherSbn.n1 == this.n1 && otherSbn.n2 == this.n2
				|| otherSbn.n2 == this.n1 && otherSbn.n1 == this.n2;
	}

	public int hashCode() {
		return n1.hashCode() + n2.hashCode();
	}

}
