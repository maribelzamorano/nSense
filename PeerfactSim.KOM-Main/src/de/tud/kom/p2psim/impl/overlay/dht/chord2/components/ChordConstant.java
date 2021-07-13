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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.components;


/**
 * 
 * All constant values 
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordConstant {

	public static final long BYTE_SIZE = 1;

	public static final long BOOLEAN_SIZE = 1;

	public static final long CHAR_SIZE = 2;

	public static final long SHORT_SIZE = 2;

	public static final long INT_SIZE = 4;

	public static final long FLOAT_SIZE = 4;

	public static final long DOUBLE_SIZE = 8;

	public static final long LONG_SIZE = 8;
	
	public final static long CHORD_ID_SIZE = 20;

	public final static long NET_ID_SIZE = 5;

	public final static long CHORD_CONTACT_SIZE = CHORD_ID_SIZE + NET_ID_SIZE;

}
