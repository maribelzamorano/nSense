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

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.CanMessage;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.LookupMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.LookupReplyMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.PingMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.PongMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.StoreMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.StoreReplyMsg;

/**
 * Collects the data of every node.
 * Is used to create the statistics.
 * It collects:
 * - number of hops of all received lookups
 * - the time which was needed to receive the answer for a lookup
 * - the number of all lookupStoreMsg, leaveJoinTakeoverMsg and 
 * 		StabilizeMsg
 * - the number of started lookup, the number of answers and the number of 
 * 		received lookups
 * - the hashes from the send lookups
 * - the received and send bytes
 * 
 * @author Bjoern Dollak	 <peerfact@kom.tu-darmstadt.de>
 * @version February 2010 
 *
 */
public class GetDataOperation extends AbstractOperation<CanNode, Object>{
	//private final static Logger log = SimLogger.getLogger(CanNode.class);
	
	private List<Integer> hopCount;
	private List<Long> timeForHops;
	
	private int lookupStoreMsg;
	private int leaveJoinTakeoverMsg;
	private int stabilizeMsg;
	
	private int startedLookup;
	private int receivedBackLookup;
	private int receivedLookupRequest;
	private LinkedHashMap<BigInteger, Integer> lookupValues;
	
	private long receivedBytes;
	private long sendBytes;
	
	/**
	 * Collects the data of every node.
	 * Is used to create the statistics.
	 * It collects:
	 * - number of hops of all received lookups
	 * - the time which was needed to receive the answer for a lookup
	 * - the number of all lookupStoreMsg, leaveJoinTakeoverMsg and 
	 * 		StabilizeMsg
	 * - the number of started lookup, the number of answers and the number of 
	 * 		received lookups
	 * - the hashes from the send lookups
	 * - the received and send bytes
	 * 
	 * @param component
	 * 		CanNode which collects the data
	 */
	public GetDataOperation(CanNode component) {
		super(component);
		
		hopCount=new LinkedList<Integer>();
		timeForHops = new LinkedList<Long>();
		lookupStoreMsg =0;
		leaveJoinTakeoverMsg =0;
		stabilizeMsg=0;
		startedLookup=0;
		receivedBackLookup=0;
		receivedLookupRequest=0;
		lookupValues=new LinkedHashMap<BigInteger,Integer>();
		receivedBytes=0;
		sendBytes=0;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * List of Integer how many hops the lookups needed to 
	 * arrive at the destination
	 * @return
	 */
	public List<Integer> getHopCount(){
		List<Integer> output = hopCount;
		hopCount = new LinkedList<Integer>();
		return output;
	}
		
	public void addHops(int hops){
		this.hopCount.add(hops);
	}
	
	/**
	 * List of times how long the lookups needed to arrive at
	 * the destination an travel back.
	 * @return
	 */
	public List<Long> getTimeForHop(){
		List<Long> output = timeForHops;
		timeForHops = new LinkedList<Long>();
		return output;
	}
	
	public void addTimeForHops(long time){
		this.timeForHops.add(time);
	}
	
	public void setMessage(CanMessage msg){
		if(msg instanceof LookupMsg || msg instanceof LookupReplyMsg 
				|| msg instanceof StoreMsg || msg instanceof StoreReplyMsg)
			lookupStoreMsg++;
		else if(msg instanceof PingMsg || msg instanceof PongMsg )
			stabilizeMsg++;
		else
			leaveJoinTakeoverMsg++;
		
		this.addReceivedBytes(msg.getSize());
	}
	
	public int getNumberLookupStore(){
		int output = lookupStoreMsg;
		lookupStoreMsg = 0;
		return output;
	}
	
	public int getNumberStabilizeMsg(){
		int output = stabilizeMsg;
		stabilizeMsg = 0;
		return output;
	}
	
	public int getNumberLeaveJoinTakeover(){
		int output = leaveJoinTakeoverMsg;
		leaveJoinTakeoverMsg=0;
		return output;
	}
	
	public void addStartedLookup(){
		startedLookup++;
	}
	
	public int getStartedLookup(){
		int output = startedLookup;
		startedLookup=0;
		return output;
	}
	
	public void addReceivedLookup(){
		receivedBackLookup++;
	}

	public int getReceivedLookup(){
		int output = receivedBackLookup;
		receivedBackLookup=0;
		return output;
	}
	
	public void addReceivedLookupRequest(){
		receivedLookupRequest++;
	}
	
	public int getReceivedLookupRequest(){
		return receivedLookupRequest;
	}
	
	/**
	 * adds the has value from a lookup
	 * @param value
	 */
	public void addNewLookupValue(BigInteger value){
		boolean saved=false;
		for(BigInteger request : lookupValues.keySet()){
			if(request==value){
				int lookups=lookupValues.get(request);
				lookupValues.remove(request);
				lookupValues.put(request, lookups+1);
				saved=true;
				break;
			}
		}
		if(saved==false)
			lookupValues.put(value, 1);
		
	}
	
	/**
	 * Gives a Map of all lookups and the number how often
	 * they were used
	 * @return
	 */
	public LinkedHashMap<BigInteger,Integer> getLookupValues(){
		return lookupValues;
	}
	
	public void addSendBytes(long value){
		this.sendBytes+=value;
	}
	
	public long getSendBytes(){
		long output=this.sendBytes;
		this.sendBytes=0;
		return output;
	}
	
	public void addReceivedBytes(long value){
		this.receivedBytes+=value;
	}
	
	public long getReceivedBytes(){
		long output=this.receivedBytes;
		this.receivedBytes=0;
		return output;
	}
}
