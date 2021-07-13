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

import de.tu.darmstadt.dvs.nSense.overlay.NSenseID;

/**
 * A class containing the constants for the overlay nSense.
 * 
 * @author Christoph Muenker <peerfact@kom.tu-darmstadt.de>
 * @author Maribel Zamorano
 * @version 12/20/2013
 */
public class Constants {

	/**
	 * Enum for Messages.
	 * 
	 * @author Christoph Muenker
	 * @version 10/15/2010
	 */
	public enum MSG_TYPE {
		/**
		 * Describes an identifier for a position update message
		 */
		POSITION_UPDATE,
		/**
		 * Describes an identifier for a sensor request message
		 */
		SENSOR_REQUEST,
		/**
		 * Describes an identifier for a forward message
		 */
		FORWORD,
		/**
		 * Describes an identifier for a sensor response message
		 */
		SENSOR_RESPONSE,
		/**
		 * Describes an identifier for an action message
		 */
		ACTION_MSG
	}

	/**
	 * An empty overlay ID in nSense.
	 */
	public final static NSenseID EMPTY_NSENSE_ID = new NSenseID(-1);

	/**
	 * Describes the size in bytes of the IP.
	 */
	public final static int BYTE_SIZE_OF_IP = 4;

	/**
	 * Describes the size in bytes of the port.
	 */
	public final static int BYTE_SIZE_OF_PORT = 2;

	/**
	 * Describes the size in byte of the counter for the number of hops in a
	 * message.
	 */
	public final static int BYTE_SIZE_OF_HOP_COUNT = 1;

	/**
	 * Describes the size in byte to distinguish a message.
	 */
	public final static int BYTE_SIZE_OF_MSG_TYPE = 1;

	/**
	 * Describes the size in byte of a sequence number. (short)
	 */
	public final static int BYTE_SIZE_OF_SEQ_NR = 2;

	/**
	 * Describes the size in bytes of the vision range (one integer).
	 */
	public final static int BYTE_SIZE_OF_RADIUS = 4;

	/**
	 * Describes the size in bytes of a Point in a 2D plane (two integers!).// to-do cambiar a diferentes dimensiones
	 */
	public final static int BYTE_SIZE_OF_POINT = 8;

	/**
	 * Describes the size in byte of the identifier of a sector.
	 */
	public final static int BYTE_SIZE_OF_SECTOR_ID = 1;
}
