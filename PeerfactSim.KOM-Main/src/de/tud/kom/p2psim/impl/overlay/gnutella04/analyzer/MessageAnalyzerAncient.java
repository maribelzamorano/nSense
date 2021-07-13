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


package de.tud.kom.p2psim.impl.overlay.gnutella04.analyzer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.analyzer.Analyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.TransAnalyzer;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayID;
import de.tud.kom.p2psim.impl.overlay.gnutella04.filesharing.FilesharingDocument;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.BaseMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.ConnectMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.OkMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.PingMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.PongMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.PushMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.QueryHitMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.QueryMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.operations.ScheduleStateOperation;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.AbstractTransMessage;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class MessageAnalyzerAncient implements Analyzer, TransAnalyzer,
		OperationAnalyzer {

	final static Logger log = SimLogger.getLogger(MessageAnalyzerAncient.class);

	private static final String url = "jdbc:mysql://localhost/";

	private static final String user = "ba";

	private static final String pwd = "uiaenrtd";

	private Connection connection = null;

	private Statement statement = null;

	private FileWriter fileWriter;

	private BufferedWriter bufferedWriter;

	private int stateID = 0;

	private long stateSimulatorTime = 0;

	private Map<Message, BigInteger> messages = new HashMap<Message, BigInteger>();

	public void start() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, user, pwd);
			statement = connection.createStatement();
			statement.execute("TRUNCATE `ba`.`messages_received`;");
			statement.execute("TRUNCATE `ba`.`messages_sent`;");
			statement.execute("TRUNCATE `ba`.`state`;");
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void stop(Writer output) {
		try {
			if (connection != null) {
				// statement.close();
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void transMsgReceived(AbstractTransMessage transMessage) {
		Message message = transMessage.getPayload();
		if (messages.containsKey(message)) {
			String query = "INSERT INTO `ba`.`messages_received` (`id`,`time_received`) VALUES ("
					+ messages.get(message)
					+ ",'"
					+ Simulator.getCurrentTime()
					+ "');";
			messages.remove(message);
			try {
				statement = connection.createStatement();
				statement.execute(query);
				statement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.error("Message received but not sent: " + transMessage);
		}
	}

	public void transMsgSent(AbstractTransMessage transMessage) {
		if (connection != null) {
			try {
				AbstractOverlayMessage<GnutellaOverlayID> message = (AbstractOverlayMessage<GnutellaOverlayID>) transMessage
						.getPayload();
				Integer hashCode = Integer.valueOf(message.hashCode());
				String timeFormated = Simulator.getSimulatedRealtime();
				BigInteger time = BigInteger
						.valueOf(Simulator.getCurrentTime());
				BigInteger sender = (BigInteger) message.getSender()
						.getUniqueValue();
				BigInteger receiver = null;
				if (message.getReceiver() != null) {
					receiver = (BigInteger) message.getReceiver()
							.getUniqueValue();
				}
				String messageType = "";
				BigInteger messageSize = BigInteger.valueOf(message.getSize());
				Integer hops = null;
				Integer ttl = null;
				BigInteger descriptor = null;
				String fileRank = null;
				BigInteger initiator = null;
				BigInteger target = null;

				if (message instanceof BaseMessage) {
					BaseMessage baseMessage = (BaseMessage) message;
					hops = baseMessage.getHops();
					ttl = baseMessage.getTTL();
					descriptor = baseMessage.getDescriptor();
				}

				if (message instanceof ConnectMessage) {
					ConnectMessage connectMessage = (ConnectMessage) message;
					messageType = "ConnectMessage";
				} else if (message instanceof OkMessage) {
					OkMessage okMessage = (OkMessage) message;
					messageType = "OkMessage";
				} else if (message instanceof PingMessage) {
					PingMessage pingMessage = (PingMessage) message;
					messageType = "PingMessage";
				} else if (message instanceof PongMessage) {
					PongMessage pongMessage = (PongMessage) message;
					messageType = "PongMessage";
					initiator = (BigInteger) pongMessage.getContact()
							.getOverlayID().getUniqueValue();
				} else if (message instanceof QueryMessage) {
					QueryMessage queryMessage = (QueryMessage) message;
					messageType = "QueryMessage";
					fileRank = queryMessage.getKey().toString();
				} else if (message instanceof QueryHitMessage) {
					// TODO QueryHitMessage aufsplitten
					QueryHitMessage queryHitMessage = (QueryHitMessage) message;
					messageType = "QueryHitMessage";
					initiator = (BigInteger) queryHitMessage.getContact()
							.getOverlayID().getUniqueValue();
					fileRank = queryHitMessage.getKeys().toString();
				} else if (message instanceof PushMessage) {
					PushMessage pushMessage = (PushMessage) message;
					messageType = "PushMessage";
					fileRank = pushMessage.getKey().toString();
					initiator = (BigInteger) pushMessage.getPushSender()
							.getOverlayID().getUniqueValue();
					target = (BigInteger) pushMessage.getPushReceiver()
							.getUniqueValue();
				}
				String fields = "INSERT INTO `ba`.`messages_sent` (`hash_code`, `state_id`, `time_sent` , `time_sent_formated` ,`sender` ,`message_type` ,`message_size`";
				String values = ") VALUES ('" + hashCode + "', '"
						+ this.stateID + "', '" + time + "', '" + timeFormated
						+ "', '" + sender + "', '" + messageType + "', '"
						+ messageSize + "'";
				String end = ");";
				if (receiver != null) {
					fields += ", `receiver`";
					values += ", '" + receiver + "'";
				}
				if (hops != null) {
					fields += ", `hops`";
					values += ", '" + hops + "'";
				}
				if (ttl != null) {
					fields += ", `ttl`";
					values += ", '" + ttl + "'";
				}
				if (descriptor != null) {
					fields += ", `descriptor`";
					values += ", '" + descriptor + "'";
					fields += ", `descriptor_hash`";
					values += ", '"
							+ descriptor.and(
									BigInteger.valueOf(2).pow(41).subtract(
											BigInteger.valueOf(1))).hashCode()
							+ "'";
				}
				if (fileRank != null) {
					fields += ", `file_rank`";
					values += ", '" + fileRank + "'";
				}
				if (initiator != null) {
					fields += ", `initiator`";
					values += ", '" + initiator + "'";
				}
				if (target != null) {
					fields += ", `target`";
					values += ", '" + target + "'";
				}
				statement = connection.createStatement();
				statement.execute(fields + values + end);
				ResultSet generatedKeys = statement.getGeneratedKeys();
				if (generatedKeys.next()) {
					messages.put(message, BigInteger.valueOf(generatedKeys
							.getLong(1)));
				}
				statement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void operationFinished(Operation<?> op) {
		// 
	}

	public void operationInitiated(Operation<?> operation) {
		if (operation instanceof ScheduleStateOperation) {
			ScheduleStateOperation scheduleStateOperation = (ScheduleStateOperation) operation;
			if (stateSimulatorTime != Simulator.getCurrentTime()) {
				stateSimulatorTime = Simulator.getCurrentTime();
				stateID += 1;
			}
			for (FilesharingDocument document : scheduleStateOperation
					.getComponent().getDocuments()) {
				try {
					String fields = "INSERT INTO `ba`.`state` (`time`, `state_id` , `node` , `rank`";
					String values = ") VALUES ('" + Simulator.getCurrentTime()
							+ "', '" + this.stateID + "', '"
							+ scheduleStateOperation.getNode() + "', '"
							+ document.getPopularity() + "'";
					String end = ");";
					statement = connection.createStatement();
					statement.execute(fields + values + end);
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
