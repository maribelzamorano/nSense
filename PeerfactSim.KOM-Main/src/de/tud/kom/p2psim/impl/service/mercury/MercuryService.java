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

package de.tud.kom.p2psim.impl.service.mercury;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.Component;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.INeighborDeterminator;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.api.common.SupportOperations;
import de.tud.kom.p2psim.api.overlay.DHTNode;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.common.DefaultHost;
import de.tud.kom.p2psim.impl.service.mercury.attribute.IMercuryAttribute;
import de.tud.kom.p2psim.impl.service.mercury.attribute.MercuryAttributeInteger;
import de.tud.kom.p2psim.impl.service.mercury.dht.MercuryBootstrap;
import de.tud.kom.p2psim.impl.service.mercury.dht.MercuryBootstrapInfo;
import de.tud.kom.p2psim.impl.service.mercury.dht.MercuryIDMapping;
import de.tud.kom.p2psim.impl.service.mercury.filter.IMercuryFilter;
import de.tud.kom.p2psim.impl.service.mercury.filter.IMercuryFilter.OPERATOR_TYPE;
import de.tud.kom.p2psim.impl.service.mercury.filter.MercuryFilterInteger;
import de.tud.kom.p2psim.impl.service.mercury.messages.MercuryMessage;
import de.tud.kom.p2psim.impl.service.mercury.messages.MercuryNotification;
import de.tud.kom.p2psim.impl.service.mercury.messages.MercuryPayload;
import de.tud.kom.p2psim.impl.service.mercury.messages.MercuryPublication;
import de.tud.kom.p2psim.impl.service.mercury.messages.MercuryPublicationInterHub;
import de.tud.kom.p2psim.impl.service.mercury.messages.MercurySendRange;
import de.tud.kom.p2psim.impl.service.mercury.messages.MercurySubscription;
import de.tud.kom.p2psim.impl.service.mercury.messages.MercurySubscriptionDirect;
import de.tud.kom.p2psim.impl.service.mercury.operations.MaintenanceOperation;
import de.tud.kom.p2psim.impl.service.mercury.operations.NotificationOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * Mercury Publish-Subscribe System
 * 
 * Mercury uses a DHT-Overlay for Lookup-Requests. A member publishes his own
 * attribute values and is notified if a publication from another Service
 * instance matches one of his subscriptions. In an IDO-Usecase a publication
 * consists of the players position (x,y), whereas a subscription specifies a
 * range for each attribute.
 * 
 * To speed up Publication delivery and Subscription forwarding, MercuryService
 * implements a Caching Table for known contacts and their Attribute range. The
 * DHT-Overlay is only used to periodically refresh this information or to find
 * a new Contact.
 * 
 * 
 * @author Bjoern Richerzhagen <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MercuryService implements Component, SupportOperations {

	private static Logger log = SimLogger.getLogger(MercuryService.class);

	private Host host = null;

	private short port = 0;

	// private MercuryBootstrapInfo bsInfo = null;

	private MercuryBootstrap bootstrap = null;

	/**
	 * List of available Attributes, defined by Application or config.xml
	 */
	private List<MercuryAttributePrimitive> attributes = null;

	/**
	 * Attribute this service instance is responsible for
	 */
	private MercuryAttributePrimitive ownAttribute = null;

	/**
	 * Listeners (i.e. applications that want to be notified by mercury)
	 */
	private List<MercuryListener> listeners = new Vector<MercuryListener>();

	/**
	 * DHT-Node, responsible for LookUp-Requests started by Mercury
	 */
	private DHTNode dhtnode = null;

	/**
	 * Stored Subscriptions on this Node
	 */
	private List<MercurySubscription> storedSubscriptions = new Vector<MercurySubscription>();

	/**
	 * ID-Mapping provides Methods to translate between an attribute value and
	 * the corresponding Overlay-ID/Key
	 */
	private MercuryIDMapping idMapping = null;

	/**
	 * Mercury maintains a contactList for every Attribute (HUB)
	 */
	private Map<String, MercuryContactList> contacts = new HashMap<String, MercuryContactList>();

	protected boolean hasJoined = false;

	private MaintenanceOperation maintenanceOp = null;

	private long timeBetweenMaintenance = 0;

	private long timeToCollectNotifications = 0;

	private MercuryMessageHandler messageHandler = null;

	/**
	 * Mercury collects Publications to one subscriber for a specified time, in
	 * order to notify the subscriber with only one message
	 */
	protected HashMap<MercuryContact, NotificationOperation> notificationOperations = new HashMap<MercuryContact, NotificationOperation>();

	/**
	 * to prevent the DHT-Overlay from collapsing, the amount of
	 * Lookup-Operations is restricted
	 */
	protected int openLookupOperations = 0;

	/**
	 * Create Service and Overlay-Node
	 */
	public MercuryService(Host host, short port, MercuryBootstrap bootstrap,
			long timeBetweenMaintenance, long timeToCollectNotifications) {

		this.bootstrap = bootstrap;
		this.host = host;
		this.port = port;
		this.timeBetweenMaintenance = timeBetweenMaintenance;
		this.timeToCollectNotifications = timeToCollectNotifications;
		this.messageHandler = new MercuryMessageHandler(this);
		this.host.getTransLayer().addTransMsgListener(this.messageHandler,
				this.port);

		// Create new Chord Node
		// MercuryBootstrapInfo bsInfo = bootstrap.getBootstrapInfo();
		// ChordBootstrapManager bsmanager = (ChordBootstrapManager) bsInfo
		// .getBootstrap();
		// this.node = new ChordNode(dHost.getTransLayer(), port, bsmanager);
		// this.dhtnode = (DHTNode) this.node;

		MercuryBootstrapInfo bsInfo = bootstrap.getBootstrapInfo();
		dhtnode = bootstrap.createOverlayNode(bsInfo, host, port);

		// this.idMapping = new MercuryIDMappingChord();
		idMapping = bootstrap.getIDMapping();

		DefaultHost dHost = (DefaultHost) host;
		dHost.setOverlayNode(dhtnode);

		// Own Attribute (needed for distribution of publications and
		// subscriptions)
		ownAttribute = bsInfo.getOwnAttribute();

	}

	@Override
	public void setHost(Host host) {
		this.host = host;
	}

	@Override
	public Host getHost() {
		return host;
	}

	/**
	 * get Own Port number
	 * 
	 * @return
	 */
	public short getPort() {
		return port;
	}

	/**
	 * notify subscribers of a particular Publication
	 * 
	 * @param publication
	 */
	private void notifySubscribers(MercuryPublication publication) {
		List<MercuryContact> alreadyNotified = new Vector<MercuryContact>();

		// System.out.println(getOwnContact().toString() + storedSubscriptions);
		// System.out.println(getOwnContact().toString()
		// + "received Publication: " + publication);

		List<MercurySubscription> subs = getSubscriptions();

		// Caching
		for (int i = subs.size() - 1; i >= 0; i--) {
			MercurySubscription sub = subs.get(i);
			if (sub.isValid() && sub.match(publication)
					&& !alreadyNotified.contains(sub.getOrigin())) {
				MercuryNotification notification = new MercuryNotification(
						publication.getAttributes(), publication.getOrigin(),
						getOwnContact());
				notification.setMercuryPayload(publication.getMercuryPayload());
				// System.out.println(Simulator.getFormattedTime(Simulator
				// .getCurrentTime()) + notification);

				// no Caching
				// getHost().getTransLayer().send(notification,
				// sub.getOrigin().getTransInfo(), getPort(),
				// TransProtocol.UDP);

				// Caching
				addNotificationToQueue(sub.getOrigin(), notification);
				alreadyNotified.add(sub.getOrigin());
			}
		}
	}

	/**
	 * Bulk-Sending of Notifications to a specific Host
	 */
	protected void notificationOperation(MercuryContact target) {
		NotificationOperation op = new NotificationOperation(this, target,
				new OperationCallback<MercuryContact>() {

					@Override
					public void calledOperationFailed(
							Operation<MercuryContact> op) {
						// delete this operation
						notificationOperations.remove(op.getResult());
					}

					@Override
					public void calledOperationSucceeded(
							Operation<MercuryContact> op) {
						notificationOperation(op.getResult());
					}
				});
		notificationOperations.put(target, op);
		op.scheduleWithDelay(timeToCollectNotifications);
	}

	/**
	 * Add notification to operation
	 * 
	 * @param target
	 * @param notify
	 */
	protected void addNotificationToQueue(MercuryContact target,
			MercuryNotification notify) {

		// no Caching
		// getHost().getTransLayer().send(notify, target.getTransInfo(),
		// getPort(), TransProtocol.UDP);

		NotificationOperation op = notificationOperations.get(target);
		if (op != null) {
			op.addNotification(notify);
		} else {
			notificationOperation(target);
			notificationOperations.get(target).addNotification(notify);
		}
	}

	/**
	 * Returns the Value corresponding to the second value of an interval for
	 * given attribute in a given subscription
	 * 
	 * @param subscription
	 * @return
	 */
	private Comparable getHighestValueOfSubscription(
			MercurySubscription subscription,
			MercuryAttributePrimitive attribute) {
		Comparable highestValue = null;
		for (IMercuryFilter actFilter : subscription.getFilters()) {
			if (actFilter.getName().equals(attribute.getName())) {
				if (highestValue == null
						|| highestValue.compareTo(actFilter.getValue()) < 0) {
					highestValue = actFilter.getValue();
				}
			}
		}
		return highestValue;
	}

	/**
	 * Returns the Value corresponding to the first value of an interval for
	 * given attribute in a given subscription
	 * 
	 * @param subscription
	 * @return
	 */
	private Comparable getLowestValueOfSubscription(
			MercurySubscription subscription,
			MercuryAttributePrimitive attribute) {
		Comparable lowestValue = null;
		for (IMercuryFilter actFilter : subscription.getFilters()) {
			if (actFilter.getName().equals(attribute.getName())) {
				if (lowestValue == null
						|| lowestValue.compareTo(actFilter.getValue()) > 0) {
					lowestValue = actFilter.getValue();
				}
			}
		}
		return lowestValue;
	}

	/**
	 * store subscription and forward, if needed
	 * 
	 * @param subscription
	 * @param forward
	 *            if false, subscription will not be forwarded
	 */
	public void receivedSubscription(MercurySubscription subscription,
			boolean forward) {
		if (isOnline()) {
			// save contact
			storeContact(subscription.getOrigin());

			/*
			 * criteria to stop this subscription
			 */

			List<MercurySubscription> subs = getSubscriptions();

			// not valid anymore (due to timeout)
			if (!subscription.isValid() || subs.contains(subscription)) {
				// subscription is not Valid any more -> drop
				return;
			}

			Comparable highestValue = getHighestValueOfSubscription(
					subscription, getOwnAttribute());
			Comparable lowestValue = getLowestValueOfSubscription(subscription,
					getOwnAttribute());
			if (getOwnRange()[0].compareTo(lowestValue) < 0
					&& !inOwnRange(lowestValue, getOwnAttribute().getName())) {
				// possible wrap-around occured, if contactInfo used to contact
				// this
				// node was outdated
				// System.err.println(Simulator.getFormattedTime(Simulator
				// .getCurrentTime())
				// + "WrapAround detected for subscription ["
				// + subscription.getSeqNr()
				// // + subscription.toString()
				// + "]"
				// + " (highest Value: "
				// + highestValue.toString()
				// + ") at contact: " + getOwnContact().toString());
				return;
			}

			/*
			 * store this subscription
			 */
			subs.add(subscription);

			/*
			 * route Subscription to Neighbor, if appropriate. Alternative
			 * without Chord-Routing (which would use two 20 byte ID-fields in
			 * EVERY message)
			 */
			if (forward
					&& !inOwnRange(highestValue, getOwnAttribute().getName())) {

				/*
				 * If this Node has a very small range (little to distribute) it
				 * may use its bandwidth to distribute subscriptions directly.
				 * Metric for this decision: number of stored notificationOps
				 */
				if (notificationOperations.size() < 5) {
					List<MercuryContact> recipients = getContactList(
							getOwnAttribute().getName()).getAllRecipients(
							lowestValue, highestValue);
					if (recipients.size() > 3) {
						for (MercuryContact recipient : recipients) {
							getHost().getTransLayer()
									.send(new MercurySubscriptionDirect(
											subscription),
											recipient.getTransInfo(),
											getPort(), TransProtocol.UDP);
						}
						// done!
						return;

						// System.err.println(Simulator.getFormattedTime(Simulator
						// .getCurrentTime())
						// + " sent Subscriptions directly to "
						// + recipients.size() + " contacts!");
					}
				}

				/*
				 * forwarding of a subscription to next hop
				 */
				MercuryContact nextHop = getContactList(
						getOwnAttribute().getName()).getNeighbor(
						getOwnContact());
				if (nextHop != null
						&& !nextHop.getTransInfo().equals(getOwnTransInfo())) {

					getHost().getTransLayer().send(subscription,
							nextHop.getTransInfo(), getPort(),
							TransProtocol.UDP);
					// System.out.println(Simulator.getFormattedTime(Simulator
					// .getCurrentTime())
					// + "Forwarded subscription ["
					// + subscription.getSeqNr()
					// // + subscription.toString()
					// + "]"
					// + " (highest Value: "
					// + highestValue.toString()
					// + ") from: "
					// + getOwnContact().toString()
					// + " to: "
					// + nextHop.toString());
					// System.out.println(getContactList(getOwnAttribute().getName())
					// .toString());
				} else {
					// System.err.println("Found no contact to forward subscription to");
				}
			} else {
				// nothing to do here
				// System.err.println(Simulator.getFormattedTime(Simulator
				// .getCurrentTime())
				// + "Subscription "
				// + subscription.toString() + "reached Destination.");
			}
		}
	}

	/**
	 * Received Subscription via direct connection (not Overlay)
	 * 
	 * @param subscription
	 */
	public void receivedSubscriptionFromOtherHub(
			final MercurySubscription subscription) {
		if (isOnline()) {

			// save contact
			storeContact(subscription.getOrigin());

			Comparable value = getLowestValueOfSubscription(subscription,
					ownAttribute);
			if (value != null) {
				// store on own hub
				if (!inOwnRange(value, ownAttribute.getName())) {
					MercuryContact contact = getContactList(
							getOwnAttribute().getName()).getContact(value);
					if (contact != null) {
						getHost().getTransLayer().send(subscription,
								contact.getTransInfo(), getPort(),
								TransProtocol.UDP);
					} else {
						OverlayKey key = idMapping
								.map(getOwnAttribute(), value);
						doLookupAndSend(key, subscription);
					}
				} else {
					receivedSubscription(subscription, true);
				}
			} else {
				// nothing to do here. Maybe throw Warning
				System.err.println("LowestKey for subscription "
						+ subscription.toString() + " is null!");
			}
		}
	}

	private List<MercuryContact> rangeInformedSenders = new Vector<MercuryContact>();

	/**
	 * Received Publication on own Hub
	 * 
	 * @param publication
	 */
	public void receivedPublication(MercuryPublication publication) {
		if (isOnline()) {
			// save MercuryContacts of sender
			storeContact(publication.getOrigin());

			for (IMercuryAttribute attr : publication.getAttributes()) {
				if (attr.getName().equals(getOwnAttribute().getName())) {
					notifySubscribers(publication);
					if (!inOwnRange(attr.getValue(), attr.getName())) {
						if (rangeInformedSenders.contains(publication
								.getOrigin())) {
							// System.err
							// .println("Wrong Destination, did NOT inform Sender! "
							// + getOwnAttribute().getName()
							// + getOwnRange()[0]
							// + "-"
							// + getOwnRange()[1]
							// + " got: "
							// + publication.toString());
						} else {
							rangeInformedSenders.add(publication.getOrigin());
							// System.err
							// .println("Wrong Destination, informed Sender! "
							// + getOwnAttribute().getName()
							// + getOwnRange()[0] + "-"
							// + getOwnRange()[1] + " got: "
							// + publication.toString());
							MercurySendRange rangeMsg = new MercurySendRange(
									getOwnContact(), false);
							getHost().getTransLayer().send(rangeMsg,
									publication.getOrigin().getTransInfo(),
									getPort(), TransProtocol.UDP);
						}
					}
					return;
				}
			}
			System.err.println("Publication not handeled!");
		}
	}

	/**
	 * Received a Publication from an other hub
	 * 
	 * @param publication
	 */
	public void receivedPublicationFromOtherHub(
			final MercuryPublication publication) {
		if (isOnline()) {

			// save contact
			storeContact(publication.getOrigin());

			for (IMercuryAttribute attr : publication.getAttributes()) {
				if (attr.getName().equals(getOwnAttribute().getName())) {
					if (inOwnRange(attr.getValue(), attr.getName())) {
						// Publication reached destination
						// FIXME notifySubscribers(publication);
						receivedPublication(publication);
					} else {
						MercuryContact contact = getContactList(
								getOwnAttribute().getName()).getContact(
								attr.getValue());
						if (contact != null) {
							// System.out.println(getOwnAttribute().getName());
							// System.out.println(" ContactFound: "
							// + contact.getAttribute()
							// + contact.getRange()[0] + "-"
							// + contact.getRange()[1] + " for Publication: "
							// + publication.toString() + " and value: "
							// + attr.getValue());

							getHost().getTransLayer().send(publication,
									contact.getTransInfo(), getPort(),
									TransProtocol.UDP);
						} else {
							doLookupAndSend(idMapping.map(ownAttribute,
									attr.getValue()), publication);
						}
					}
					break;
				}
			}
		}
	}

	/**
	 * received a notification, inform listeners
	 * 
	 * @param notification
	 */
	public void receivedNotification(MercuryNotification notification) {
		if (isOnline()) {
			// save MercuryContacts
			storeContact(notification.getOrigin());
			storeContact(notification.getRendevouzPoint());

			for (MercuryListener listener : getListeners()) {
				listener.notificationReceived(notification.getMercuryPayload(),
						notification.getAttributes());
			}
		}
	}

	/**
	 * received a sendRange-Message
	 * 
	 * @param sendrange
	 */
	public void receivedSendRange(MercurySendRange srange) {
		if (isOnline()) {
			storeContact(srange.getOrigin());
			// Send Reply if needed
			if (srange.needsReply()) {
				MercurySendRange srangeReply = new MercurySendRange(
						getOwnContact(), false);
				getHost().getTransLayer().send(srangeReply,
						srange.getOrigin().getTransInfo(), getPort(),
						TransProtocol.UDP);
			}
		}
	}

	/**
	 * true, if AttributeValue lies in own Range
	 * 
	 * @return
	 */
	private boolean inOwnRange(Comparable value, String attributeName) {
		Comparable minR = getOwnRange()[0];
		Comparable maxR = getOwnRange()[1];
		if (minR.compareTo(maxR) > 0) {
			// wrap-around
			return (value.compareTo(minR) >= 0 || value.compareTo(maxR) <= 0);
		} else {
			return (value.compareTo(minR) >= 0 && value.compareTo(maxR) <= 0);
		}
		// return node.isRootOf(idMapping.map(getAttributeByName(attributeName),
		// value));
	}

	/**
	 * get the contact list for one Attribute
	 * 
	 * @param attribute
	 * @return
	 */
	public MercuryContactList getContactList(String attribute) {
		return contacts.get(attribute);
	}

	/**
	 * called periodically
	 */
	protected void maintenanceOperation() {
		MaintenanceOperation op = new MaintenanceOperation(this,
				new OperationCallback<Object>() {

					@Override
					public void calledOperationFailed(Operation<Object> op) {
						// do nothing because it is stopped
					}

					@Override
					public void calledOperationSucceeded(Operation<Object> op) {
						maintenanceOperation();
					}
				});
		op.scheduleWithDelay(timeBetweenMaintenance);
		maintenanceOp = op;
		// delete Information about already sent ranges
		rangeInformedSenders.clear();
	}

	/*
	 * =================================================================
	 * 
	 * Methods below this line should be considered part of a generic
	 * PUB/SUB-Service and are safe to use. It is however possible, that the
	 * used interfaces are renamed to more generic names.
	 */

	/**
	 * Get all Subscriptions on this Node
	 */
	public List<MercurySubscription> getSubscriptions() {
		return storedSubscriptions;
	}

	/**
	 * set all available Attributes (usually done by application or config.xml)
	 * 
	 * @param attributes
	 */
	public void setAvailableAttributes(
			List<MercuryAttributePrimitive> attributes) {
		this.attributes = attributes;
		for (MercuryAttributePrimitive attr : attributes) {
			this.contacts.put(attr.getName(), new MercuryContactList(this));
		}
	}

	/**
	 * Store contact-Info
	 * 
	 * @param contact
	 */
	private void storeContact(MercuryContact contact) {
		getContactList(contact.getAttribute()).addContact(contact);
	}

	/**
	 * Get a MercuryAttributePrimitive for a given name
	 * 
	 * @param name
	 * @return MercuryAttributePrimitive or null, if none is found
	 */
	public MercuryAttributePrimitive getAttributeByName(String name) {
		for (MercuryAttributePrimitive actAttribute : this.attributes) {
			if (actAttribute.getName().equals(name))
				return actAttribute;
		}
		return null;
	}

	/**
	 * Allows an app to create an attribute for a publication, ensures that the
	 * value is within the allowed range
	 * 
	 * @param attribute
	 * @param value
	 * @return
	 */
	public IMercuryAttribute createAttribute(
			MercuryAttributePrimitive attribute, Object value) {
		switch (attribute.getType()) {
		case Integer:
			Integer val = (Integer) value;
			if (val.compareTo((Integer) attribute.getMin()) < 0)
				val = (Integer) attribute.getMin();
			if (val.compareTo((Integer) attribute.getMax()) > 0)
				val = (Integer) attribute.getMax();
			return new MercuryAttributeInteger(attribute.getName(), val);

			// TODO implement other Types
		default:
			return null;
		}
	}

	/**
	 * Creates a MercuryFilter of the specified Type, ensures that values are
	 * within the allowed ranges for this attribute
	 * 
	 * @param attribute
	 * @param value
	 * @param operator
	 * @return
	 */
	public IMercuryFilter createFilter(MercuryAttributePrimitive attribute,
			Object value, OPERATOR_TYPE operator) {
		switch (attribute.getType()) {
		case Integer:
			Integer vali = (Integer) value;
			if (vali.compareTo((Integer) attribute.getMin()) < 0)
				vali = (Integer) attribute.getMin();
			if (vali.compareTo((Integer) attribute.getMax()) > 0)
				vali = (Integer) attribute.getMax();
			return new MercuryFilterInteger(attribute.getName(), vali, operator);

			// TODO implement other Types
		default:
			return null;
		}
	}

	/**
	 * Start Service
	 * 
	 * @param callback
	 */
	public void start(final OperationCallback<Object> callback) {

		// callback for the Bootstrapper to change the overlay-ID of a node
		// before joining
		bootstrap.callbackOverlayID(this);

		dhtnode.join(new OperationCallback<Object>() {
			@Override
			public void calledOperationSucceeded(Operation<Object> op) {
				if (!hasJoined)
					started();
				callback.calledOperationSucceeded(null);
			}

			@Override
			public void calledOperationFailed(Operation<Object> op) {
				callback.calledOperationFailed(null);
				log.warn("MercuryService: Start Operation failed!");
			}
		});
	}

	/**
	 * Method called after successful start-Callback
	 */
	protected void started() {
		List<MercuryContact> otherHubsContacts = bootstrap
				.getRandomContactForEachAttribute();
		for (MercuryContact contact : otherHubsContacts) {
			storeContact(contact);
		}

		maintenanceOperation();
		this.hasJoined = true;
	}

	/**
	 * Start Service
	 * 
	 * @param callback
	 */
	public void stop(final OperationCallback<Object> callback) {
		this.hasJoined = false;
		// End service
		dhtnode.leave(new OperationCallback<Object>() {

			@Override
			public void calledOperationSucceeded(Operation<Object> op) {
				stopped();
				callback.calledOperationSucceeded(null);
			}

			@Override
			public void calledOperationFailed(Operation<Object> op) {
				callback.calledOperationFailed(null);
				log.warn("MercuryService: Stop Operation failed!");
			}
		});
	}

	protected void stopped() {
		reset();
	}

	/**
	 * Publish information by Specifying a List of Attributes and additional
	 * payload
	 * 
	 * @param attributes
	 * @param payload
	 */
	public void publish(List<IMercuryAttribute> attributes,
			MercuryPayload payload) {
		if (this.hasJoined) {
			MercuryPublication pub = new MercuryPublication(getOwnContact());
			pub.addAttributes(attributes);
			pub.setMercuryPayload(payload);
			// System.err.println(Simulator.getFormattedTime(Simulator
			// .getCurrentTime())
			// + getOwnTransInfo().getNetId().toString()
			// + " new Publication "
			// + attributes.toString());
			for (IMercuryAttribute actAttribute : attributes) {
				// Routing on own hub
				if (actAttribute.getName().equals(ownAttribute.getName())) {
					// if (node.isRootOf(key)) {
					if (inOwnRange(actAttribute.getValue(),
							ownAttribute.getName())) {
						// responsible for own publication, no message needed
						receivedPublication(pub);
					} else {
						MercuryContact localContact = getContactList(
								actAttribute.getName()).getContact(
								actAttribute.getValue());

						if (localContact != null) {
							getHost().getTransLayer().send(pub,
									localContact.getTransInfo(), getPort(),
									TransProtocol.UDP);
						} else {
							OverlayKey key = this.idMapping.map(ownAttribute,
									actAttribute.getValue());
							doLookupAndSend(key, pub);
						}
					}

				} else {
					// Routing to other Hub
					MercuryContact otherHubContact = getContactList(
							actAttribute.getName()).getNearestContact(
							actAttribute.getValue());
					MercuryPublicationInterHub pubInterHub = new MercuryPublicationInterHub(
							pub);
					if (otherHubContact != null) {
						getHost().getTransLayer().send(pubInterHub,
								otherHubContact.getTransInfo(), getPort(),
								TransProtocol.UDP);
					} else {
						log.warn("MercuryService "
								+ getOwnContact().toString()
								+ " is unable to find a contact for attribute "
								+ actAttribute.getName()
								+ ", a publication may not arrive at all interested nodes!"
								+ getContactList(actAttribute.getName())
										.toString());
					}
				}
			}
		}
	}

	/**
	 * After a lookup, there are two things to do: update Range and send Message
	 * 
	 * @param contact
	 * @param msg
	 */
	protected void sendAfterLookup(OverlayContact contact, MercuryMessage msg) {
		if (contact.getTransInfo().equals(getOwnTransInfo())) {
			// TODO needed?
			if (msg instanceof MercuryPublication) {
				MercuryPublication pub = (MercuryPublication) msg;
				receivedPublication(pub);
			}
			return;
		}
		// System.out.println("Lookup Succeeeded: " + msg.toString() + "");

		// Send MercuryMessage
		getHost().getTransLayer().send(msg, contact.getTransInfo(), getPort(),
				TransProtocol.UDP);

		// Get Range of Contact -> therefore send own Range and get a Reply
		MercurySendRange rangeMsg = new MercurySendRange(getOwnContact(), true);
		getHost().getTransLayer().send(rangeMsg, contact.getTransInfo(),
				getPort(), TransProtocol.UDP);
	}

	/**
	 * Subscribe to Information specified by a List of filters
	 * 
	 * @param filters
	 */
	public void subscribe(List<IMercuryFilter> filters) {
		if (this.hasJoined) {
			final MercurySubscription sub = new MercurySubscription(
					getOwnContact());
			sub.addFilters(filters);
			// set ValidUntil for subscriptions which do not need an
			// unsubscribe-Operation
			for (IMercuryFilter filter : filters) {
				if (getAttributeByName(filter.getName()).doesExpire()) {
					sub.setValidUntil(getAttributeByName(filter.getName())
							.getExpirationTime() + Simulator.getCurrentTime());
				}
			}
			Comparable value = getLowestValueOfSubscription(sub,
					getOwnAttribute());
			if (value != null) {
				// store on own hub
				// if (!node.isRootOf(key)) {
				if (!inOwnRange(value, getOwnAttribute().getName())) {
					// node.route(key, sub, null);
					MercuryContact localContact = getContactList(
							getOwnAttribute().getName()).getContact(value);

					if (localContact != null) {
						getHost().getTransLayer().send(sub,
								localContact.getTransInfo(), getPort(),
								TransProtocol.UDP);
						// System.out.println(Simulator.getFormattedTime(Simulator
						// .getCurrentTime())
						// + "Started subscription ["
						// + sub.getSeqNr()
						// // + subscription.toString()
						// + "]"
						// + " from: "
						// + getOwnContact().toString()
						// + " to: " + localContact.toString());
						// System.out.println(getContactList(getOwnAttribute()
						// .getName()));
					} else {
						OverlayKey key = idMapping
								.map(getOwnAttribute(), value);
						doLookupAndSend(key, sub);
					}
				} else {
					receivedSubscription(sub, true);
				}
			} else {
				// store on ONE other hub
				for (IMercuryFilter actFilter : filters) {
					MercuryContact contact = getContactList(actFilter.getName())
							.getNearestContact(actFilter.getValue());
					if (contact != null) {
						getHost().getTransLayer().send(sub,
								contact.getTransInfo(), getPort(),
								TransProtocol.UDP);
						break; // store Subscription only on ONE hub
					} else {
						System.err.println("I have no Contact for other Hub!");
					}
				}
			}
		}
	}

	/**
	 * Start a Lookup-Operation for a given OverlayKey. When the
	 * lookup-Operation is finished, send the Message msg
	 * 
	 * @param key
	 * @param msg
	 */
	protected void doLookupAndSend(OverlayKey key, final MercuryMessage msg) {

		if (openLookupOperations > 2) {
			// System.out.println(node.getOverlayID().toString()
			// + " still 3 open lookup-Operations. Waiting for results...");
			return;
		}
		openLookupOperations++;
		dhtnode.nodeLookup(key, new OperationCallback<List<OverlayContact>>() {

			@Override
			public void calledOperationSucceeded(
					Operation<List<OverlayContact>> op) {
				sendAfterLookup(op.getResult().get(0), msg);
				openLookupOperations--;
			}

			@Override
			public void calledOperationFailed(Operation<List<OverlayContact>> op) {
				openLookupOperations--;
			}
		}, true);
	}

	/**
	 * Remove all stored Information
	 */
	protected void reset() {
		this.storedSubscriptions.clear();
		if (this.maintenanceOp != null) {
			this.maintenanceOp.stop();
		}
		for (NotificationOperation op : notificationOperations.values()) {
			op.stop();
		}
		notificationOperations.clear();
		this.hasJoined = false;
	}

	/**
	 * Add Listener
	 * 
	 * @param listener
	 */
	public void addListener(MercuryListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Remove Listener
	 * 
	 * @param listener
	 */
	public void removeListener(MercuryListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Get all listeners
	 * 
	 * @return
	 */
	public List<MercuryListener> getListeners() {
		return this.listeners;
	}

	/**
	 * Is the service connected with the underlying Overlay/Network.
	 * 
	 * @return
	 */
	public boolean isOnline() {
		return hasJoined;
	}

	/**
	 * get the attribute this service is responsible for
	 * 
	 * @return
	 */
	public MercuryAttributePrimitive getOwnAttribute() {
		return this.ownAttribute;
	}

	@Override
	public String toString() {
		return "MercuryService " + dhtnode.getOverlayID().toString()
				+ getOwnAttribute().getName() + getOwnRange()[0] + "-"
				+ getOwnRange()[1] + " Contacts: "
				+ getContactList("x").toString() + " ### "
				+ getContactList("y").toString();
	}

	/**
	 * For Visualization
	 * 
	 * @return
	 */
	public int[] getResponsibility() {
		int[] ret = new int[2];
		Integer[] reti = (Integer[]) this.getOwnRange();
		ret[0] = reti[0].intValue();
		ret[1] = reti[1].intValue();
		return ret;
	}

	/**
	 * Get own Attribute Responsibility
	 * 
	 * @return
	 */
	public Comparable[] getOwnRange() {
		INeighborDeterminator neighbors = dhtnode.getNeighbors();
		OverlayID[] range = bootstrap.getRange(neighbors, this);

		switch (this.ownAttribute.getType()) {
		case Integer:
			Integer[] reti = new Integer[2];
			reti[0] = idMapping.getInteger(ownAttribute, range[0]);
			reti[1] = idMapping.getInteger(ownAttribute, range[1]);
			if (range[0].equals(idMapping.getNextID(range[1]))) {
				// Node is repsonsible for whole address range
				reti[0] = (Integer) ownAttribute.getMin();
				reti[1] = (Integer) ownAttribute.getMax();
			}
			return reti;

		default:
			System.err.println("TODO: implement other Attribute Types");
			return null;
		}
	}

	/**
	 * ContactInfo for this Service-Instance
	 * 
	 * @return
	 */
	public MercuryContact getOwnContact() {
		return new MercuryContact(getOwnAttribute().getName(),
				getOwnTransInfo(), getOwnRange());
	}

	/**
	 * returns local transInfo
	 * 
	 * @return
	 */
	public TransInfo getOwnTransInfo() {
		return getHost().getTransLayer().getLocalTransInfo(getPort());
	}

	/**
	 * get this services' DHT-Node
	 * 
	 * @return
	 */
	public DHTNode getDHTNode() {
		return dhtnode;
	}
}
