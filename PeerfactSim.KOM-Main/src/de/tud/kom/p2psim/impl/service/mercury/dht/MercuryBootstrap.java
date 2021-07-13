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

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.overlay.DHTNode;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.impl.service.mercury.MercuryAttributePrimitive;
import de.tud.kom.p2psim.impl.service.mercury.MercuryContact;
import de.tud.kom.p2psim.impl.service.mercury.MercuryService;

/**
 * Interface for Different Mercury Bootstrappers. For each supported DHT-Overlay
 * a bootstrapper which implements this interface is needed
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public interface MercuryBootstrap {

	/**
	 * Set all available Attributes
	 * 
	 * @param attributes
	 */
	public void setAttributes(List<MercuryAttributePrimitive> attributes);

	/**
	 * Bootstrap-Info for the particular Service Instance. This Method should
	 * only be called once in every Service!
	 * 
	 * @return
	 */
	public MercuryBootstrapInfo getBootstrapInfo();
	
	/**
	 * Get a List of random MercuryContacts for every Attribute supported by the
	 * service (or the application). This is used to contact other Hubs
	 * 
	 * @return
	 */
	public List<MercuryContact> getRandomContactForEachAttribute();

	/**
	 * Create a and get the overlay node used by this service instance
	 * 
	 * @return
	 */
	public DHTNode createOverlayNode(MercuryBootstrapInfo bsInfo, Host host,
			short port);

	/**
	 * Allows to overwrite the overlay-ID before joining, to implement some kind
	 * of easy load balancing
	 * 
	 * @param service
	 * @return
	 */
	public void callbackOverlayID(MercuryService service);

	/**
	 * get an instance of the used ID-Mapping
	 * 
	 * @return
	 */
	public MercuryIDMapping getIDMapping();

	/**
	 * get Range of this Particular Service Instance, in terms of Overlay-IDs
	 * 
	 * @param neighbors
	 * @param service
	 * @return
	 */
	public OverlayID[] getRange(INeighborDeterminator neighbors,
			MercuryService service);

}
