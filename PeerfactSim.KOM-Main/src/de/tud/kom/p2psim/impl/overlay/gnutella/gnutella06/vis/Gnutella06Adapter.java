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


package de.tud.kom.p2psim.impl.overlay.gnutella.gnutella06.vis;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Gnutella06OverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.GnutellaLikeOverlayContact;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.Query;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.evaluation.GnutellaEvents;
import de.tud.kom.p2psim.impl.overlay.gnutella.api.evaluation.IGnutellaEventListener;
import de.tud.kom.p2psim.impl.overlay.gnutella.common.AbstractGnutellaLikeNode;
import de.tud.kom.p2psim.impl.overlay.gnutella.vis.Type;
import de.tud.kom.p2psim.impl.vis.analyzer.OverlayAdapter;
import de.tud.kom.p2psim.impl.vis.analyzer.Translator.EdgeHandle;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class Gnutella06Adapter extends OverlayAdapter implements
		IGnutellaEventListener {

	Map<GnutellaLikeOverlayContact, NetID> ids = new HashMap<GnutellaLikeOverlayContact, NetID>();

	Map<Connection, EdgeHandle> conns = new HashMap<Connection, EdgeHandle>();

	public Gnutella06Adapter() {
		addOverlayImpl(AbstractGnutellaLikeNode.class);
		GnutellaEvents.getGlobal().addListener(this);
		addOverlayNodeMetric(Type.class);
	}

	@Override
	public Object getBootstrapManagerFor(OverlayNode nd) {
		if (nd instanceof AbstractGnutellaLikeNode) {
			AbstractGnutellaLikeNode<?, ?> node = (AbstractGnutellaLikeNode) nd;

			return node.getBootstrap();
		} else
			return null;
	}

	@Override
	public String getOverlayName() {
		return "Gnutella 0.6";
	}

	@Override
	public void handleLeavingHost(Host host) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleNewHost(Map<String, Serializable> attributes, Host host,
			OverlayNode overlayNode) {
		if (overlayNode instanceof AbstractGnutellaLikeNode) {
			AbstractGnutellaLikeNode nd = (AbstractGnutellaLikeNode) overlayNode;

			ids.put(nd.getOwnContact(), host.getNetLayer().getNetID());

		}
	}

	@Override
	public void handleNewHostAfter(Host host, OverlayNode overlayNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleOperation(Host host, Operation<?> op, boolean finished) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleOverlayMsg(Message omsg, Host from, NetID fromID,
			Host to, NetID toID) {
		// not needed as the visualization of messages is done by the method
		// overlayMsgOccured(...) in VisAnalyzer since the flag
		// messageEdges="true" is set in the respective xml-config-file

	}

	@Override
	public void connectionFailed(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID,
			FailCause cause) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionStarted(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID) {
		conns.put(new Connection(invoker, receiver), this.addEdge(invoker
				.getTransInfo().getNetId(), receiver.getTransInfo().getNetId(),
				Color.YELLOW, "Connecting"));
		System.out.println("Connection started: " + invoker + ", " + receiver);
	}

	@Override
	public void connectionSucceeded(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver, int connectionUID) {

		boolean leafConnection = (receiver instanceof Gnutella06OverlayContact
				&& invoker instanceof Gnutella06OverlayContact && (!((Gnutella06OverlayContact) invoker)
				.isUltrapeer() || !((Gnutella06OverlayContact) invoker)
				.isUltrapeer()));

		EdgeHandle hdl = conns.get(new Connection(invoker, receiver));
		if (hdl != null)
			hdl.remove();

		if (leafConnection)
			conns.put(new Connection(invoker, receiver), this.addEdge(invoker
					.getTransInfo().getNetId(), receiver.getTransInfo()
					.getNetId(), Color.GREEN, "leaf-2-up"));
		else
			conns.put(new Connection(invoker, receiver), this.addEdge(invoker
					.getTransInfo().getNetId(), receiver.getTransInfo()
					.getNetId(), Color.CYAN, "up-2-up"));

		System.out
				.println("Connection succeeded: " + invoker + ", " + receiver);
	}

	@Override
	public void pingTimeouted(GnutellaLikeOverlayContact invoker,
			GnutellaLikeOverlayContact receiver) {
		// TODO Auto-generated method stub

	}

	@Override
	public void queryFailed(GnutellaLikeOverlayContact initiator, Query query,
			int hits) {
		// TODO Auto-generated method stub

	}

	@Override
	public void queryMadeHop(int queryUID, GnutellaLikeOverlayContact hopContact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void queryStarted(GnutellaLikeOverlayContact initiator, Query query) {
		// TODO Auto-generated method stub

	}

	@Override
	public void querySucceeded(GnutellaLikeOverlayContact initiator,
			Query query, int hits) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reBootstrapped(GnutellaLikeOverlayContact c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionBreak(GnutellaLikeOverlayContact notifiedNode,
			GnutellaLikeOverlayContact opponent, ConnBreakCause cause) {
		EdgeHandle hdl = conns.get(new Connection(notifiedNode, opponent));
		if (hdl != null)
			hdl.remove();
		else
			System.out.println("CONNECTION NOT FOUND");
		System.out.println("Connection broke: " + notifiedNode + ", "
				+ opponent);
	}

	class Connection {

		private GnutellaLikeOverlayContact b;

		private GnutellaLikeOverlayContact a;

		public Connection(GnutellaLikeOverlayContact a,
				GnutellaLikeOverlayContact b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public int hashCode() {
			return a.hashCode() + b.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Connection))
				return false;
			Connection other = (Connection) obj;
			return other.a.equals(this.a) && other.b.equals(this.b)
					|| other.a.equals(this.b) && other.b.equals(this.a);
		}

	}

}
