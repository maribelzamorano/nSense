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


package de.tud.kom.p2psim.api.service.skynet;

import java.io.File;

import de.tud.kom.p2psim.Constants;

/**
 * This interface defines constants for SkyNet, whose values are often needed or
 * must be predefined. The constants can be atomic, or consist of others.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public interface SkyNetConstants {

	// This section contains the sizes of simple data-types like int, double etc
	// in bytes

	/**
	 * Contains the size of <code>byte</code> (in bytes)
	 */
	public static final long BYTE_SIZE = 1;

	/**
	 * Contains the size of <code>boolean</code> (in bytes)
	 */
	public static final long BOOLEAN_SIZE = 1;

	/**
	 * Contains the size of <code>char</code> (in bytes)
	 */
	public static final long CHAR_SIZE = 2;

	/**
	 * Contains the size of <code>short</code> (in bytes)
	 */
	public static final long SHORT_SIZE = 2;

	/**
	 * Contains the size of <code>int</code> (in bytes)
	 */
	public static final long INT_SIZE = 4;

	/**
	 * Contains the size of <code>float</code> (in bytes)
	 */
	public static final long FLOAT_SIZE = 4;

	/**
	 * Contains the size of <code>double</code>
	 */
	public static final long DOUBLE_SIZE = 8;

	/**
	 * Contains the size of <code>long</code> (in bytes)
	 */
	public static final long LONG_SIZE = 8;

	/**
	 * This variable is used to divide the value of the simulator, since the
	 * simulator calculates the time in micro-seconds. The result expresses the
	 * time in seconds.
	 */
	public static final long DIVISOR_FOR_SECOND = 1000000;

	// This section contains the sizes of Address-Objects which are used to
	// address messages
	/**
	 * Contains the size of <code>OverlayID</code> (in bytes). This size must
	 * equal the size of <code>SkyNetID</code> to enable a correct
	 * address-resolution.
	 */
	public static final long OVERLAY_ID_SIZE = 20;

	/**
	 * Contains the size of <code>TransInfo</code> (in bytes)
	 */
	public static final long TRANS_INFO_SIZE = LONG_SIZE + SHORT_SIZE;

	/**
	 * Contains the size of <code>OverlayContact</code> for the overlay-type
	 * defined in <code>de.tud.kom.p2psim.impl.overlay.dht.napster</code> and
	 * its sub-packages.
	 */
	public static final long NAPSTER_OVERLAY_CONTACT_SIZE = TRANS_INFO_SIZE
			+ OVERLAY_ID_SIZE;

	/**
	 * Contains the size of <code>ResponsibleForKeyResult</code> (in bytes)
	 */
	public static final long RESPONSIBILITY_FOR_KEY_RESULT_SIZE = NAPSTER_OVERLAY_CONTACT_SIZE
			+ BOOLEAN_SIZE;

	/**
	 * Contains the size of <code>SkyNetID</code> (in bytes).This size must
	 * equal the size of <code>OverlayID</code> to enable a correct
	 * address-resolution.
	 */
	public static final long SKY_NET_ID_SIZE = 20;

	/**
	 * Contains the size of <code>SkyNetNodeInfo</code> (in bytes)
	 */
	public static final long SKY_NET_NODE_INFO_SIZE = SKY_NET_ID_SIZE
			+ SKY_NET_ID_SIZE + TRANS_INFO_SIZE;

	// Size of an metric-aggregate
	/**
	 * Contains the size of <code>MetricAggregate</code> (in bytes)
	 */
	public static final long METRIC_AGGREGATE = DOUBLE_SIZE * 4 + LONG_SIZE * 3
			+ 1 * INT_SIZE;

	// This section contains the sizes of elements related to queries
	/**
	 * Contains the size of <code>QueryReplyingPeer</code> (in bytes)
	 */
	public static final long QUERY_REPLYING_PEER_SIZE = SkyNetConstants.SKY_NET_NODE_INFO_SIZE
			+ INT_SIZE + BOOLEAN_SIZE;

	/**
	 * Contains an estimation of the size of <code>QueryCondition</code> (in
	 * bytes)
	 */
	public static final long QUERY_CONDITION_SIZE_ESTIMATE = 25;

	// estimated size of an average attributeEntry
	/**
	 * Contains an estimate of the size of <code>AttributeEntry</code> (in
	 * bytes), which contains all attributes from one SkyNet-node
	 */
	public static final long ATTRIBUTE_ENTRY_SIZE_ESTIMATE = 300;

	public static final String COMMON_SIMULATIONS_PATH = Constants.OUTPUTS_DIR
			+ File.separatorChar + "skynetSimulations";

	public static final String GNU_SCRIPTS_PATH = "gnuScriptsForSkyNet";

	public static final String POSTPROCESSING_PROPERTIES_FILE = "batchSettings.properties";
}
