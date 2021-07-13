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


package de.tud.kom.p2psim.api.analyzer;

import java.io.Writer;

import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Monitor;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.service.aggr.IAggregationResult;
import de.tud.kom.p2psim.impl.transport.AbstractTransMessage;

/**
 * In general, analyzers are used to receive notifications about actions that
 * took place on specific components, for instance the sending or receiving of
 * messages. In particular, analyzers are able to collect data during a
 * simulation run and prepare the results at the end of a simulation.
 * 
 * Note that analyzers must be registered by an implementation of the
 * {@link Monitor} interface by using the xml configuration file before the
 * simulation starts.
 * 
 * @author Sebastian Kaune <peerfact@kom.tu-darmstadt.de>
 * @author Konstantin Pussep
 * @version 4.0, 03/10/2011
 * 
 */
public interface Analyzer {

	/**
	 * TransAnalyzers receive notifications when a network message is sent, or
	 * received at the transport layer.
	 * 
	 */
	public interface TransAnalyzer extends Analyzer {

		/**
		 * Invoking this method denotes that the given message is sent at the
		 * transport layer (from the application towards the network layer).
		 * 
		 * @param msg
		 *            the AbstractTransMessage which is sent out.
		 */
		public void transMsgSent(AbstractTransMessage msg);

		/**
		 * Invoking this method denotes that the given message is received at
		 * the transport layer (from the network layer towards the application
		 * layer).
		 * 
		 * @param msg
		 *            the received AbstractTransMessage.
		 */
		public void transMsgReceived(AbstractTransMessage msg);

	}

	/**
	 * NetAnalyzers receive notifications when a network message is send,
	 * received or dropped at the network layer.
	 * 
	 */
	public interface NetAnalyzer extends Analyzer {
		/**
		 * Invoking this method denotes that the given network message is sent
		 * at the network layer with the given NetID
		 * 
		 * @param msg
		 *            the message which is send out
		 * @param id
		 *            the NetID of the sender of the given message
		 */
		public void netMsgSend(NetMessage msg, NetID id);

		/**
		 * Invoking this method denotes that the given network message is
		 * received at the network layer with the given NetID
		 * 
		 * @param msg
		 *            the received message
		 * @param id
		 *            the NetID of the receiver of the given message
		 */
		public void netMsgReceive(NetMessage msg, NetID id);

		/**
		 * Invoking this method denotes that the given network message is
		 * dropped at the network layer with the given NetID (due to packet loss
		 * or the receiving network layer has no physical connectivity).
		 * 
		 * @param msg
		 *            the dropped message
		 * @param id
		 *            the NetID of the receiver at which the message is droped
		 */
		public void netMsgDrop(NetMessage msg, NetID id);
	}

	/**
	 * OperationAnalyzers receive notifications when a operation is triggered or
	 * finished either with or without success.
	 * 
	 */
	public interface OperationAnalyzer extends Analyzer {

		/**
		 * This method is called whenever an operation has been triggered.
		 * 
		 * @param op
		 *            the Operation that has been triggered.
		 */
		public void operationInitiated(Operation<?> op);

		/**
		 * This method is called whenever an operation has completed.
		 * 
		 * @param op
		 *            the Operation that has completed.
		 */
		public void operationFinished(Operation<?> op);

	}

	/**
	 * ConnectivityAnalyzers receive notifications when the network connectivity
	 * has been changed of churn affected hosts
	 * 
	 */
	public interface ConnectivityAnalyzer extends Analyzer {

		/**
		 * Invoking this method denotes that the given host does not have
		 * network connectivity
		 * 
		 * @param host
		 *            the churn affected host
		 */
		public void offlineEvent(Host host);

		/**
		 * Invoking this method denotes that the given host does have network
		 * connectivity
		 * 
		 * @param host
		 *            the churn affected host
		 */
		public void onlineEvent(Host host);
	}

	/**
	 * ChurnAnalyzers receive notifications about the session/inter-session
	 * times of the hosts. Thus, it is possible to estimate the mean/median
	 * session/inter-session lengths of the applied churn model.
	 * 
	 */
	public interface ChurnAnalyzer extends Analyzer {

		/**
		 * Informs the correspondent churn analyzer about the next session time
		 * calculated by the applied churn model
		 * 
		 * @param time
		 *            the next session time in minutes (time = calculatedTime *
		 *            Simulator.MINUTE_UNIT);
		 */
		public void nextSessionTime(long time);

		/**
		 * Informs the correspondent churn analyzer about the next inter-session
		 * time calculated by the applied churn model
		 * 
		 * @param time
		 *            the next inter-session time in minutes (time =
		 *            calculatedTime * Simulator.MINUTE_UNIT);
		 */
		public void nextInterSessionTime(long time);
	}

	/**
	 * KBROverlayAnalyzers receive notifications about events on the KBR layer.
	 * This way it is possible to collect data independent of the used overlay.
	 * To use this kind of analyzer in a meaningful way you have to use an
	 * application that uses the KBR layer.
	 * 
	 * @author Julius Rueckert
	 * 
	 */
	public interface KBROverlayAnalyzer extends Analyzer {

		/**
		 * Informs about the forward of a routed message on the KBR layer
		 * 
		 * @param sender
		 *            the contact of the forwarding peer
		 * @param receiver
		 *            the contact of the destination peer
		 * @param msg
		 *            the forwarded message
		 * @param hops
		 *            the current number of hops of the forwarded message
		 */
		public void messageForwarded(OverlayContact sender,
				OverlayContact receiver, Message msg, int hops);

		/**
		 * Informs about the delivery of a routed message on the KBR layer
		 * 
		 * @param contact
		 *            the contact of the peer that the message is delivered at
		 * @param msg
		 *            the delivered message
		 * @param hops
		 *            the final number of hops for the whole routing of the
		 *            message
		 */
		public void messageDelivered(OverlayContact contact, Message msg,
				int hops);

		/**
		 * Informs about the start of a new query on the KBR layer. A query is
		 * started when a message is routed towards a key and not to a concrete
		 * receiver. These messages are sent to a next hop determined by the
		 * overlay.
		 * 
		 * @param contact
		 *            the contact of the peer that starts the query
		 * @param appMsg
		 *            the application message that is routed
		 */
		public void queryStarted(OverlayContact contact, Message appMsg);

		/**
		 * Informs about the fail of a query on the KBR layer. This happens if a
		 * host can not determine a next hop during the routing process.
		 * 
		 * @param failedHop
		 *            the host that could not determine a next hop
		 * @param appMsg
		 *            the application message of the query
		 */
		public void queryFailed(OverlayContact failedHop, Message appMsg);
	}

	/**
	 * The AggregationAnalyzer receive notifications when a aggregation query is
	 * executed or the query is finished with success or failure.
	 * 
	 * @author Christoph Muenker
	 * @version 1.0, 03/10/2011
	 */
	public interface AggregationAnalyzer extends Analyzer {
		/**
		 * Informs about the start of an aggregation query.
		 * 
		 * @param host
		 *            The host, that starts the query
		 * @param identifier
		 *            the identifier of the value for which an aggregation
		 *            result shall be returned.
		 * @param UID
		 *            An unique identifier for this query.
		 */
		public void aggregationQueryStarted(Host host, Object identifier,
				Object UID);

		/**
		 * Informs about the success of an aggregation query.
		 * 
		 * @param host
		 *            The host, that starts the query
		 * @param identifier
		 *            the identifier of the value for which an aggregation
		 *            result shall be returned.
		 * @param UID
		 *            The unique identifier for this query
		 * @param result
		 *            The result of the aggregation query
		 */
		public void aggregationQuerySucceeded(Host host, Object identifier,
				Object UID, IAggregationResult result);

		/**
		 * Informs about the fail of an aggregation query.
		 * 
		 * @param host
		 *            The host, that starts the query
		 * @param identifier
		 *            the identifier of the value for which an aggregation
		 *            result shall be returned.
		 * @param UID
		 *            The unique identifier for this query
		 */
		public void aggregationQueryFailed(Host host, Object identifier,
				Object UID);
	}

	/**
	 * Invoking this method denotes start running analyzer
	 * 
	 */
	public void start();

	/**
	 * Invoking this method denotes stop running analyzer. Furthermore, all
	 * results have to be prepared and printed out using the given writer
	 * 
	 * @param output
	 *            the given output writer
	 */
	public void stop(Writer output);

}
