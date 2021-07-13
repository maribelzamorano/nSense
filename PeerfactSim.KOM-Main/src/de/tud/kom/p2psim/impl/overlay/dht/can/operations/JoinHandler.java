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


package de.tud.kom.p2psim.impl.overlay.dht.can.operations;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.transport.TransProtocol;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayID;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanVID;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.JoinMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.JoinOverloadMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.JoinReplyMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.NewNeighbourMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.NewVIDNeighbourMsg;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.transport.TransMsgEvent;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * 
 * This class is called if a new join message arrives. Either it adds a
 * contact to its overloaded area (this feature isn' implemented very well) or
 * it start the Overload join Operation. In the second case it takes all the 
 * peers in the area and give them a new area, neighbours, VID and VID neighbours. 
 * As well for the master of the area.
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010
 * 
 */
public class JoinHandler {

	private final static Logger log = SimLogger.getLogger(CanNode.class);
	
	/**
	 * Starts the joining process
	 * 
	 * @param node
	 * 		own CanNode
	 * @param join
	 * 		JoinMsg from the joining node
	 * @param receivingEvent
	 * 		TransMsgEvent from the joining message
	 */
	public JoinHandler(CanNode node, JoinMsg join, TransMsgEvent receivingEvent){
		if(node.getOverloadNumber()<node.getOverloadedContacts().size()){
			log.debug("Add to Overload.!");
			List<CanOverlayContact> addOverload = new LinkedList<CanOverlayContact>();
			addOverload.addAll(node.getOverloadedContacts());
			addOverload.add(join.getJoiningNode());
			JoinReplyMsg joinReply = new JoinReplyMsg((CanOverlayID)node.getOverlayID(), join.getSender(), node.getLocalContact().clone());
			node.getTransLayer().sendReply(joinReply, receivingEvent, node.getPort(), TransProtocol.TCP);
		}
		else{	//alle neuen Knoten werden in einer liste gespeichert
			log.debug("Overload full.");
			
			List<CanOverlayContact> addOverload = new LinkedList<CanOverlayContact>();
			addOverload.addAll(node.getOverloadedContacts());
			addOverload.add(join.getJoiningNode());
			
			//ein neues Objekt overloadjoin wird erzeugt
			OverloadJoin overloadJoin = new OverloadJoin(node, addOverload);
			List<CanOverlayContact> newContacts = overloadJoin.getNewMasters();
			
			CanOverlayContact[] oldVIDNeighbours = node.getVIDNeighbours();
			
			
			List<CanOverlayContact> sendNeighbours = new LinkedList<CanOverlayContact>();
			try{
				for(int i=0;i<newContacts.size();i++){
					for (int j=0;j<node.getNeighbours().size();j++){
						if (newContacts.get(i).getArea().commonCorner(node.getNeighbours().get(j).getArea())){
							sendNeighbours.add(newContacts.get(i));
						}
					}
				}
				for(int i=0;i<sendNeighbours.size();i++){
					List<CanOverlayContact> oldNeighbours = new LinkedList<CanOverlayContact>();
					oldNeighbours.add(node.getLocalContact().clone());
					NewNeighbourMsg newNeighbourMsg = new NewNeighbourMsg(node.getLocalContact().getOverlayID(), 
							node.getNeighbours().get(i).getOverlayID(), node.getLocalContact().clone(), 
							oldNeighbours, sendNeighbours);
					node.getTransLayer().send(newNeighbourMsg, 
							node.getNeighbours().get(i).getTransInfo(), node.getPort(), 
							TransProtocol.TCP);
				}
			}catch (Exception e){
				//just in case
			}
			
								
			//neue Nachbarnliste erstellen
			List<CanOverlayContact> newNeighbour = new LinkedList<CanOverlayContact>();
			newNeighbour.addAll(newContacts);
			
			if(node.getNeighbours()!=null)
				newNeighbour.addAll(node.getNeighbours());
			
			
			log.debug(node.getLocalContact().getArea().getVid().toString()
			           + " " + node.getVIDNeighbours()[0].getArea().getVid().toString() 
			           + " " + node.getVIDNeighbours()[1].getArea().getVid().toString() );
			
			List<CanVID[]> allNewVidNeighbours = new LinkedList<CanVID[]>();
			allNewVidNeighbours.addAll(overloadJoin.newVidNeighbours(node.getVIDNeighbours()));
			
		
			for(int i=0;i<newContacts.size();i++){
				//die neuen nachbarn zu jedem neuen Knoten werden ausgelessen und danach 
				//bekommt jeder knoten eine nachricht mit der neuen fläche und den nachbarn
				newNeighbour = new LinkedList<CanOverlayContact>();
				newNeighbour.addAll(newContacts);
				
				if(node.getNeighbours()!=null)
					newNeighbour.addAll(node.getNeighbours());
				
				newNeighbour=overloadJoin.getNeighboursToNewArea(
						newContacts.get(i).getArea().getArea(), newNeighbour);
				
				//vid Nachbarn einteilen
				CanOverlayContact[] sendVidNeighbours = new CanOverlayContact[2];
				CanVID[] newVidNeighbours = new CanVID[3];
				newVidNeighbours=allNewVidNeighbours.get(i);
				
				if(newContacts.get(i).getArea().getVid().toString().equals(newVidNeighbours[0].toString())){
					//Finde Contact zu den VID Nachbarn
					for(int j=0;j<newContacts.size();j++){
						if(newContacts.get(j).getArea().getVid().toString().equals(newVidNeighbours[1].toString()))
							sendVidNeighbours[0]=newContacts.get(j);
						if(newContacts.get(j).getArea().getVid().toString().equals(newVidNeighbours[2].toString()))
							sendVidNeighbours[1]=newContacts.get(j);
					}
					if(node.getVIDNeighbours()[0].getArea().getVid().toString().equals(newVidNeighbours[1].toString()))
						sendVidNeighbours[0]=node.getVIDNeighbours()[0].clone();
					else if(node.getVIDNeighbours()[0].getArea().getVid().toString().equals(newVidNeighbours[2].toString()))
						sendVidNeighbours[1]=node.getVIDNeighbours()[0].clone();
					else if(node.getVIDNeighbours()[1].getArea().getVid().toString().equals(newVidNeighbours[1].toString()))
						sendVidNeighbours[0]=node.getVIDNeighbours()[1].clone();
					else if(node.getVIDNeighbours()[1].getArea().getVid().toString().equals(newVidNeighbours[2].toString()))
						sendVidNeighbours[1]=node.getVIDNeighbours()[1].clone();
				}
				
				
				if(!node.getCanOverlayID().toString().equals(newContacts.get(i).getOverlayID().toString()) ){
					JoinOverloadMsg joinOverloadMsg = new JoinOverloadMsg(node.getCanOverlayID(), 
							newContacts.get(i).getOverlayID(),
							newNeighbour,newContacts.get(i).getArea(), 
							sendVidNeighbours);
					node.getTransLayer().send(joinOverloadMsg, 
							newContacts.get(i).getTransInfo(), node.getPort(), TransProtocol.TCP);
					
					CanOverlayContact[] sendVID1= {null, newContacts.get(i).clone()};
					CanOverlayContact[] sendVID2= {newContacts.get(i).clone(), null};
					NewVIDNeighbourMsg newVIDNeighboursMsg1 = new NewVIDNeighbourMsg(
							node.getLocalContact().getOverlayID(), 
							sendVidNeighbours[0].getOverlayID(), sendVID1);
					
					node.getTransLayer().send(newVIDNeighboursMsg1, sendVidNeighbours[0].getTransInfo(), 
							node.getPort(), TransProtocol.TCP);
					
					NewVIDNeighbourMsg newVIDNeighboursMsg2 = new NewVIDNeighbourMsg(node.getLocalContact().getOverlayID(), 
							sendVidNeighbours[1].getOverlayID(), sendVID2);
					node.getTransLayer().send(newVIDNeighboursMsg2, sendVidNeighbours[1].getTransInfo(), 
							node.getPort(), TransProtocol.TCP);
				}
				else{
					
					node.setArea(newContacts.get(i).getArea());
					for(int j=0;j<newNeighbour.size();j++){
						if(!node.getLocalContact().getArea().commonCorner((newNeighbour.get(j)).getArea()))
							newNeighbour.remove(j);
					}
					try{
						log.debug(Simulator.getSimulatedRealtime() + " new created node: " + 
								node.getLocalContact().getOverlayID().toString());
						for(int x=0; x<node.getNeighbours().size();x++)
							log.debug(node.getNeighbours().get(x).getOverlayID().toString());
					}catch(Exception e){ //just for log
						
					}
					
					node.setNeighbours(newNeighbour);
					node.setVIDNeigbours(sendVidNeighbours);
					
					log.debug("Number of Neighbours: " + node.getNeighbours().size());
					for(int x=0; x<node.getNeighbours().size();x++)
						log.debug(node.getNeighbours().get(x).getOverlayID().toString());
					log.debug(Simulator.getSimulatedRealtime() 
							+ "area: " + node.getLocalContact().getArea().toString() + " VID: " 
							+ node.getLocalContact().getArea().getVid().toString() + " id: "
							+ node.getLocalContact().getOverlayID().toString()
							+ " " + node.getVIDNeighbours()[0].getArea().getVid().toString()
							+ " " + node.getVIDNeighbours()[1].getArea().getVid().toString());
					node.getBootstrap().update(node);
				}
			}
			
			int firstNotAllreadyNeighbour=0;
			while( allNewVidNeighbours.get(firstNotAllreadyNeighbour)[0].toString().equals(oldVIDNeighbours[0].getArea().getVid().toString())
						|| allNewVidNeighbours.get(firstNotAllreadyNeighbour)[0].toString().equals(oldVIDNeighbours[1].getArea().getVid().toString()) )
				firstNotAllreadyNeighbour++;
			
			CanVID smallestVID = allNewVidNeighbours.get(firstNotAllreadyNeighbour)[0];
			CanVID biggestVID = allNewVidNeighbours.get(firstNotAllreadyNeighbour)[0];
			
			for(int i=0; i<allNewVidNeighbours.size();i++){
				if(allNewVidNeighbours.get(i)[0].lower(smallestVID)
						&& !allNewVidNeighbours.get(i)[0].toString().equals(oldVIDNeighbours[0].getArea().getVid().toString())
						&& !allNewVidNeighbours.get(i)[0].toString().equals(oldVIDNeighbours[1].getArea().getVid().toString()) )
					smallestVID=allNewVidNeighbours.get(i)[0];
				if(allNewVidNeighbours.get(i)[0].higher(biggestVID)
						&& !allNewVidNeighbours.get(i)[0].toString().equals(oldVIDNeighbours[0].getArea().getVid().toString())
						&& !allNewVidNeighbours.get(i)[0].toString().equals(oldVIDNeighbours[1].getArea().getVid().toString()) )
					biggestVID=allNewVidNeighbours.get(i)[0];
			}
			
			CanOverlayContact smallestVIDContact = null, biggestVIDContact=null;
			
			for(int i=0; i< newContacts.size();i++){
				if(newContacts.get(i).getArea().getVid().toString().equals(smallestVID.toString()))
					smallestVIDContact=newContacts.get(i).clone();
				if(newContacts.get(i).getArea().getVid().toString().equals(biggestVID.toString()))
					biggestVIDContact=newContacts.get(i).clone();
			}
			
			CanOverlayContact[] sendVID1= {null, smallestVIDContact.clone()};
			CanOverlayContact[] sendVID2= {biggestVIDContact.clone(), null};
			NewVIDNeighbourMsg newVIDNeighboursMsg1 = new NewVIDNeighbourMsg(node.getLocalContact().getOverlayID(), 
					oldVIDNeighbours[0].getOverlayID(), sendVID1);
			
			node.getTransLayer().send(newVIDNeighboursMsg1, oldVIDNeighbours[0].getTransInfo(), 
					node.getPort(), TransProtocol.TCP);
			
			if(!oldVIDNeighbours[1].getOverlayID().toString().equals(oldVIDNeighbours[0].getOverlayID().toString())){
				NewVIDNeighbourMsg newVIDNeighboursMsg2 = new NewVIDNeighbourMsg(node.getLocalContact().getOverlayID(), 
						oldVIDNeighbours[1].getOverlayID(), sendVID2);
				node.getTransLayer().send(newVIDNeighboursMsg2, oldVIDNeighbours[1].getTransInfo(), 
						node.getPort(), TransProtocol.TCP);
			}
			
		}
	}
}
