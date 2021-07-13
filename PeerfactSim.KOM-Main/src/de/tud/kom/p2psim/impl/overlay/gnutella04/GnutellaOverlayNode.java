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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.api.transport.TransMessageCallback;
import de.tud.kom.p2psim.api.transport.TransMessageListener;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayMessage;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode;
import de.tud.kom.p2psim.impl.overlay.gnutella04.filesharing.FilesharingDocument;
import de.tud.kom.p2psim.impl.overlay.gnutella04.filesharing.FilesharingKey;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.BaseMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.ConnectMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.OkMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.PingMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.PongMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.PushMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.QueryHitMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.QueryMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.operations.ConnectOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella04.operations.PingOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella04.operations.PongOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella04.operations.PushOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella04.operations.QueryHitOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella04.operations.QueryOperation;
import de.tud.kom.p2psim.impl.overlay.gnutella04.operations.ScheduleConnectOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class GnutellaOverlayNode extends AbstractOverlayNode implements TransMessageCallback, TransMessageListener {

	private TransLayer transLayer;
	private Map<FilesharingKey, FilesharingDocument> documents = new HashMap<FilesharingKey, FilesharingDocument>();
	// TODO OverlayID zu Integer ändern
	// TODO timeout für ping initiated
	// ping initiated (Descriptor)
	private Set<BigInteger> pingInitiated = new HashSet<BigInteger>();
	// queries initiated by application layer (Descriptor, FilesharingKey)
	private Map<BigInteger, FilesharingKey> queryInitiatedWithKey = new HashMap<BigInteger, FilesharingKey>();
	// results, ready to get for application layer (Descriptor, Contacts)
	private Map<BigInteger, List<OverlayContact<GnutellaOverlayID>>> queryResults = new HashMap<BigInteger, List<OverlayContact<GnutellaOverlayID>>>();
	// TODO low priority: Timeouts
	private long delayAcceptConnection;
	private long lastAcceptedConnection = 0;
	private long lastSentConnect = 0;
	boolean active = false;
	
	public GnutellaOverlayNode(TransLayer transLayer, OverlayID peerId, int numConn, long delayAcceptConnection, long refresh, long contactTimeout, long descriptorTimeout, short port) {
		super(peerId, port);
		active = true;
		this.transLayer = transLayer;
		transLayer.addTransMsgListener(this, this.getPort());
		this.delayAcceptConnection = delayAcceptConnection;
		this.routingTable = new GnutellaOverlayRoutingTable(peerId);
		GnutellaBootstrapManager.getInstance().registerPeer(this);
		((GnutellaOverlayRoutingTable) this.getRoutingTable()).setNumConn(numConn);
		((GnutellaOverlayRoutingTable) this.getRoutingTable()).setRefresh(refresh);
		((GnutellaOverlayRoutingTable) this.getRoutingTable()).setContactTimeout(contactTimeout);
		((GnutellaOverlayRoutingTable) this.getRoutingTable()).setDescriptorTimeout(descriptorTimeout);
	}

	@Override
	public TransLayer getTransLayer() {
		return transLayer;
	}

	public void connectivityChanged(ConnectivityEvent ce) {
		//
	}

	public void messageTimeoutOccured(int commId) {
		//
	}

	public void receive(Message msg, TransInfo senderInfo, int commId) {
		// 
	}

	public void messageArrived(TransMsgEvent receivingEvent) {
		Message message = receivingEvent.getPayload();

		if(isActive()){
			// accept messages only if connection to peer exists
			if(message instanceof BaseMessage && getRoutingTable().getContact(((AbstractOverlayMessage<OverlayID>) message.getPayload()).getSender()) != null) {
				BaseMessage baseMessage = (BaseMessage) message;
				
				if (message instanceof PingMessage) {
					processPing(receivingEvent);
				} else if (message instanceof PongMessage) {
					processPong(receivingEvent);
				} else if (message instanceof QueryMessage) {
					processQuery(receivingEvent);
				} else if (message instanceof QueryHitMessage) {
					processQueryHit(receivingEvent);
				} else if (message instanceof PushMessage) {
					processPush(receivingEvent);
				}
			}
	
			if (message instanceof ConnectMessage) {
				processConnect(receivingEvent);
			} else if (message instanceof OkMessage) {
				processOk(receivingEvent);
			}
		}
	}

	private void processConnect(TransMsgEvent receivingEvent) {
		int size = ((GnutellaOverlayRoutingTable) this.getRoutingTable()).numberOfActiveContacts();
		if(size < ((GnutellaOverlayRoutingTable) this.getRoutingTable()).getNumConn() || this.lastAcceptedConnection < Simulator.getCurrentTime()) {
			// set time for next connections
			if(size >= ((GnutellaOverlayRoutingTable) this.getRoutingTable()).getNumConn()) {
				this.lastAcceptedConnection = this.lastAcceptedConnection + this.delayAcceptConnection;
				if(Simulator.getCurrentTime() - this.lastAcceptedConnection > this.delayAcceptConnection * Math.ceil(1.0 * ((GnutellaOverlayRoutingTable) this.getRoutingTable()).getNumConn() * GnutellaConfiguration.RELATIVE_CONNECT_SLOTS)) {
					this.lastAcceptedConnection = (Simulator.getCurrentTime() - ((long) (this.delayAcceptConnection * Math.ceil(1.0 * ((GnutellaOverlayRoutingTable) this.getRoutingTable()).getNumConn() * GnutellaConfiguration.RELATIVE_CONNECT_SLOTS))));
				}
			}
			
			ConnectMessage connectMessage = (ConnectMessage) receivingEvent.getPayload();
			this.getRoutingTable().addContact(connectMessage.getContact());
			OverlayContact<GnutellaOverlayID> contact = new GnutellaOverlayContact((GnutellaOverlayID) this.getOverlayID(), this.getTransLayer().getLocalTransInfo(this.getPort()));

			if(this.getOverlayID().getUniqueValue().equals(BigInteger.valueOf(788))) {
				@SuppressWarnings("unused")
				int active = ((GnutellaOverlayRoutingTable) this.getRoutingTable()).numberOfActiveContacts();
				int numConn = ((GnutellaOverlayRoutingTable) this.getRoutingTable()).getNumConn();
				long time = Simulator.getCurrentTime();
				int test = 0;
			}
			
			OkMessage okMessage = new OkMessage((GnutellaOverlayID) this.getOverlayID(), connectMessage.getContact().getOverlayID(), contact);
			this.getTransLayer().send(okMessage, connectMessage.getContact().getTransInfo(), this.getPort(), TransProtocol.UDP);
		}
	}

	private void processOk(TransMsgEvent receivingEvent) {
		if(this.getOverlayID().getUniqueValue().equals(BigInteger.valueOf(788))) {
			@SuppressWarnings("unused")
			int active = ((GnutellaOverlayRoutingTable) this.getRoutingTable()).numberOfActiveContacts();
			int numConn = ((GnutellaOverlayRoutingTable) this.getRoutingTable()).getNumConn();
			long time = Simulator.getCurrentTime();
			int test = 0;
		}
		OkMessage message = (OkMessage) receivingEvent.getPayload();
		((GnutellaOverlayRoutingTable) this.getRoutingTable()).addContact((GnutellaOverlayContact) message.getContact());
	}

	private void processPing(TransMsgEvent receivingEvent) {
		PingMessage message = (PingMessage) receivingEvent.getPayload();
		if (((GnutellaOverlayRoutingTable) this.getRoutingTable()).incomingPing(message.getSender(), message.getDescriptor())
				&& !this.pingInitiated.contains(message.getDescriptor())) {
			// route ping
			if (message.getTTL() - 1 > 0) {
				PingOperation pingOperation = new PingOperation(this, message.getTTL() - 1, message.getHops() + 1, message.getDescriptor(), message.getSender(), new OperationCallback<Object>() {
					public void calledOperationFailed(Operation<Object> op) {
						//
					}
					public void calledOperationSucceeded(Operation<Object> op) {
						// 
					}
				});
				pingOperation.scheduleImmediately();
			}
			// reply with pong
			PongOperation pongOperation = new PongOperation(this, message.getDescriptor(), new OperationCallback<Object>() {
				public void calledOperationFailed(Operation<Object> op) {
					// 
				}
				public void calledOperationSucceeded(Operation<Object> op) {
					// 
				}
			});
			pongOperation.scheduleImmediately();

		}
	}

	private void processPong(TransMsgEvent receivingEvent) {
		PongMessage message = (PongMessage) receivingEvent.getPayload();
		boolean acceptPong = ((GnutellaOverlayRoutingTable) this.getRoutingTable()).incomingPong(message.getSender(), message.getDescriptor());
		// route pong
		if(acceptPong) {
			if (message.getTTL() -1 > 0) {
				PongOperation pongOperation = new PongOperation(this, message.getTTL() - 1, message.getHops() + 1, message.getDescriptor(), message.getContact(), new OperationCallback<Object>(){
					public void calledOperationFailed(Operation<Object> op) {
						//
					}
					public void calledOperationSucceeded(Operation<Object> op) {
						// 
					}
				});
				pongOperation.scheduleImmediately();
			}
		}
		// add contact
		((GnutellaOverlayRoutingTable) this.getRoutingTable()).addInactiveContact((GnutellaOverlayContact) message.getContact());
	}

	private void processQuery(TransMsgEvent receivingEvent) {
		QueryMessage message = (QueryMessage) receivingEvent.getPayload();
		// prevent accepting the same query two times or a query sent from here
		if (((GnutellaOverlayRoutingTable) this.getRoutingTable()).incomingQuery(message.getSender(), message.getDescriptor())
				&& !this.queryInitiatedWithKey.containsKey(message.getDescriptor())) {
			// route query
			if (message.getTTL() - 1 > 0) {
				QueryOperation queryOperation = new QueryOperation(this, message.getTTL() - 1, message.getHops() + 1, message.getDescriptor(), message.getSender(), message.getKey(), new OperationCallback<Object>() {
					public void calledOperationFailed(Operation<Object> op) {
						//
					}
					public void calledOperationSucceeded(Operation<Object> op) {
						// 
					}
				});
				queryOperation.scheduleImmediately();
			}
			OverlayContact<GnutellaOverlayID> contact = new GnutellaOverlayContact((GnutellaOverlayID) this.getOverlayID(), this.getTransLayer().getLocalTransInfo(this.getPort()));
			List<FilesharingKey> keys = new LinkedList<FilesharingKey>();
			// send query hits (same Key with prop. = 1, different keys with prop. < 1)
			int messageKeyRank = message.getKey().getRank();
			for (FilesharingDocument document : documents.values()) {
				int documentKeyRank = ((FilesharingKey) document.getKey()).getRank();
				if((messageKeyRank & GnutellaConfiguration.QUERY_KEY_MASK) == (documentKeyRank & GnutellaConfiguration.QUERY_KEY_MASK)) {
					keys.add((FilesharingKey) document.getKey());
				}
			}
			if(!keys.isEmpty()) {
				QueryHitOperation queryHitOperation = new QueryHitOperation(this, message.getDescriptor(), contact, keys, new OperationCallback<Object>() {
					public void calledOperationFailed(Operation<Object> op) {
						//
					}
					public void calledOperationSucceeded(Operation<Object> op) {
						//
					}
				});
				queryHitOperation.scheduleImmediately();
			}
		}
	}

	private void processQueryHit(TransMsgEvent receivingEvent) {
		QueryHitMessage message = (QueryHitMessage) receivingEvent.getPayload();
		boolean acceptQuery = ((GnutellaOverlayRoutingTable) this.getRoutingTable()).incomingQueryHit(message.getSender(), message.getDescriptor(), message.getContact());
		// route query hit
		if(acceptQuery) {
			if (message.getTTL() -1 > 0) {
				QueryHitOperation queryHitOperation = new QueryHitOperation(this, message.getTTL() - 1, message.getHops() + 1, message.getDescriptor(), message.getContact(), message.getKeys(), new OperationCallback<Object>(){
					public void calledOperationFailed(Operation<Object> op) {
						//
					}
					public void calledOperationSucceeded(Operation<Object> op) {
						// 
					}
				});
				queryHitOperation.scheduleImmediately();
			}
		}
		// addContact
		((GnutellaOverlayRoutingTable) this.getRoutingTable()).addInactiveContact((GnutellaOverlayContact) message.getContact());
		// check if query was send from here and filter unwanted results
		if(message.getKeys().contains(queryInitiatedWithKey.get(message.getDescriptor()))) {
			// add contact to result list
			if(queryResults.get(message.getDescriptor()) == null) {
				queryResults.put(message.getDescriptor(), new LinkedList<OverlayContact<GnutellaOverlayID>>());
			}
			queryResults.get(message.getDescriptor()).add(new GnutellaOverlayContact(message.getContact()));
		}
	}

	private void processPush(TransMsgEvent receivingEvent) {
		PushMessage message = (PushMessage) receivingEvent.getPayload();
		// check push and send file
		if(this.getOverlayID().equals(message.getPushReceiver())) {
			FilesharingDocument document = documents.get(message.getKey());
			if(document != null) {
				// TODO Datei senden
			}
		}
		// route push
		else if(message.getTTL() - 1 > 0) {
			PushOperation pushOperation = new PushOperation(this, message.getTTL() - 1, message.getHops() + 1, message.getDescriptor(), message.getPushSender(), message.getPushReceiver(), message.getKey(), new OperationCallback<Object>(){
				public void calledOperationFailed(Operation<Object> op) {
					//
				}
				public void calledOperationSucceeded(Operation<Object> op) {
					//
				}
			});
			pushOperation.scheduleImmediately();
		}
	}

	public boolean addDocument(FilesharingDocument arg0) {
		return (documents.put((FilesharingKey) arg0.getKey(), arg0) != null);
	}

	public void clearDocuments() {
		documents.clear();
	}

	public boolean containsKey(Object arg0) {
		return documents.containsKey(arg0);
	}
	
	public boolean containsDocument(Object arg0) {
		return documents.containsValue(arg0);
	}

	public boolean removeDocument(FilesharingDocument arg0) {
		FilesharingKey key = (FilesharingKey) arg0.getKey();
		return (documents.remove(key) != null);
	}
	

	public FilesharingDocument removeKey(FilesharingKey arg0) {
		return documents.remove(arg0);
	}
	
	public Set<FilesharingKey> keySet() {
		return documents.keySet();
	}

	public Collection<FilesharingDocument> getDocuments() {
		return documents.values();
	}

	public void registerQuery(BigInteger descriptor, FilesharingKey key) {
		this.queryResults.put(descriptor, new LinkedList<OverlayContact<GnutellaOverlayID>>());
		this.queryInitiatedWithKey.put(descriptor, key);
	}
	
	public List<OverlayContact<GnutellaOverlayID>> getQueryResults(BigInteger descriptor) {
		List<OverlayContact<GnutellaOverlayID>> ret = queryResults.get(descriptor);
		queryResults.remove(descriptor);
		queryInitiatedWithKey.remove(descriptor);
		return ret;
	}

	public void scheduleConnect(ScheduleConnectOperation scheduleOverlayOperation) {
		// Join to bootstrap_nodes, if no contacts available
		if(((GnutellaOverlayRoutingTable) this.getRoutingTable()).numberOfActiveContacts() == 0) {
			List<TransInfo> bootstrapInfos = GnutellaBootstrapManager.getInstance().getBootstrapInfo();
			for (TransInfo bootstrapInfo : bootstrapInfos) {
				ConnectOperation connectOperation = new ConnectOperation(this, bootstrapInfo, new OperationCallback<Object>(){
					public void calledOperationFailed(Operation<Object> op) {
						// 
					}
					public void calledOperationSucceeded(Operation<Object> op) {
						// 
					}
				});
				connectOperation.scheduleImmediately();
			}
		}
		// sent ping, if not enough peers known
		else if(((GnutellaOverlayRoutingTable) this.getRoutingTable()).numberOfActiveContacts() + ((GnutellaOverlayRoutingTable) this.getRoutingTable()).inactiveContacts().size() < ((GnutellaOverlayRoutingTable) this.getRoutingTable()).getNumConn()) {
			PingOperation pingOperation = new PingOperation(this, new OperationCallback<Object>(){
				public void calledOperationFailed(Operation<Object> op) {
					// 					
				}
				public void calledOperationSucceeded(Operation<Object> op) {
					// 					
				}
			});
			pingOperation.scheduleImmediately();
			this.pingInitiated.add(pingOperation.getDescriptor());
		}
		// sent ping if refresh needed
		List<GnutellaOverlayContact> refreshContacts = ((GnutellaOverlayRoutingTable) this.getRoutingTable()).getRefreshContacts();
		for (GnutellaOverlayContact contact : refreshContacts) {
			PingOperation pingOperation = new PingOperation(this, contact, new OperationCallback<Object>(){
				public void calledOperationFailed(Operation<Object> op) {
					// 
				}
				public void calledOperationSucceeded(Operation<Object> op) {
					// 
				}
			});
			pingOperation.scheduleImmediately();
		}
		// remove dead descriptors
		List<BigInteger> deadDescriptors = ((GnutellaOverlayRoutingTable) this.getRoutingTable()).getDeadContacts();		
		pingInitiated.removeAll(deadDescriptors);
		// connect to inactive nodes until all available connections are used
		for (int i = 0; i < ((GnutellaOverlayRoutingTable) this.getRoutingTable()).getNumConn() - ((GnutellaOverlayRoutingTable) this.getRoutingTable()).numberOfActiveContacts() ; i++) {
			OverlayContact<GnutellaOverlayID> contact = ((GnutellaOverlayRoutingTable) this.getRoutingTable()).removeInactiveContact();
			if(contact != null) {
				if(this.getOverlayID().getUniqueValue().equals(BigInteger.valueOf(788))) {
					@SuppressWarnings("unused")
					int active = ((GnutellaOverlayRoutingTable) this.getRoutingTable()).numberOfActiveContacts();
					int numConn = ((GnutellaOverlayRoutingTable) this.getRoutingTable()).getNumConn();
					long time = Simulator.getCurrentTime();
					
				}
				ConnectOperation connectOperation = new ConnectOperation(this, contact.getTransInfo(), new OperationCallback<Object>(){
					public void calledOperationFailed(Operation<Object> op) {
						// 
					}
					public void calledOperationSucceeded(Operation<Object> op) {
						// 
					}
				});
				connectOperation.scheduleImmediately();
			}
		}
	}
	
	public void scheduleReconnect(ScheduleConnectOperation scheduleOverlayOperation) {
		OverlayContact<GnutellaOverlayID> contact = ((GnutellaOverlayRoutingTable) this.getRoutingTable()).removeInactiveContact();
		if(contact != null) {
			ConnectOperation connectOperation = new ConnectOperation(this, contact.getTransInfo(), new OperationCallback<Object>(){
				public void calledOperationFailed(Operation<Object> op) {
					// 
				}
				public void calledOperationSucceeded(Operation<Object> op) {
					// 
				}
			});
			connectOperation.scheduleImmediately();
		}
	}

	public String toString() {
		return this.getOverlayID().toString();
	}
	
	public void sendQuery(FilesharingKey key){
		QueryOperation query = new QueryOperation(this, key, new OperationCallback<Object>(){
			public void calledOperationFailed(Operation<Object> op) {
				// 
			}
			public void calledOperationSucceeded(Operation<Object> op) {
				// 
			}
		});
	}
	
	public void fail(){
		active = false;
		routingTable.clearContacts();
		GnutellaBootstrapManager.getInstance().unregisterNode(this);
		GnutellaBootstrapManager.getInstance().unregisterPeer(this);
	}
	
	public void leave(){
		active = false;
		routingTable.clearContacts();
		GnutellaBootstrapManager.getInstance().unregisterNode(this);
		GnutellaBootstrapManager.getInstance().unregisterPeer(this);
	}
	
	public boolean isActive(){
		return active;
	}

	@Override
	public INeighborDeterminator getNeighbors() {
		return new INeighborDeterminator() {
			
			@Override
			public List<OverlayContact> getNeighbors() {
				return Collections.unmodifiableList(routingTable.allContacts());
			}
		};
	}
	
}
















