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

import java.io.Writer;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.analyzer.Analyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.TransAnalyzer;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayID;
import de.tud.kom.p2psim.impl.overlay.gnutella04.GnutellaOverlayRoutingTable;
import de.tud.kom.p2psim.impl.overlay.gnutella04.filesharing.FilesharingDocument;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.BaseMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.ConnectMessage;
import de.tud.kom.p2psim.impl.overlay.gnutella04.messages.OkMessage;
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
public class MessageAnalyzer implements Analyzer, TransAnalyzer, OperationAnalyzer {

	final static Logger log = SimLogger.getLogger(MessageAnalyzer.class);

	private static final String url = "jdbc:mysql://localhost/";
	private static final String user = "ba";
	private static final String pwd = "uiaenrtd";

	private Connection connection = null;

	private int stateID = 0;
	private long stateSimulatorTime = 0;

	private static final GnutellaOverlayID testnode = new GnutellaOverlayID(BigInteger.valueOf(2345));
	
	public void start() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, user, pwd);
			Statement statement = connection.createStatement();
			statement.execute("TRUNCATE `ba`.`queries`;");
			statement.execute("TRUNCATE `ba`.`ressources`;");
			statement.execute("TRUNCATE `ba`.`state`;");
			statement.execute("TRUNCATE `ba`.`connections`;");
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void stop(Writer output) {
		try {
			if (connection != null) {
				Statement statement = connection.createStatement();

				// set queries_total and hits_total
				String sql = "UPDATE `ba`.`queries`"
						+ "SET `queries_total` = `queries_hop_0` + `queries_hop_1` + `queries_hop_2` + `queries_hop_3` + `queries_hop_4` + `queries_hop_5` + `queries_hop_6`, "
						+ "`hits_total` = `hits_hop_0` + `hits_hop_1` + `hits_hop_2` + `hits_hop_3` + `hits_hop_4` + `hits_hop_5` + `hits_hop_6`, "
						+ "`testnode_queries_total` = `testnode_queries_hop_0` + `testnode_queries_hop_1` + `testnode_queries_hop_2` + `testnode_queries_hop_3` + `testnode_queries_hop_4` + `testnode_queries_hop_5` + `testnode_queries_hop_6`, "
						+ "`testnode_hits_total` = `testnode_hits_hop_0` + `testnode_hits_hop_1` + `testnode_hits_hop_2` + `testnode_hits_hop_3` + `testnode_hits_hop_4` + `testnode_hits_hop_5` + `testnode_hits_hop_6`;";
				statement.execute(sql);

				// update total ressources
				sql = "INSERT INTO `ba`.`ressources` SELECT DISTINCT `state_id`, `rank`, count(id) AS ressources FROM `ba`.`state` GROUP BY `state_id`, `rank`;";
				statement.execute(sql);
				sql = "UPDATE `ba`.`queries`, `ba`.`ressources` SET `res_total` = `ressources` WHERE `queries`.`state_id` = `ressources`.`state_id` AND `file_rank` = `rank`;";
				statement.execute(sql);

				statement.close();

				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void transMsgReceived(AbstractTransMessage transMessage) {
		//
	}

	public void transMsgSent(AbstractTransMessage transMessage) {
		if (connection != null) {
			try {
				if (transMessage.getPayload() instanceof ConnectMessage) {
					ConnectMessage message = (ConnectMessage) transMessage.getPayload();

					Statement statement = connection.createStatement();
					String sql = "UPDATE `ba`.`connections`" + "SET `connect` = `connect` + 1" + " WHERE `state_id` = " + stateID + " AND `node` = " + message.getSender() + ";";
					statement.execute(sql);
					statement.close();
				} else if (transMessage.getPayload() instanceof OkMessage) {
					OkMessage message = (OkMessage) transMessage.getPayload();

					Statement statement = connection.createStatement();
					String sql = "UPDATE `ba`.`connections`" + "SET `ok` = `ok` + 1" + " WHERE `state_id` = " + stateID + " AND `node` = " + message.getSender() + ";";
					statement.execute(sql);
					statement.close();
				} else if (transMessage.getPayload() instanceof BaseMessage) {
					BaseMessage message = (BaseMessage) transMessage.getPayload();

					BigInteger sender = (BigInteger) message.getSender().getUniqueValue();
					BigInteger receiver = (BigInteger) message.getReceiver().getUniqueValue();
					String messageType = "";
					BigInteger messageSize = BigInteger.valueOf(message.getSize());
					Integer hops = message.getHops();
					Integer ttl = message.getTTL();
					BigInteger descriptor = message.getDescriptor();
					String fileRank = null;

					if (message instanceof QueryMessage) {
						QueryMessage queryMessage = (QueryMessage) message;
						fileRank = queryMessage.getKey().toString();

						Statement statement = connection.createStatement();

						// check if descriptor already used
						String sql = "SELECT `id` FROM `ba`.`queries` WHERE `descriptor` = " + descriptor + ";";
						statement.execute(sql);
						ResultSet resultSet = statement.getResultSet();
						// if first message descriptor, insert into database
						if (!resultSet.next()) {
							sql = "INSERT INTO `ba`.`queries` (`descriptor` ,`initiator` ,`file_rank`, `state_id`) VALUES (" + descriptor + "," + sender + "," + fileRank + "," + stateID + ");";
							statement.execute(sql);
						}

						// add query hop
						sql = "UPDATE `ba`.`queries`" + "SET `queries_hop_" + hops + "` = `queries_hop_" + hops + "` + 1, " + "`traffic` = `traffic` + " + messageSize + " WHERE `descriptor` = "
								+ descriptor + ";";
						statement.execute(sql);
						
						// add query hop, if testnode
						if(message.getReceiver().equals(testnode)) {
							sql = "UPDATE `ba`.`queries`" + "SET `testnode_queries_hop_" + hops + "` = `testnode_queries_hop_" + hops + "` + 1 WHERE `descriptor` = "
							+ descriptor + ";";
							statement.execute(sql);
							sql = "UPDATE `ba`.`connections` SET `traffic_to_testnode` = `traffic_to_testnode` + " + messageSize + " WHERE `state_id` = " + stateID + " AND `node` = " + message.getSender() + ";";
							statement.execute(sql);
						}

						statement.close();
					} else if (message instanceof QueryHitMessage) {
						Statement statement = connection.createStatement();

						// substract hit from previous hop
						String sqlSubstractHop = "";
						if (hops > 0) {
							sqlSubstractHop = "`hits_hop_" + (hops - 1) + "` = `hits_hop_" + (hops - 1) + "` - 1, ";
						}

						// add hit hop
						String sql = "UPDATE `ba`.`queries`" + "SET `hits_hop_" + hops + "` = `hits_hop_" + hops + "` + 1, " + sqlSubstractHop + "`traffic` = `traffic` + " + messageSize
								+ " WHERE `descriptor` = " + descriptor + ";";
						statement.execute(sql);

						// add hit hop, if testnode
						if(message.getReceiver().equals(testnode)) {
							sql = "UPDATE `ba`.`queries`" + "SET `testnode_hits_hop_" + hops + "` = `testnode_hits_hop_" + hops + "` + 1 WHERE `descriptor` = " + descriptor + ";";
							statement.execute(sql);
							sql = "UPDATE `ba`.`connections` SET `traffic_to_testnode` = `traffic_to_testnode` + " + messageSize + " WHERE `state_id` = " + stateID + " AND `node` = " + message.getSender() + ";";
							statement.execute(sql);
							sql = "UPDATE `ba`.`connections` SET `hits_to_testnode` = `hits_to_testnode` + 1 WHERE `state_id` = " + stateID + " AND `node` = " + message.getSender() + ";";
							statement.execute(sql);
						}
						statement.close();
					}
				}
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
			try {
				for (FilesharingDocument document : scheduleStateOperation.getComponent().getDocuments()) {
					String fields = "INSERT INTO `ba`.`state` (`state_id` , `node` , `rank`";
					String values = ") VALUES ('" + this.stateID + "', '" + scheduleStateOperation.getNode() + "', '" + document.getPopularity() + "'";
					String end = ");";
					Statement statement = connection.createStatement();
					statement.execute(fields + values + end);
					statement.close();
				}
				String fields = "INSERT INTO `ba`.`connections` (`time`, `state_id` , `node`, `num_connections`";
				String values = ") VALUES ('" + Simulator.getCurrentTime() + "', '" + this.stateID + "', '" + scheduleStateOperation.getNode() + "', '"
						+ ((GnutellaOverlayRoutingTable) scheduleStateOperation.getNode().getRoutingTable()).numberOfActiveContacts() + "'";
				String end = ");";
				Statement statement = connection.createStatement();
				statement.execute(fields + values + end);
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
