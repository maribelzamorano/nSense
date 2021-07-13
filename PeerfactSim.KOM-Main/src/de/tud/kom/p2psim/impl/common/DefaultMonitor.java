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


package de.tud.kom.p2psim.impl.common;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.analyzer.Analyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.AggregationAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.ChurnAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.ConnectivityAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.KBROverlayAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.OperationAnalyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.TransAnalyzer;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.common.Monitor;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.scenario.Configurator;
import de.tud.kom.p2psim.api.scenario.HostBuilder;
import de.tud.kom.p2psim.api.service.aggr.IAggregationResult;
import de.tud.kom.p2psim.impl.simengine.SimulationEvent;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.AbstractTransMessage;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class DefaultMonitor implements Monitor {
	private static Logger log = SimLogger.getLogger(DefaultMonitor.class);

	private BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
			System.out));

	private HostBuilder hostBuilder;

	private boolean isMonitoring;

	/** Monitor list for base monitor. */
	private final List<Analyzer> baseAnalyzers;

	/** List of analyzers monitoring at the network layer. */
	protected final List<NetAnalyzer> netAnalyzers;

	/** List of analyzers monitoring at the transport layer. */
	protected final List<TransAnalyzer> transAnalyzers;

	/** List of analyzers monitoring the executed operation. */
	protected final List<OperationAnalyzer> opAnalyzers;

	/**
	 * List of analyzers monitoring the session times of peers as well as their
	 * arrival and depature.
	 */
	protected final List<ChurnAnalyzer> churnAnalyzers;

	/**
	 * List of analyzers monitoring at the overlay layer overlays that implement
	 * the kbr-interface.
	 */
	protected final List<KBROverlayAnalyzer> overlayAnalyzers;

	protected final List<ConnectivityAnalyzer> connAnalyzers;

	/**
	 * Storage for aggregation analyzers
	 */
	protected final List<AggregationAnalyzer> aggregationAnalyzers;

	public DefaultMonitor() {
		this.baseAnalyzers = new LinkedList<Analyzer>();
		this.netAnalyzers = new LinkedList<NetAnalyzer>();
		this.transAnalyzers = new LinkedList<TransAnalyzer>();
		this.churnAnalyzers = new LinkedList<ChurnAnalyzer>();
		this.opAnalyzers = new LinkedList<OperationAnalyzer>();
		this.connAnalyzers = new LinkedList<ConnectivityAnalyzer>();
		this.overlayAnalyzers = new LinkedList<KBROverlayAnalyzer>();
		this.aggregationAnalyzers = new LinkedList<Analyzer.AggregationAnalyzer>();
		Simulator.getInstance().setMonitor(this);
		this.isMonitoring = false;
	}

	/** {@inheritDoc} */
	@Override
	public void setAnalyzer(Analyzer analyzer) {
		if (!this.baseAnalyzers.contains(analyzer)) {
			this.baseAnalyzers.add(analyzer);
			if (analyzer instanceof NetAnalyzer)
				this.netAnalyzers.add((NetAnalyzer) analyzer);
			if (analyzer instanceof TransAnalyzer)
				this.transAnalyzers.add((TransAnalyzer) analyzer);
			if (analyzer instanceof OperationAnalyzer)
				this.opAnalyzers.add((OperationAnalyzer) analyzer);
			if (analyzer instanceof ChurnAnalyzer)
				this.churnAnalyzers.add((ChurnAnalyzer) analyzer);
			if (analyzer instanceof ConnectivityAnalyzer)
				this.connAnalyzers.add((ConnectivityAnalyzer) analyzer);
			if (analyzer instanceof KBROverlayAnalyzer)
				this.overlayAnalyzers.add((KBROverlayAnalyzer) analyzer);
			if (analyzer instanceof AggregationAnalyzer)
				this.aggregationAnalyzers.add((AggregationAnalyzer) analyzer);
		}
	}

	@Override
	public void operationInitiated(Operation<?> op) {
		if (isMonitoring) {
			for (OperationAnalyzer opAna : this.opAnalyzers) {
				opAna.operationInitiated(op);
			}
		}
	}

	@Override
	public void operationFinished(Operation<?> op) {
		if (isMonitoring) {
			for (OperationAnalyzer opAna : this.opAnalyzers) {
				opAna.operationFinished(op);
			}
		}
	}

	@Override
	public void netMsgEvent(NetMessage msg, NetID id, Reason reason) {
		if (isMonitoring) {
			switch (reason) {
			case SEND:
				for (NetAnalyzer monitor : this.netAnalyzers) {
					monitor.netMsgSend(msg, id);
				}
				break;
			case RECEIVE:
				for (NetAnalyzer monitor : this.netAnalyzers) {
					monitor.netMsgReceive(msg, id);
				}
				break;
			case DROP:
				for (NetAnalyzer monitor : this.netAnalyzers) {
					monitor.netMsgDrop(msg, id);
				}
				break;
			default:
				throw new RuntimeException("error: reason (" + reason + ")");
			}
		}
	}

	@Override
	public void setStart(long time) {
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.MONITOR_START);
	}

	@Override
	public void setStop(long time) {
		Simulator.scheduleEvent(null, time, this,
				SimulationEvent.Type.MONITOR_STOP);
	}

	public void close() {
		if (this.baseAnalyzers.size() != 0) {
			try {
				output.write("*******************************************************\n");
				output.write("# Monitoring results \n");
				output.newLine();
				for (Analyzer analyzer : this.baseAnalyzers) {
					analyzer.stop(output);
				}
				output.write("*******************************************************\n");
				// output.close();

			} catch (IOException e) {
				log.error("Failed to print monitoring results.", e);
			}
		}
	}

	public void compose(Configurator config) {
		hostBuilder = (HostBuilder) config
				.getConfigurable(Configurator.HOST_BUILDER);
	}

	@Override
	public void eventOccurred(SimulationEvent se) {
		if (se.getType().equals(SimulationEvent.Type.MONITOR_START)) {
			this.isMonitoring = true;
			for (Analyzer analyzer : this.baseAnalyzers) {
				analyzer.start();
			}
		} else {
			this.close();
			this.isMonitoring = false;
		}
	}

	// public void finish() {
	// if (baseAnalyzers.size() != 0) {
	// try {
	// output.write("*******************************************************\n");
	// output.write("# Monitoring results \n");
	// output.newLine();
	// for (Analyzer analyzer : this.baseAnalyzers) {
	// analyzer.stop(output);
	// }
	// output.write("*******************************************************\n");
	// output.close();
	//
	// } catch (IOException e) {
	// throw new RuntimeException("Failed to print monitoring results.", e);
	// }
	// }
	// }

	/**
	 * Specifies where to write the monitoring results to.
	 * 
	 * @param output
	 *            writer (e.g. FileWriter, StringWriter, ...)
	 */
	public void setResultWriter(Writer output) {
		this.output = new BufferedWriter(output);
	}

	@Override
	public void churnEvent(Host host, Reason reason) {
		if (isMonitoring) {
			switch (reason) {
			case ONLINE:
				for (ConnectivityAnalyzer connAna : this.connAnalyzers)
					connAna.onlineEvent(host);
				break;
			case OFFLINE:
				for (ConnectivityAnalyzer connAna : this.connAnalyzers)
					connAna.offlineEvent(host);
				break;
			default:
				throw new RuntimeException("error: reason (" + reason + ")");
			}
		}
	}

	@Override
	public void transMsgReceived(final AbstractTransMessage msg) {
		if (isMonitoring) {
			for (final TransAnalyzer monitor : this.transAnalyzers) {
				monitor.transMsgReceived(msg);
			}
		}
	}

	@Override
	public void transMsgSent(final AbstractTransMessage msg) {
		if (isMonitoring) {
			for (final TransAnalyzer monitor : this.transAnalyzers) {
				monitor.transMsgSent(msg);
			}
		}
	}

	@Override
	public void nextInterSessionTime(long time) {
		if (isMonitoring) {
			for (ChurnAnalyzer churnAna : this.churnAnalyzers)
				churnAna.nextInterSessionTime(time);
		}
	}

	@Override
	public void nextSessionTime(long time) {
		if (isMonitoring) {
			for (ChurnAnalyzer churnAna : this.churnAnalyzers)
				churnAna.nextSessionTime(time);
		}
	}

	@Override
	public void overlayMessageDelivered(OverlayContact contact, Message msg,
			int hops) {
		if (isMonitoring) {
			for (KBROverlayAnalyzer overlayAna : this.overlayAnalyzers) {
				overlayAna.messageDelivered(contact, msg, hops);
			}
		}
	}

	@Override
	public void overlayMessageForwarded(OverlayContact sender,
			OverlayContact receiver, Message msg, int hops) {
		if (isMonitoring) {
			for (KBROverlayAnalyzer overlayAna : this.overlayAnalyzers) {
				overlayAna.messageForwarded(sender, receiver, msg, hops);
			}
		}
	}

	@Override
	public void queryStarted(OverlayContact contact, Message appMsg) {
		if (isMonitoring) {
			for (KBROverlayAnalyzer overlayAna : this.overlayAnalyzers) {
				overlayAna.queryStarted(contact, appMsg);
			}
		}
	}

	@Override
	public void queryFailed(OverlayContact failedHop, Message appMsg) {
		if (isMonitoring) {
			for (KBROverlayAnalyzer overlayAna : this.overlayAnalyzers) {
				overlayAna.queryFailed(failedHop, appMsg);
			}
		}
	}

	@Override
	public void aggregationQueryStarted(Host host, Object identifier, Object UID) {
		if (isMonitoring) {
			for (AggregationAnalyzer aggrAna : this.aggregationAnalyzers) {
				aggrAna.aggregationQueryStarted(host, identifier, UID);
			}
		}
	}

	@Override
	public void aggregationQuerySucceeded(Host host, Object identifier,
			Object UID, IAggregationResult result) {
		if (isMonitoring) {
			for (AggregationAnalyzer aggrAna : this.aggregationAnalyzers) {
				aggrAna.aggregationQuerySucceeded(host, identifier, UID, result);
			}
		}

	}

	@Override
	public void aggregationQueryFailed(Host host, Object identifier, Object UID) {
		if (isMonitoring) {
			for (AggregationAnalyzer aggrAna : this.aggregationAnalyzers) {
				aggrAna.aggregationQueryFailed(host, identifier, UID);
			}
		}
	}

}
