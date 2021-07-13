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



package de.tud.kom.p2psim.api.network;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.ConnectivityListener;
import de.tud.kom.p2psim.api.common.Message;

/**
 * The NetLayer provides a general interface to encapsulate various networking
 * models. This way, it is possible to use network solutions with different
 * abstraction levels and complexity. For instance, one can choose either
 * between a lightweight simple implementation which offers an adequate model
 * with minimum demands in resource requirements and a more complex
 * implementation capable to model fairshare bandwidth allocation of TCP but
 * having a lot of CPU consumption.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 11/29/2007
 * 
 */

public interface NetLayer extends Component {

	public void cancelTransmission(int commId);

	/**
	 * 
	 * Deliver a message with the given data to the destination using the given
	 * network protocol such as IPv4.
	 * 
	 * @param msg
	 *            the msg to be sent
	 * @param receiver
	 *            the remote receiver
	 * @param protocol
	 *            the used network protocol
	 */
	public void send(Message msg, NetID receiver, NetProtocol protocol);

	/**
	 * Returns whether the network layer has connectivity to the physical
	 * network.
	 * 
	 * @return return true if the network layer has connectivity to the physical
	 *         network
	 */
	public boolean isOnline();

	/**
	 * Returns whether the network layer has connectivity to the physical
	 * network.
	 * 
	 * @return return true if the network layer does not have connectivity to
	 *         the physical network
	 */
	public boolean isOffline();

	/**
	 * Returns the NetID of a NetLayer instance
	 * 
	 * @return the NetID of a given NetLayer instance
	 */
	public NetID getNetID();

	/**
	 * Establishes the connection to the physical network. Further to this, if
	 * installed, the correspondent ConnectivityListener will be informed about
	 * the changes in connectivity.
	 * 
	 */
	public void goOnline();

	/**
	 * Releases the connection to the physical network. Further to this, if
	 * installed, the correspondent ConnectivityListener will be informed about
	 * the changes in connectivity.
	 * 
	 */
	public void goOffline();

	/**
	 * Returns the NetPosition of a host which might be represented as the
	 * position in a two-dimensional Euclidean space, as n-dimensional
	 * coordinates used in Global Network Positioning or the location on Earth
	 * which is described by two numbers--its latitude and its longitude.
	 * 
	 * @return NetPosition the appropriate position
	 */
	public NetPosition getNetPosition();

	/**
	 * Returns the maximum physical upload bandwidth that is available at the
	 * given network wrapper. Thus, this value represents a state in which all
	 * upload connections are closed.
	 * 
	 * @return the maximum physical upload bandwidth
	 */
	@Deprecated
	public double getMaxUploadBandwidth();

	/**
	 * Returns the current available upload bandwidth of the network layer as
	 * the maximum upload bandwidth might be shared between concurrently
	 * established connections.
	 * 
	 * @return the available upload bandwidth
	 */
	@Deprecated
	public double getCurrentUploadBandwidth();

	/**
	 * Returns the maximum physical upload bandwidth that is available at the
	 * given network wrapper. Thus, this value represents a state in which all
	 * download connections are closed.
	 * 
	 * @return the maximum physical download bandwidth
	 */
	@Deprecated
	public double getMaxDownloadBandwidth();

	/**
	 * Returns the current available download bandwidth of the network layer as
	 * the maximum download bandwidth might be shared between concurrently
	 * established connections.
	 * 
	 * @return the available download bandwidth
	 */
	@Deprecated
	public double getCurrentDownloadBandwidth();

	public Bandwidth getMaxBandwidth();
	
	public Bandwidth getCurrentBandwidth();
	
	/**
	 * Adds the given NetMessageListener as a handler for incoming NetMsgEvents
	 * triggered by the NetLayer which implements the message passing from the
	 * NetLayer to a layer above.
	 * 
	 * @param listener
	 *            the listener for network events
	 */
	public void addNetMsgListener(NetMessageListener listener);

	/**
	 * Removes the given NetMessageListener which handles incoming NetMsgEvents
	 * 
	 * @param listener
	 *            the listener for network events
	 */
	public void removeNetMsgListener(NetMessageListener listener);

	/**
	 * Adds a connectivity listener to a given NetLayer instance. This should be
	 * automatically managed by the HostBuilder and the listener should be a
	 * subclass of the HostProperties interface.
	 * 
	 * @param listener
	 *            the connectivity listener
	 */
	public void addConnectivityListener(ConnectivityListener listener);

	/**
	 * Removes the installed connectivity listener.
	 * 
	 * @param listener
	 *            the connectivity listener to be removed
	 */
	public void removeConnectivityListener(ConnectivityListener listener);

}
