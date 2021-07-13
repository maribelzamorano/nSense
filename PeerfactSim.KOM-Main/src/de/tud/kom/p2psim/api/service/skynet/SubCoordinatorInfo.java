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

import de.tud.kom.p2psim.impl.skynet.SupportPeerInfo;
import de.tud.kom.p2psim.impl.skynet.attributes.AttributeSubCoordinatorInfo;
import de.tud.kom.p2psim.impl.skynet.metrics.MetricsSubCoordinatorInfo;

/**
 * Just a marker-interface to highlight the classes, which implement
 * {@link AliasInfo} and additionally contain the property of a Sub-Coordinator.
 * Currently, the <code>SubCoordinatorInfo</code>-interface is used to mark
 * {@link MetricsSubCoordinatorInfo} and {@link AttributeSubCoordinatorInfo} and
 * to dissociate the two classes from {@link SupportPeerInfo}, which also
 * implements the <code>AliasInfo</code>-interface.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public interface SubCoordinatorInfo {
	// Just a marker-interface
}
