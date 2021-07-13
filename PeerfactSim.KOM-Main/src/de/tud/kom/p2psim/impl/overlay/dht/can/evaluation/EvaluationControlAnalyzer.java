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


package de.tud.kom.p2psim.impl.overlay.dht.can.evaluation;

import java.io.Writer;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.analyzer.Analyzer;
import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanConfig;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This analyzer is used to start the StatisticGenerationEvent, which
 * writes the measured data into a file.
 * The analyzer collects as well the send and received messages.
 * 
 * Implements Analyzer and NetAnalyzer
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010 
 *
 */
public class EvaluationControlAnalyzer implements Analyzer, NetAnalyzer{

	final static Logger log = SimLogger
			.getLogger(EvaluationControlAnalyzer.class);

	StatisticGenerationEvent event;
	
	private long receivedBytes, sentBytes;
	private int receivedMsg, sentMsg;

	/**
	 * starts the analyzer which starts the StatisticGernerationEvent
	 */
	public void start() {
		event = new StatisticGenerationEvent(this);
		event.writerStarted();
		event.scheduleImmediatly();
		receivedBytes=0;
		sentBytes=0;
		receivedMsg=0;
		sentMsg=0;
	}

	public void stop(Writer output) {
		event.writerStopped();

	}
	
	
	///////////////////////////////////////////////////
	//send and received messages
	/**
	 * collects all send messages
	 */
	public void netMsgSend(NetMessage msg, NetID id){
		addSentMsg(msg);
		sentMsg++;
	}
	
	/**
	 * collects all received messages
	 */
	public void netMsgReceive(NetMessage msg, NetID id){
		addReceivedMsg(msg);
		receivedMsg++;
	}
	
	/**
	 * counts the bytes of all received messages
	 * 
	 * @param msg
	 */
	public void addReceivedMsg(NetMessage msg){
		receivedBytes += msg.getSize();
	}
	
	/**
	 * gives the bytes of all received messages
	 * 
	 * @param globalNodes
	 * 		all nodes in the CAN
	 * @return
	 * 		double[] = {outputPerMin,outputPerMinPerNode}
	 */
	public double[] getReceivedBytes(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes){
		double outputPerMin=receivedBytes*Simulator.MINUTE_UNIT / CanConfig.intervallBetweenStatistics;
		double outputPerMinPerNode= outputPerMin/globalNodes.size();
		receivedBytes=0;
		double[] output = {outputPerMin,outputPerMinPerNode};
		return output;
	}
	
	/**
	 * counts the bytes of all send messages
	 * 
	 * @param msg
	 */
	public void addSentMsg(NetMessage msg){
		sentBytes += msg.getSize();
	}
	
	/**
	 * gives the bytes of all received messages
	 * 
	 * @param globalNodes
	 * 		all nodes in the CAN
	 * @return
	 * 		double[] = {outputPerMin,outputPerMinPerNode}
	 */
	public double[] getSentBytes(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes){
		double outputPerMin = sentBytes*Simulator.MINUTE_UNIT / CanConfig.intervallBetweenStatistics;
		double outputPerMinPerNode = outputPerMin/globalNodes.size();
		sentBytes=0;

		double[] output = {outputPerMin,outputPerMinPerNode};
		return output;
	}
	
	/**
	 * gives the number of all send messages
	 * 
	 * @param globalNodes
	 * 		all nodes in the CAN
	 * @return
	 * 		double[] = {outputPerMin,outputPerMinPerNode}
	 */
	public double[] getSentMsg(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes){
		double outputPerMin = sentMsg*Simulator.MINUTE_UNIT / CanConfig.intervallBetweenStatistics;
		double outputPerMinPerNode = outputPerMin/globalNodes.size();
		sentMsg=0;

		double[] output = {outputPerMin,outputPerMinPerNode};
		return output;
	}
	
	/**
	 * gives the number of all received messages
	 * 
	 * @param globalNodes
	 * 		all nodes in the CAN
	 * @return
	 * 		double[] = {outputPerMin,outputPerMinPerNode}
	 */
	public double[] getReceivedMsg(
			LinkedHashMap<CanOverlayID, CanNode> globalNodes){
		double outputPerMin = receivedMsg*Simulator.MINUTE_UNIT / CanConfig.intervallBetweenStatistics;
		double outputPerMinPerNode = outputPerMin/globalNodes.size();
		receivedMsg=0;

		double[] output = {outputPerMin,outputPerMinPerNode};
		return output;
	}
	

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		
	}

}
