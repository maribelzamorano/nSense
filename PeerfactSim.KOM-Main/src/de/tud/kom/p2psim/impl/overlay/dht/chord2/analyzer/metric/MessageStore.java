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


package de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.metric;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.analyzer.ChordOverlayAnalyzer;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.components.ChordContact;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.IServiceMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.ISetupMessage;
import de.tud.kom.p2psim.impl.overlay.dht.chord2.messages.IStabilizeMessage;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This class collects the send and receive messages in common system.
 * After that the measured metrics will be calculated and saved in
 * a data file
 *  
 * @author Minh Hoang Nguyen  <peerfact@kom.tu-darmstadt.de>
 *
 * @version 05/06/2011
 */
public class MessageStore extends AbstractMetricStore{

	private static MessageStore instance = new MessageStore();
	
	private LinkedList<MessageProxy> sendMsgStore;
	
	private LinkedList<MessageProxy> receiveMsgStore;
	
	public static enum Metrics {
		SentSetupMessages("SentSystemMessages/Min"),
		RecSetupMessages("RecSystemMessages/Min"),
		SentServiceMessages("SentServiceMessages/Min"),
		RecServiceMessages("RecServiceMessages/Min"),
		
		SentSizeSetupMessages("SentSizeSystemMessages/Min"),
		RecSizeSetupMessages("RecSizeSystemMessages/Min"),
		SentSizeServiceMessages("SentSizeServiceMessages/Min"),
		RecSizeServiceMessages("RecSizeServiceMessages/Min");
		
		private final String label;

		Metrics(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}
	
	public static MessageStore getInstance() {
		return instance;
	}

	private MessageStore() {
		// Private constructor prevents instantiation from other classes
		sendMsgStore = new LinkedList<MessageProxy>();
		receiveMsgStore = new LinkedList<MessageProxy>();
	}
	
	@Override
	public List<String> getMetricList() {
		ArrayList<String> metricList = new ArrayList<String>();
		for (Metrics metric : Metrics.values()) {
			metricList.add(metric.toString());
		}
		return metricList;
	}

	@Override
	public double getMeasureValue(String metric, long begin, long end) {
		double min = (double) (end - begin) / Simulator.MINUTE_UNIT;
	
		if (Metrics.RecSetupMessages.equals(Metrics.valueOf(metric))) {
			return getRecSetupMessages(begin, end) / min;
		}
		else if (Metrics.RecServiceMessages.equals(Metrics.valueOf(metric))) {
			return getRecServiceMessages(begin, end) / min;
		}
		else if (Metrics.RecSizeSetupMessages.equals(Metrics.valueOf(metric))) {
			return getRecSizeSetupMessages(begin, end) / min;
		}
		else if (Metrics.RecSizeServiceMessages.equals(Metrics.valueOf(metric))) {
			return getRecSizeServiceMessages(begin, end) / min;
		}
		// send
		else if (Metrics.SentSetupMessages.equals(Metrics.valueOf(metric))) {
			return getSendSetupMessages(begin, end) / min;
		}
		else if (Metrics.SentServiceMessages.equals(Metrics.valueOf(metric))) {
			return getSendServiceMessages(begin, end) / min;
		}
		else if (Metrics.SentSizeSetupMessages.equals(Metrics.valueOf(metric))) {
			return getSendSizeSetupMessages(begin, end) / min;
		}
		else if (Metrics.SentSizeServiceMessages.equals(Metrics.valueOf(metric))) {
			return getSendSizeServiceMessages(begin, end) / min;
		}
		
		else {
			return -1;
		}
	}

	
	
	/**
	 * in interval [begin, end) 
	 */
	public double getRecSetupMessages(long begin, long end) {
		double count = 0;
		for (MessageProxy msg : receiveMsgStore) {
			if (isSystemMessage(msg.getMsgClass())) {
				if (begin <= msg.getTimeStamp() && msg.getTimeStamp() < end) {
					count++;
				}
			}
		}
		return count;
	}
	
	public double getRecServiceMessages(long begin, long end) {
		double count = 0;
		for (MessageProxy msg : receiveMsgStore) {
			if (isServiceMessage(msg.getMsgClass())) {
				if (begin <= msg.getTimeStamp() && msg.getTimeStamp() < end) {
					count++;
				}
			}
		}
		return count;
	}
	
	public double getRecSizeSetupMessages(long begin, long end) {
		double size = 0;
		for (MessageProxy msg : receiveMsgStore) {
			if (isSystemMessage(msg.getMsgClass())) {
				if (begin <= msg.getTimeStamp() && msg.getTimeStamp() < end) {
					size += msg.getSize();
				}
			}
		}
		return size;
	}
	
	public double getRecSizeServiceMessages(long begin, long end) {
		double size = 0;
		for (MessageProxy msg : receiveMsgStore) {
			if (isServiceMessage(msg.getMsgClass())) {
				if (begin <= msg.getTimeStamp() && msg.getTimeStamp() < end) {
					size += msg.getSize();
				}
			}
		}
		return size;
	}
	
	// send messages
	public double getSendSetupMessages(long begin, long end) {
		double count = 0;
		for (MessageProxy msg : sendMsgStore) {
			if (isSystemMessage(msg.getMsgClass())) {
				if (begin <= msg.getTimeStamp() && msg.getTimeStamp() < end) {
					count++;
				}
			}
		}
		return count;
	}
	
	public double getSendServiceMessages(long begin, long end) {
		double count = 0;
		for (MessageProxy msg : sendMsgStore) {
			if (isServiceMessage(msg.getMsgClass())) {
				if (begin <= msg.getTimeStamp() && msg.getTimeStamp() < end) {
					count++;
				}
			}
		}
		return count;
	}
	
	public double getSendSizeSetupMessages(long begin, long end) {
		double size = 0;
		for (MessageProxy msg : sendMsgStore) {
			if (isSystemMessage(msg.getMsgClass())) {
				if (begin <= msg.getTimeStamp() && msg.getTimeStamp() < end) {
					size += msg.getSize();
				}
			}
		}
		return size;
	}
	
	public double getSendSizeServiceMessages(long begin, long end) {
		double size = 0;
		for (MessageProxy msg : sendMsgStore) {
			if (isServiceMessage(msg.getMsgClass())) {
				if (begin <= msg.getTimeStamp() && msg.getTimeStamp() < end) {
					size += msg.getSize();
				}
			}
		}
		return size;
	}
	
	
	private boolean isSystemMessage(Class msg) {
		return (ISetupMessage.class.isAssignableFrom(msg) || IStabilizeMessage.class
				.isAssignableFrom(msg));
	}
	
	private boolean isServiceMessage(Class msg) {

		return IServiceMessage.class.isAssignableFrom(msg);
	}
	
	public void registReceiveMessage(ChordContact node, Message msg, long atTime){
		
		if(! ChordOverlayAnalyzer.messageStats ){
			return;
		}
		MessageProxy msgProxy = new MessageProxy(node, msg.getClass(), atTime,
				msg.getSize(), MessageProxy.SentOrReceive.RECEIVE); 
		receiveMsgStore.add(msgProxy);
	}
	
	public void registSendMessage(ChordContact sender, Message msg, long atTime){
		
		if(! ChordOverlayAnalyzer.messageStats ){
			return;
		}
		MessageProxy msgProxy = new MessageProxy(sender, msg.getClass(), atTime,
				msg.getSize(), MessageProxy.SentOrReceive.SEND); 
		sendMsgStore.add(msgProxy);
	}
}
