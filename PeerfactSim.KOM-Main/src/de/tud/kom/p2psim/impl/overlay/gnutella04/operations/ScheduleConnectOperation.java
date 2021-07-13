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


package de.tud.kom.p2psim.impl.overlay.gnutella04.operations;

import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayNode;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class ScheduleConnectOperation extends
		AbstractOperation<GnutellaOverlayNode, Object> {

	private boolean active = true;

	private long delay;

	public ScheduleConnectOperation(GnutellaOverlayNode component, long delay,
			OperationCallback<Object> callback) {
		super(component, callback);
		this.delay = delay;
	}

	@Override
	protected void execute() {
		if (active) {
			this.getComponent().scheduleConnect(this);
		}
		if (active) {
			this.scheduleWithDelay(delay);
		}
	}

	@Override
	public Object getResult() {
		return this;
	}

	public void start() {
		active = true;
	}

	public void stop() {
		active = false;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

}
