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


package de.tud.kom.p2psim.impl.service.aggr.gossip;

import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class DefaultConfig implements IConfiguration {

	static final long REQ_RESP_TIMEOUT = 1 * Simulator.SECOND_UNIT;

	static final int RESTART_COUNT = 30; // 100 peers => 100, 1000 peers =>

	static final long UPDATE_PERIOD = 10 * Simulator.SECOND_UNIT; // 1s,
																			// 5s

	static final boolean GATHER_MODE = true;

	static final int CONCURRENT_NC_LEADERS = 10;

	private static final long RESYNC_TIMEOUT = 2 * Simulator.SECOND_UNIT;

	private static final int INITIALLY_ASSUMED_NODE_COUNT = 100;

	private static final int NODE_COUNT_START_CYCLE = 0;

	private static boolean LOCK_MERGE_ON_RPC = true;

	@Override
	public long getReqRespTimeout() {
		return REQ_RESP_TIMEOUT;
	}

	@Override
	public int getRestartCount() {
		return RESTART_COUNT;
	}

	@Override
	public long getUpdatePeriod() {
		return UPDATE_PERIOD;
	}

	@Override
	public boolean gatherMode() {
		return GATHER_MODE;
	}

	@Override
	public int getConcurrentNCLeaders() {
		return CONCURRENT_NC_LEADERS;
	}

	@Override
	public int getInitiallyAssumedNodeCount() {
		return INITIALLY_ASSUMED_NODE_COUNT;
	}

	@Override
	public int getNodeCountStartCycle() {
		return NODE_COUNT_START_CYCLE;
	}

	@Override
	public long getResyncTimeout() {
		return RESYNC_TIMEOUT;
	}

	@Override
	public boolean shallLockMergeOnRPC() {
		return LOCK_MERGE_ON_RPC;
	}

}
