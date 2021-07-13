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


package de.tud.kom.p2psim.api.common;

import de.tud.kom.p2psim.api.analyzer.Analyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.AggregationAnalyzer;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.scenario.Configurable;
import de.tud.kom.p2psim.api.service.aggr.IAggregationResult;
import de.tud.kom.p2psim.api.simengine.SimulationEventHandler;
import de.tud.kom.p2psim.impl.transport.AbstractTransMessage;

/**
 * Monitor defines a central instance which is called by components whenever an
 * action occurs that is important to trace. In particular, upon calling a
 * specific monitor method, the monitor delegates notifications to all installed
 * analyzers.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 3.0, 12/03/2007
 * 
 */
public interface Monitor extends Configurable, SimulationEventHandler {

	/**
	 * 
	 * Provides additional information why an action occurs at a specific
	 * component
	 * 
	 */
	public enum Reason {
		/**
		 * Sending of a message
		 */
		SEND,
		/**
		 * Receiving of a message
		 */
		RECEIVE,
		/**
		 * Dropping of a message
		 */
		DROP,
		/**
		 * Host does not have physical network connectivity
		 */
		OFFLINE,
		/**
		 * Host does have physical network connectivity
		 */
		ONLINE

	}

	/**
	 * Sets the point in time at which the simulation framework will activate
	 * the monitoring of actions
	 * 
	 * @param time
	 *            time at which the monitoring starts
	 */
	public void setStart(long time);

	/**
	 * Sets the point in time at which the simulation framework will deactivate
	 * the monitoring of actions
	 * 
	 * @param time
	 *            time at which the monitoring ends
	 */
	public void setStop(long time);

	/**
	 * Invoking this method denotes that an action related to a network message
	 * is occurred on the network layer with the given NetID. The reason why
	 * this action is occurred is specified by the given reason such as send,
	 * receive or drop.
	 * 
	 * @param msg
	 *            the related network message
	 * @param id
	 *            the NetId of the network layer of the occurring event
	 * @param reason
	 *            the reason why this event happened
	 */
	public void netMsgEvent(NetMessage msg, NetID id, Reason reason);

	/**
	 * This method is called whenever an operation has been triggered.
	 * 
	 * @param op
	 *            the Operation that has been triggered.
	 */
	public void operationInitiated(Operation<?> op);

	/**
	 * Invoking this method denotes that an operation is finished
	 * 
	 * @param op
	 *            the finished operation
	 */
	public void operationFinished(Operation<?> op);

	/**
	 * Invoking this method denotes that the given message is sent at the
	 * transport layer (from the application towards the network layer).
	 * 
	 * @param msg
	 *            the AbstractTransMessage which is sent out.
	 */
	public void transMsgSent(AbstractTransMessage msg);

	/**
	 * Invoking this method denotes that the given message is received at the
	 * transport layer (from the network layer towards the application layer).
	 * 
	 * @param msg
	 *            the received AbstractTransMessage.
	 */
	public void transMsgReceived(AbstractTransMessage msg);

	/**
	 * Registers an analyzer to the monitor which will be notified about
	 * specific actions
	 * 
	 * @param analyzer
	 *            the given analyzer
	 */
	public void setAnalyzer(Analyzer analyzer);

	/**
	 * Invoking this method denotes that the physical network connectivity of
	 * the given host has been changed cause by churn.
	 * 
	 * @param host
	 *            the churn affected host
	 * @param reason
	 *            the given reason
	 */
	public void churnEvent(Host host, Reason reason);

	/**
	 * Informs the installed churn analyzers about the next session time
	 * calculated by the applied churn model
	 * 
	 * @param time
	 *            next session time in minutes (time = calculatedTime *
	 *            Simulator.MINUTE_UNIT);
	 */
	public void nextSessionTime(long time);

	/**
	 * Informs the installed churn analyzers about the next inter-session time
	 * calculated by the applied churn model
	 * 
	 * @param time
	 *            next inter-session time in minutes (time = calculatedTime *
	 *            Simulator.MINUTE_UNIT);
	 */
	public void nextInterSessionTime(long time);

	/**
	 * Informs the installed KbrOverlayAnalyzers about the forward of a routed
	 * message
	 * 
	 * @param sender
	 * @param receiver
	 * @param olMsg
	 *            the forwarded overlay Message
	 * @param hops
	 *            the hop count at message send
	 */
	public void overlayMessageForwarded(OverlayContact sender,
			OverlayContact receiver, Message olMsg, int hops);

	/**
	 * Informs the installed KbrOverlayAnalyzers about the delivery of a routed
	 * message
	 * 
	 * @param contact
	 *            the contact of the host the message is delivered at
	 * @param olMsg
	 *            the delivered overlay message
	 * @param hops
	 *            the hop count at message delivery
	 */
	public void overlayMessageDelivered(OverlayContact contact, Message olMsg,
			int hops);

	/**
	 * Informs the installed KbrOverlayAnalyzers about the start of a new query.
	 * A Query is defined as the procedure of routing a message through an
	 * overlay towards a key. The start of a query is then determined by the
	 * call of the route method of a KBR overlay node.
	 * 
	 * @param contact
	 *            the contact of the host that initiates the routing
	 * @param appMsg
	 *            the application message that is routed
	 */
	public void queryStarted(OverlayContact contact, Message appMsg);

	/**
	 * Informs the installed KbrOverlayAnalyzers about the fail of a query. A
	 * Query is defined as the procedure of routing a message through an overlay
	 * towards a key. The fail of a query happens if a host that is part of the
	 * routing process can not determine a next hop.
	 * 
	 * @param failedHop
	 *            the contact of the host that could not determine a next hop
	 * @param appMsg
	 *            the application message of the query that failed
	 */
	public void queryFailed(OverlayContact failedHop, Message appMsg);

	/**
	 * Informs the installed {@link AggregationAnalyzer} about the start of an
	 * aggregation query.
	 * 
	 * @param host
	 *            The host, that starts the query
	 * @param identifier
	 *            the identifier of the value for which an aggregation result
	 *            shall be returned.
	 * @param UID
	 *            An unique identifier for this query.
	 */
	public void aggregationQueryStarted(Host host, Object identifier, Object UID);

	/**
	 * Informs the installed {@link AggregationAnalyzer} about the success of an
	 * aggregation query.
	 * 
	 * @param host
	 *            The host, that starts the query
	 * @param identifier
	 *            the identifier of the value for which an aggregation result
	 *            shall be returned.
	 * @param UID
	 *            The unique identifier for this query
	 * @param result
	 *            The result of the aggregation query
	 */
	public void aggregationQuerySucceeded(Host host, Object identifier,
			Object UID, IAggregationResult result);

	/**
	 * Informs the installed {@link AggregationAnalyzer} about the fail of an
	 * aggregation query.
	 * 
	 * @param host
	 *            The host, that starts the query
	 * @param identifier
	 *            the identifier of the value for which an aggregation result
	 *            shall be returned.
	 * @param UID
	 *            The unique identifier for this query
	 */
	public void aggregationQueryFailed(Host host, Object identifier, Object UID);

}
