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

package de.tu.darmstadt.dvs.nSense.overlay.util;

/**
 * The class is for the sequence number. It contains a short number and try to
 * handle the overflow of the sequence number.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @author Maribel Zamorano
 * @version 12/20/2013
 * 
 */
public class SequenceNumber {

	/**
	 * A big number in the near of MAX_VALUE of short. It is used for a
	 * comparison of the sequence number. If a short overflow occurred, then
	 * will be the number reset to 0 and the comparison will be failed.
	 * Therefore it will calculate the difference between the two numbers and if
	 * the distance greater as this, then can made a decision about the
	 * comparison.
	 */
	private final static int SHORT_DISTANCE_AFTER_RESET = 32500;

	private final short seqNr;

	public SequenceNumber() {
		seqNr = 0;
	}

	public SequenceNumber(short sequenceNumber) {
		if (sequenceNumber < 0) {
			seqNr = 0;
		} else {
			this.seqNr = sequenceNumber;
		}
	}

	public SequenceNumber incSeqNr() {
		// do a reset before the short overflow
		if (seqNr == Short.MAX_VALUE || seqNr < 0) {
			return new SequenceNumber((short) 0);
		} else {
			return new SequenceNumber((short) (seqNr + 1));
		}
	}

	private short getSeqNr() {
		return seqNr;
	}

	public boolean equals(SequenceNumber sequenceNr) {
		return this.seqNr == sequenceNr.getSeqNr();
	}

	/**
	 * Check if this sequence number is newer as the given sequenceNr.
	 * 
	 * @param sequenceNr
	 *            A sequence number
	 * @return <code>true</code> if this sequence number is newer then the given
	 *         sequenceNr, otherwise <code>false</code>
	 */
	public boolean isNewerAs(SequenceNumber sequenceNr) {
		// Second condition is for a check after a reset of a sequence number.
		// (Reset: sequence number is set to 0 before a overflow occurred).
		return (this.getSeqNr() > sequenceNr.getSeqNr() || sequenceNr
				.getSeqNr() - this.getSeqNr() > SHORT_DISTANCE_AFTER_RESET);
	}

	/**
	 * Determine the difference between the stored and the given sequence
	 * number. The difference is a positive number. It computes only the right
	 * value, if the two numbers not so far away.
	 * 
	 * @param sequenceNr
	 *            A sequence number
	 * @return The difference between the stored and the given sequence number.
	 *         The difference is a positive number.
	 */
	public int difference(SequenceNumber sequenceNr) {
		int diff = Math.abs(this.getSeqNr() - sequenceNr.getSeqNr());
		if (diff > SHORT_DISTANCE_AFTER_RESET) {
			int min = Math.min(this.getSeqNr(), sequenceNr.getSeqNr());
			int max = Math.max(this.getSeqNr(), sequenceNr.getSeqNr());
			diff = Math.abs(Short.MAX_VALUE + min - max);
		}
		return diff;
	}

	@Override
	public String toString() {
		return new Short(seqNr).toString();
	}
}
