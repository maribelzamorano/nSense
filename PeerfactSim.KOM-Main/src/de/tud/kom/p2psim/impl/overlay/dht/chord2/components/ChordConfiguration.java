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

import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This class contains all configurable parameters
 * 
 * @author Minh Hoang Nguyen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class ChordConfiguration {

	/**
	 * To enable the evaluation functions
	 */
	public final static boolean DO_CHORD_EVALUATION = false;

	/**
	 * Transport protocol, which should be used in Overlay
	 */
	public final static TransProtocol TRANSPORT_PROTOCOL = TransProtocol.UDP;

	/**
	 * If no reply is received in this period, assumably messages loss
	 */
	public final static long MESSAGE_TIMEOUT = 10 * Simulator.SECOND_UNIT;

	/**
	 * If no reply is received in this period, assumably messages loss
	 */
	public final static long OPERATION_TIMEOUT = 2 * Simulator.MINUTE_UNIT;

	/**
	 * If no reply is received, the message will be resent until a certain times
	 * again
	 */
	public final static int MESSAGE_RESEND = 3;

	/**
	 * If an operation failed, the operation will be executed until a certain
	 * times again
	 */
	public final static int OPERATION_MAX_REDOS = 3;

	/**
	 * Maximum time for performing a lookup operation.
	 */
	public final static long LOOKUP_TIMEOUT = 1 * Simulator.MINUTE_UNIT;

	/**
	 * The interval between two operations, which refresh next direct successor
	 */
	public final static long UPDATE_SUCCESSOR_INTERVAL = 30 * Simulator.SECOND_UNIT;

	/**
	 * The interval between two operations, which refresh finger point
	 */
	public final static long UPDATE_FINGERTABLE_INTERVAL = 30 * Simulator.SECOND_UNIT;

	/**
	 * The interval between two operations, which refresh a
	 * successor/predecessor in successors/predecessors list
	 */
	public final static long UPDATE_NEIGHBOURS_INTERVAL = 30 * Simulator.SECOND_UNIT;

	/**
	 * number of successors/predecessors which are stored and used as back up
	 * for direct successor/predecessor
	 */
	public static int STORED_NEIGHBOURS = 3;

	/**
	 * message will be dropped if whose path exceeds this value
	 */
	public final static int MAX_HOP_COUNT = 50;

	/**
	 * This parameter is used by Gnuplot to plot metrics values by the time t
	 */
	public final static long METRIC_INTERVALL = 5 * Simulator.MINUTE_UNIT;

	/**
	 * The interval between two random lookup operations executed on one host.
	 */
	public final static long TIME_BETWEEN_RANDOM_LOOKUPS = 20 * Simulator.MINUTE_UNIT;

	/**
	 * Maximal time interval between a failed join operation and a retry
	 */
	public final static long MAX_WAIT_BEFORE_JOIN_RETRY = 5 * Simulator.MINUTE_UNIT;

}
