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


package de.tud.kom.p2psim.impl.skynet;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.ComponentFactory;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.service.skynet.SkyNetConstants;
import de.tud.kom.p2psim.api.service.skynet.SkyNetSimulationType;
import de.tud.kom.p2psim.api.service.skynet.SkyNetSimulationType.SimulationType;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.components.KBRKademliaNodeGlobalKnowledge;
import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.napster.components.NapsterClientNode;
import de.tud.kom.p2psim.impl.skynet.addressresolution.Chord2AddressResolutionImpl;
import de.tud.kom.p2psim.impl.skynet.addressresolution.KademliaAddressResolutionImpl;
import de.tud.kom.p2psim.impl.skynet.addressresolution.NapsterAddressResolutionImpl;
import de.tud.kom.p2psim.impl.skynet.components.SkyNetNode;
import de.tud.kom.p2psim.impl.skynet.overlay2SkyNet.Chord2MetricsCollectorDelegator;
import de.tud.kom.p2psim.impl.skynet.overlay2SkyNet.Chord2TreeHandlerDelegator;
import de.tud.kom.p2psim.impl.skynet.overlay2SkyNet.KademliaMetricsCollectorDelegator;
import de.tud.kom.p2psim.impl.skynet.overlay2SkyNet.KademliaTreeHandlerDelegator;
import de.tud.kom.p2psim.impl.skynet.overlay2SkyNet.NapsterMetricsCollectorDelegator;
import de.tud.kom.p2psim.impl.skynet.overlay2SkyNet.NapsterTreeHandlerDelegator;
import de.tud.kom.p2psim.impl.transport.DefaultTransInfo;

/**
 * This class implements the interface {@link ComponentFactory} and is used to
 * initialize the SkyNet-component and to add it to a host. For further details,
 * we refer to {@link ComponentFactory}.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 04.12.2008
 * 
 */
public class SkyNetNodeFactory implements ComponentFactory {

	private short commonPort;

	private int simulationSize;

	public Component createComponent(Host host) {
		TransLayer transLayer = host.getTransLayer();
		NapsterClientNode napsterNode = (NapsterClientNode) host
				.getOverlay(NapsterClientNode.class);
		if (napsterNode != null) {
			return skyNetNodeOnNapster(napsterNode, transLayer);
		}
		ChordNode chordNode = (ChordNode) host.getOverlay(ChordNode.class);
		if (chordNode != null) {
			return skyNetNodeOnChord(chordNode, transLayer);
		}
		KBRKademliaNodeGlobalKnowledge<KademliaOverlayID> kademliaNode = (KBRKademliaNodeGlobalKnowledge<KademliaOverlayID>) host
				.getOverlay(KBRKademliaNodeGlobalKnowledge.class);
		if (kademliaNode != null) {
			return skyNetNodeOnKademlia(kademliaNode, transLayer);
		}
		System.exit(1);
		return null;
	}

	/**
	 * This private method creates the SkyNet-component for a host and defines
	 * the type of the simulation, depending on the utilized overlay. If this
	 * method is called, SkyNet is simulated on top of Napster and
	 * <code>SkyNetSimulationType</code> is set to
	 * <code>NAPSTER_SIMULATION</code>.
	 * 
	 * @param napsterNode
	 *            the overlay-component, on which SkyNet is set up
	 * @param transLayer
	 *            the TransportLayer-component of the host
	 * @return the completely initialized SkyNet-component for the host.
	 */
	private Component skyNetNodeOnNapster(NapsterClientNode napsterNode,
			TransLayer transLayer) {
		// Determine the type of the simulation
		SkyNetSimulationType.createInstance(SimulationType.NAPSTER_SIMULATION);

		// creating all needed addresses for the own nodeInfo
		NetID ip = napsterNode.getOwnOverlayContact().getTransInfo().getNetId();
		SkyNetID id = NapsterAddressResolutionImpl.getInstance(
				(int) SkyNetConstants.OVERLAY_ID_SIZE).getSkyNetID(
				napsterNode.getOwnOverlayContact().getOverlayID());
		SkyNetNodeInfoImpl nodeInfo = new SkyNetNodeInfoImpl(id, null,
				DefaultTransInfo.getTransInfo(ip, commonPort), -1);

		// create and return the SkyNet-node
		return new SkyNetNode(nodeInfo, commonPort, transLayer, napsterNode,
				simulationSize - 1, new NapsterTreeHandlerDelegator(),
				new NapsterMetricsCollectorDelegator());
	}

	/**
	 * This private method creates the SkyNet-component for a host and defines
	 * the type of the simulation, depending on the utilized overlay. If this
	 * method is called, SkyNet is simulated on top of Chord and
	 * <code>SkyNetSimulationType</code> is set to <code>CHORD_SIMULATION</code>
	 * .
	 * 
	 * @param napsterNode
	 *            the overlay-component, on which SkyNet is set up
	 * @param transLayer
	 *            the TransportLayer-component of the host
	 * @return the completely initialized SkyNet-component for the host.
	 */
	private Component skyNetNodeOnChord(ChordNode chordNode,
			TransLayer transLayer) {
		// Determine the type of the simulation
		SkyNetSimulationType.createInstance(SimulationType.CHORD_SIMULATION);

		// creating all needed addresses for the own nodeInfo
		NetID ip = chordNode.getLocalChordContact().getTransInfo().getNetId();
		SkyNetID id = Chord2AddressResolutionImpl.getInstance(
				(int) SkyNetConstants.OVERLAY_ID_SIZE).getSkyNetID(
				chordNode.getOverlayID());
		SkyNetNodeInfoImpl nodeInfo = new SkyNetNodeInfoImpl(id, null,
				DefaultTransInfo.getTransInfo(ip, commonPort), -1);

		// create and return the SkyNet-node
		return new SkyNetNode(nodeInfo, commonPort, transLayer, chordNode,
				simulationSize, new Chord2TreeHandlerDelegator(),
				new Chord2MetricsCollectorDelegator());
	}

	private Component skyNetNodeOnKademlia(
			KBRKademliaNodeGlobalKnowledge<KademliaOverlayID> kademliaNode,
			TransLayer transLayer) {
		// Determine the type of the simulation
		SkyNetSimulationType.createInstance(SimulationType.KADEMLIA_SIMULATION);

		// creating all needed addresses for the own nodeInfo
		NetID ip = kademliaNode.getLocalContact().getTransInfo().getNetId();
		SkyNetID id = KademliaAddressResolutionImpl.getInstance(
				(int) SkyNetConstants.OVERLAY_ID_SIZE).getSkyNetID(
				kademliaNode.getLocalContact().getOverlayID());
		SkyNetNodeInfoImpl nodeInfo = new SkyNetNodeInfoImpl(id, null,
				DefaultTransInfo.getTransInfo(ip, commonPort), -1);

		// create and return the SkyNet-node
		return new SkyNetNode(nodeInfo, commonPort, transLayer, kademliaNode,
				simulationSize, new KademliaTreeHandlerDelegator(),
				new KademliaMetricsCollectorDelegator());
	}

	public void setPort(long port) {
		this.commonPort = (short) port;
	}

	public void setSimulationSize(long size) {
		this.simulationSize = (int) size;
	}

}
