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


package de.tud.kom.p2psim.impl.overlay.gnutella04;

import java.math.BigInteger;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class GnutellaOverlayNodeFactory implements ComponentFactory {

	private final static short port = 123;
	private static long id = 0;
	
	private int numConn;
	private long delayAcceptConnection;
	private long refresh;
	private long contactTimeout;
	private long descriptorTimeout;
	
	public Component createComponent(Host host) {
		return new GnutellaOverlayNode(host.getTransLayer(), newGnutellaOverlayID(), this.numConn, this.delayAcceptConnection, this.refresh, this.contactTimeout, this.descriptorTimeout, port);
	}

	public GnutellaOverlayID newGnutellaOverlayID() {
		this.id += 1;
		System.out.println(new GnutellaOverlayID(BigInteger.valueOf(this.id)));
		return new GnutellaOverlayID(BigInteger.valueOf(this.id));
	}
	
	public void setNumConn(int numConn) {
		this.numConn = numConn;
	}

	public void setDelayAcceptConnection(long delayAcceptConnection) {
		this.delayAcceptConnection = delayAcceptConnection;
	}

	public void setRefresh(long refresh) {
		this.refresh = refresh;
	}

	public void setContactTimeout(long contactTimeout) {
		this.contactTimeout = contactTimeout;
	}
	
	public void setDescriptorTimeout(long descriptorTimeout) {
		this.descriptorTimeout = descriptorTimeout;
	}
}
