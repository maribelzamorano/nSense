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

package de.tud.kom.p2psim.impl.service.mercury.dht;

import java.util.List;

import de.tud.kom.p2psim.impl.overlay.BootstrapManager;
import de.tud.kom.p2psim.impl.service.mercury.MercuryAttributePrimitive;
import de.tud.kom.p2psim.impl.service.mercury.MercuryContact;

/**
 * Information returned to a service instance by the bootstrapper. This includes
 * the bootstrapper-Instance for the used Overlay <code>bootstrap</code>, the
 * attribute this service instance is responsible for and the global
 * Bootstrapper for Mercury which has knowledge of all hubs.
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryBootstrapInfo {
	
	private BootstrapManager bootstrap;
	private MercuryAttributePrimitive ownAttribute;
	private MercuryBootstrap globalBootstrap;
	
	/**
	 * Create a Bootstrap-Info instance for one service-instance
	 * 
	 * @param bootstrap
	 * @param ownAttribute
	 * @param globalBootstrap
	 */
	public MercuryBootstrapInfo(BootstrapManager bootstrap, 
			MercuryAttributePrimitive ownAttribute, 
			MercuryBootstrap globalBootstrap) {
		this.bootstrap = bootstrap;
		this.ownAttribute = ownAttribute;
		this.globalBootstrap = globalBootstrap;
	}

	public BootstrapManager getBootstrap() {
		return bootstrap;
	}
	
	/**
	 * the attribute this service instance is responsible for (ist is in the hub
	 * for this attribute)
	 * 
	 * @return
	 */
	public MercuryAttributePrimitive getOwnAttribute() {
		return ownAttribute;
	}
	
	/**
	 * get a List of Contacts in other Hubs
	 * 
	 * @return
	 */
	public List<MercuryContact> getContactsInOtherHubs() {
		return globalBootstrap.getRandomContactForEachAttribute();
	}

}
