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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.vis;

import java.awt.Color;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.impl.overlay.dht.ForwardMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordNode;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordRoutingTable;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.AckMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.HandshakeMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.HandshakeReply;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.JoinMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.JoinReply;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.LeaveMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.LookupMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.LookupReply;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.NotifyOfflineMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.NotifyPredecessorMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.NotifySuccessorMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.RetrievePredecessorMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.RetrievePredecessorReply;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.RetrieveSuccessorMsg;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.RetrieveSuccessorReply;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.StoreMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.StoreReplyMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.ValueLookupMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.ValueLookupReplyMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.vis.DocCountM;
import de.tud.kom.p2psim.impl.vis.analyzer.OverlayAdapter;
import de.tud.kom.p2psim.impl.vis.analyzer.Translator.EdgeHandle;
import de.tud.kom.p2psim.impl.vis.analyzer.positioners.SchematicPositioner;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class ChordAdapter extends OverlayAdapter {

	protected Map<NetID, ChordNode> overlayImpls = new HashMap<NetID, ChordNode>();

	protected Map<Host, EdgeHandle> predecessors = new HashMap<Host, EdgeHandle>();

	protected Map<NetID, Set<NetID>> fingertables = new HashMap<NetID, Set<NetID>>();

	protected Map<ChordNode, Integer> docCount = new HashMap<ChordNode, Integer>();

	// protected Set<Integer> lookupRequestsOpIDs = new HashSet<Integer>();

	public ChordAdapter() {
		addOverlayImpl(ChordNode.class);

		addOverlayImpl(HandshakeMsg.class); // done
		addOverlayImpl(HandshakeReply.class); // done

		addOverlayImpl(JoinMessage.class); // done
		addOverlayImpl(JoinReply.class); // done
		addOverlayImpl(LeaveMessage.class); // done

		addOverlayImpl(LookupMessage.class); // done
		addOverlayImpl(LookupReply.class); // done
		addOverlayImpl(ValueLookupMessage.class); // done
		addOverlayImpl(ValueLookupReplyMessage.class); // done

		addOverlayImpl(NotifyOfflineMsg.class); // done
		addOverlayImpl(NotifyPredecessorMsg.class); // done
		addOverlayImpl(NotifySuccessorMsg.class); // done
		addOverlayImpl(AckMessage.class); // done

		addOverlayImpl(RetrievePredecessorMsg.class); // done
		addOverlayImpl(RetrievePredecessorReply.class); // done
		addOverlayImpl(RetrieveSuccessorMsg.class); // done
		addOverlayImpl(RetrieveSuccessorReply.class); // done

		addOverlayImpl(StoreMessage.class); //
		addOverlayImpl(StoreReplyMessage.class); //
		
		addOverlayImpl(ForwardMsg.class); //

		addOverlayNodeMetric(HostsInFT.class);
		addOverlayNodeMetric(ChordIDM.class);
		addOverlayNodeMetric(DocCountM.class);
	}

	@Override
	public Object getBootstrapManagerFor(OverlayNode nd) {
		if (nd instanceof ChordNode) {
			return ((ChordNode) nd).getBootstrapManager();
		} else {
			return null;
		}
	}

	@Override
	public String getOverlayName() {
		return "Chord";
	}

	@Override
	public void handleLeavingHost(Host host) {
		predecessors.remove(host).remove();
		NetID nID = host.getNetLayer().getNetID();

		List<Host> toDelete = new LinkedList<Host>();

		for (Map.Entry<Host, EdgeHandle> e : predecessors.entrySet()) {
			if (e.getValue() == null || e.getValue().getTo().equals(nID)
					|| e.getValue().getFrom().equals(nID)) {
				toDelete.add(e.getKey());
			}
		}
		for (Host h : toDelete) {
			predecessors.remove(h).remove();
		}
		fingertables.remove(host.getNetLayer().getNetID());

		getTranslator().overlayNodeRemoved(host.getNetLayer().getNetID());
	}

	@Override
	public void handleNewHost(Map<String, Serializable> attributes, Host host,
			OverlayNode overlayNode) {

		ChordNode chordNode = (ChordNode) overlayNode;

		attributes.put("ChordID", chordNode.getOverlayID());

		// Initialisiere Dokumenten-Count
		docCount.put(chordNode, chordNode.getDHT().getNumberOfDHTEntries());
		attributes.put("doc_count", chordNode.getDHT().getNumberOfDHTEntries());

		overlayImpls.put(host.getNetLayer().getNetID(), chordNode);

		updatePredecessorFor(host);

	}

	@Override
	public void handleNewHostAfter(Host host, OverlayNode overlayNode) {
		// currently not needed

	}

	@Override
	public void handleOperation(Host host, Operation<?> op, boolean finished) {
		// updatePredecessorFor(host);
	}

	@Override
	public void handleOverlayMsg(Message omsg, Host from, NetID fromID,
			Host to, NetID toID) {

		if (from != null)
			refreshFingertable(from);
		if (to != null)
			refreshFingertable(to);
		if (from != null)
			updatePredecessorFor(from);
		if (to != null)
			updatePredecessorFor(to);

		updateDocCount(fromID);
		updateDocCount(toID);

		// Mit dem != null wird überprüft, ob ein entsprechender Host auch
		// existiert.
		// Manchmal versenden Hosts Nachrichten, ohne dass sie zum Szenario
		// dazugekommen
		// sind. Komisch.

		// if (omsg instanceof LookupReply) {
		// LookupReply lookupReply = (LookupReply) omsg;
		// if (lookupRequestsOpIDs.remove(lookupReply)) {
		// Log.warn("Removed LookupReply with hash "+lookupReply.hashCode());
		// this.flashEdge(fromID, toID, Color.BLUE,
		// "LookupReply ForValue", omsg.getClass());
		// }
		// } else if (omsg instanceof LookupRequest) {
		// LookupRequest lookupRequest = (LookupRequest) omsg;
		// if (lookupRequest.forValue()) {
		// lookupRequestsOpIDs.add(lookupRequest.hashCode());
		// this.flashEdge(fromID, toID, Color.RED,
		// "LookupRequest ForValue", omsg.getClass());
		// }
		// } else if (omsg instanceof GetInfoRequest) {
		// GetInfoRequest getInfoRequest = (GetInfoRequest) omsg;
		// if (getInfoRequest.getCmdID() == -2) {
		// // getInfoRequestMap.put(currentOpID, getInfoRequest);
		// this.flashEdge(fromID, toID, Color.BLACK, "GetInfoRequest",
		// omsg.getClass());
		// }
		// } else if (omsg instanceof GetInfoReply) {
		// GetInfoReply getInfoReply = (GetInfoReply) omsg;
		// if (getInfoReply.getCmdID() == -2) {
		// // getInfoReplyMap.put(currentOpID, getInfoReply);
		// this.flashEdge(fromID, toID, Color.ORANGE, "GetInfoReply", omsg
		// .getClass());
		// }
		// }
		// messages for node lookup
		if (omsg instanceof LookupReply) {
			this.flashEdge(fromID, toID, Color.BLUE, "LookupReply ForNode",
					omsg.getClass());
		} else if (omsg instanceof LookupMessage) {
			this.flashEdge(fromID, toID, Color.RED, "LookupRequest ForNode",
					omsg.getClass());
		}
		// messages of join and leave
		else if (omsg instanceof JoinMessage) {
			this.flashEdge(fromID, toID, Color.GREEN, "JoinMsg",
					omsg.getClass());
		} else if (omsg instanceof JoinReply) {
			this.flashEdge(fromID, toID, Color.BLUE, "JoinReply",
					omsg.getClass());
		} else if (omsg instanceof LeaveMessage) {
			this.flashEdge(fromID, toID, Color.PINK, "LeaveMsg",
					omsg.getClass());
		}
		// messages for value lookup
		if (omsg instanceof ValueLookupReplyMessage) {
			this.flashEdge(fromID, toID, Color.GREEN, "LookupReply ForValue",
					omsg.getClass());
		} else if (omsg instanceof ValueLookupMessage) {
			this.flashEdge(fromID, toID, Color.YELLOW,
					"LookupRequest ForValue", omsg.getClass());
		}
		// messages for predecessor and successor
		else if (omsg instanceof RetrievePredecessorMsg) {
			this.flashEdge(fromID, toID, Color.BLACK,
					"RetrievePredecessorRequest", omsg.getClass());
		} else if (omsg instanceof RetrievePredecessorReply) {
			this.flashEdge(fromID, toID, Color.ORANGE,
					"RetrievePredecessorReply", omsg.getClass());
		} else if (omsg instanceof RetrieveSuccessorMsg) {
			this.flashEdge(fromID, toID, Color.BLACK,
					"RetrieveSuccessorRequest", omsg.getClass());
		} else if (omsg instanceof RetrieveSuccessorReply) {
			this.flashEdge(fromID, toID, Color.ORANGE,
					"RetrieveSuccessorReply", omsg.getClass());
		}
		// messages for a handshake
		else if (omsg instanceof HandshakeMsg) {
			this.flashEdge(fromID, toID, Color.MAGENTA, "HandshakeMsg",
					omsg.getClass());
		} else if (omsg instanceof HandshakeReply) {
			this.flashEdge(fromID, toID, Color.BLUE, "HandshakeReply",
					omsg.getClass());
		}
		// messages for notification
		else if (omsg instanceof NotifyOfflineMsg) {
			this.flashEdge(fromID, toID, Color.MAGENTA, "NotifyOfflineMsg",
					omsg.getClass());
		} else if (omsg instanceof NotifyPredecessorMsg) {
			this.flashEdge(fromID, toID, Color.BLUE, "NotifyPredecessorMsg",
					omsg.getClass());
		} else if (omsg instanceof NotifySuccessorMsg) {
			this.flashEdge(fromID, toID, Color.orange, "NotifySuccessorMsg",
					omsg.getClass());
		} else if (omsg instanceof AckMessage) {
			this.flashEdge(fromID, toID, Color.YELLOW, "AckMessage",
					omsg.getClass());
		}
		// messages for store
		else if (omsg instanceof StoreMessage) {
			this.flashEdge(fromID, toID, Color.MAGENTA, "StoreMessage",
					omsg.getClass());
		} else if (omsg instanceof StoreReplyMessage) {
			this.flashEdge(fromID, toID, Color.BLUE, "StoreReplyMessage",
					omsg.getClass());
		}
		
		else if (omsg instanceof ForwardMsg) {
			this.flashEdge(fromID, toID, Color.MAGENTA, "ForwardMessage",
					omsg.getClass());
		}
		
	}

	private void updateDocCount(NetID nodeID) {
		ChordNode node = overlayImpls.get(nodeID);
		if (node != null) {
			int newDocCount = node.getDHT().getNumberOfDHTEntries();
			int oldDocCount = docCount.get(node);

			if (newDocCount != oldDocCount) {
				getTranslator().nodeAttributeChanged(nodeID, "doc_count",
						newDocCount);
				docCount.put(node, newDocCount);
				System.out.println("DocCount gewechselt!");
			}
		}
	}

	private void updatePredecessorFor(Host host) {
		NetID pre = getOverlayPredecessor(host);
		EdgeHandle old_pre = predecessors.get(host);
		// System.out.println("PREDECESSORS: " + pre + "|" + ((old_pre !=
		// null)?old_pre.getTo():"null"));

		if (pre == null) {
			if (old_pre != null)
				old_pre.remove();
		} else if (old_pre == null || !pre.equals(old_pre.getTo())) {
			if (old_pre != null)
				old_pre.remove();
			EdgeHandle newEdge = this.addEdge(netID(host), pre, Color.GREEN,
					"succ/pre");
			predecessors.put(host, newEdge);
		}

	}

	private NetID netID(Host host) {
		return host.getNetLayer().getNetID();
	}

	private NetID getOverlayPredecessor(Host fromNode) {
		ChordRoutingTable crt = ((ChordNode) fromNode
				.getOverlay(ChordNode.class)).getChordRoutingTable();
		if (crt == null) {
			return null;
		}
		ChordContact pre = crt.getPredecessor();
		if (pre != null)
			return pre.getTransInfo().getNetId();
		else
			return null;
	}

	private void refreshFingertable(Host fromNode) {
		ChordRoutingTable crt = ((ChordNode) fromNode
				.getOverlay(ChordNode.class)).getChordRoutingTable();
		if (crt == null) {
			return;
		}
		ChordContact[] ft = crt.copyFingerTable();
		HashSet<ChordContact> ft_set = new HashSet<ChordContact>(
				Arrays.asList(ft));
		if (ft_set.contains(null))
			ft_set.remove(null);

		// System.out.println(fromNode + " Größe FT:" + ft_set.size());
		// System.out.println(fromNode + " FT:" + ft_set);

		if (ft != null) {

			Set<NetID> vis_ft = fingertables.get(netID(fromNode));
			if (vis_ft == null) {
				vis_ft = new HashSet<NetID>();
				fingertables.put(netID(fromNode), vis_ft);
			}
			boolean ft_changed = false;
			for (ChordContact con : ft_set) {
				NetID newNetID = con.getTransInfo().getNetId();
				if (con != null && !vis_ft.contains(newNetID)) {
					this.addEdge(netID(fromNode), newNetID, Color.LIGHT_GRAY,
							"finger");
					vis_ft.add(newNetID);
					ft_changed = true;

				}
			}
			if (ft_changed)
				this.getTranslator().nodeAttributeChanged(
						fromNode.getNetLayer().getNetID(), "ft_hosts",
						ft_set.size());
		}
	}

	@Override
	public SchematicPositioner getNewPositioner() {
		return new ChordRingPositioner();
	}

}
