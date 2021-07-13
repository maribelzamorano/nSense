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


package de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.leaf;

import java.util.Collection;
import java.util.Set;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Gnutella06OverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaOverlayID;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.IQueryInfo;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.GnutellaBootstrap;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.ConnectionManager.ConnectionManagerListener;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.ConnectionManager.ConnectionState;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.operations.UpdateDocumentsOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.AbstractGnutella06Node;
import de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.Gnutella06ConnectionManager;
import de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.IGnutella06Config;

/**
 * A leaf is a peer that connects to ultrapeers like connecting to a server.
 * Leaves do not participate in the heavy Gnutella overlay traffic, normally
 * because they have only limited bandwidth.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class Leaf extends AbstractGnutella06Node implements
		ConnectionManagerListener<Gnutella06OverlayContact, Object> {

	private Gnutella06ConnectionManager<Object> mgr;

	LeafPongHandler pongHdlr;

	private QueryHandler qHndlr;

	/**
	 * Creates a new leaf
	 * @param host: the host this overlay is part of
	 * @param id: the overlay ID of this host
	 * @param config: the configuration to use
	 * @param bootstrap: the bootstrap source to use
	 * @param port: the port to listen on
	 */
	public Leaf(Host host, GnutellaOverlayID id, IGnutella06Config config,
			GnutellaBootstrap<Gnutella06OverlayContact> bootstrap, short port) {
		super(host, id, config, bootstrap, port);

		
		mgr = new Gnutella06ConnectionManager<Object>(this, this.getConfig(), this
				.getConfig().getMaxLeafToSPConnections(), config
				.randomlyKickPeerProb());
		pongHdlr = new LeafPongHandler(this);
		mgr.addListener(this);
		mgr.setPongHandler(pongHdlr);
		IOHandler io = new IOHandler(mgr, this, pongHdlr);
		host.getTransLayer().addTransMsgListener(io, this.getPort());
		pongHdlr.setConnectionManager(mgr);
		qHndlr = new QueryHandler(this, mgr);
	}

	@Override
	protected void initConnection(Gnutella06OverlayContact contact) {
		mgr.seenContact(contact);
	}

	@Override
	protected boolean isUltrapeer() {
		return false;
	}

	@Override
	public String toString() {
		return "Leaf " + (this.isOnline() ? "on " : "off")
				+ this.getOwnContact() + mgr.toString();
	}

	@Override
	protected void updateResources() {

		Set<Gnutella06OverlayContact> contacts = mgr.getConnectedContacts();

		for (Gnutella06OverlayContact up : contacts) {
			new UpdateDocumentsOperation<Gnutella06OverlayContact>(this, mgr, up,
					Operations.getEmptyCallback()).scheduleImmediately();
		}
	}

	@Override
	public void connectionEnded(Gnutella06OverlayContact c, Object metadata) {
		// Nothing to do
	}

	@Override
	public void newConnectionEstablished(Gnutella06OverlayContact c, Object metadata) {
		new UpdateDocumentsOperation<Gnutella06OverlayContact>(this, mgr, c, Operations.getEmptyCallback())
				.scheduleImmediately();
	}

	@Override
	protected void startQuery(IQueryInfo info, int hitsWanted) {
		qHndlr.startQuery(info, hitsWanted);
	}

	@Override
	protected void handleOfflineStatus() {
		mgr.flushRaw();
	}

	@Override
	protected void handleOnlineStatus() {
		// Nothing to do
	}

	@Override
	protected boolean hasConnection() {
		return mgr.getNumberOfContactsInState(ConnectionState.Connected) > 0;
	}

	@Override
	public void lostConnectivity() {
		if (this.isOnline() && !this.isBootstrapping()) {
			this.getLocalEventDispatcher().reBootstrapped(getOwnContact());
			this.new BootstrapOperation(Operations.getEmptyCallback())
					.scheduleImmediately();
		}
	}

	@Override
	public boolean hasLowConnectivity() {
		return mgr.getNumberOfContactsInState(ConnectionState.Connected) < 2;
	}

	@Override
	public Collection<Gnutella06OverlayContact> getConnectedContacts() {
		return mgr.getConnectedContacts();
	}

}
