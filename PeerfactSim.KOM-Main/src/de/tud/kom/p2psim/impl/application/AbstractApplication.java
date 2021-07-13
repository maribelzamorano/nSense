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



package de.tud.kom.p2psim.impl.application;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.application.Application;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Abstract implementation of an application, which can be used for subclassing.
 * 
 * @author Konstantin Pussep <peerfact@kom.tu-darmstadt.de>
 * @author Sebastian Kaune
 * @version 3.0, 10.12.2007
 * 
 */
// TODO reconsider functionality
public abstract class AbstractApplication implements Application {

	protected static final Logger log = SimLogger
			.getLogger(AbstractApplication.class);

	private Host host;

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public int close(OperationCallback callback) {
		return Operations.scheduleEmptyOperation(this, callback);
	}

	public int start(OperationCallback callback) {
		return Operations.scheduleEmptyOperation(this, callback);
	}

	public void calledOperationFailed(Operation op) {
		// FIXME inform the monitor here

	}

	public void calledOperationSucceeded(Operation op) {
		// FIXME inform the monitor here

	}

}
