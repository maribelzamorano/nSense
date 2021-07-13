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
import de.tud.kom.p2psim.impl.common.AbstractOperation;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanArea;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanNode;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanOverlayContact;
import de.tud.kom.p2psim.impl.overlay.dht.can.CanVID;
import de.tud.kom.p2psim.impl.overlay.dht.can.DataID;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.LeaveLeftMsg;
import de.tud.kom.p2psim.impl.overlay.dht.can.messages.NewNeighbourMsg;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * If a peer wants to leave it starts the LeaveOperation, which starts
 * after a certain time the LeaveReorganizeOperation. This Operation is
 * responsible for the reorganization of the structure. So it has to 
 * rearrange the areas the VIDs, the neighbours and the VID neighbours.
 * To do so it uses the collected data. From the ReorganizeReplyMessages
 * it get these data a saves them in lists. At the end gets every peer a 
 * messages with the new data. 
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010 
 *
 */
public class LeaveReorganizeOperation extends AbstractOperation<CanNode, Object> {
	private final static Logger log = SimLogger.getLogger(CanNode.class);
	
	private CanNode master;
	private CanOverlayContact leavingNode;
	
	private List<CanOverlayContact> neighbours;
	private List<CanArea> area;
	private List<CanOverlayContact> replyContacts;
	private List<CanOverlayContact[]> leavingReplyVIDNeighbours;
	
	/**
	 * starts the reorganization for the leaving peer
	 * 
	 * @param component
	 * 		leaving peer
	 */
	public LeaveReorganizeOperation(CanNode component) {
		super(component);
		this.master=component;
		this.leavingNode=master.getLocalContact();
		
		neighbours=new LinkedList<CanOverlayContact>();
		area=new LinkedList<CanArea>();
		replyContacts = new LinkedList<CanOverlayContact>();
		leavingReplyVIDNeighbours = new LinkedList<CanOverlayContact[]>();
	}

	public void execute() {
		CanArea newArea = new CanArea();
		area.add(master.getLocalContact().getArea());
		neighbours = master.getLeavingNeighbours();
		area.addAll(master.getLeavingArea());
		replyContacts = master.getLeavingReplyContacts();
		leavingReplyVIDNeighbours = master.getLeavingReplyVIDNeighbours();
		List<Object[]> allHashs = new LinkedList<Object[]>();
		
		if(master.getStoredHashs()!=null)
			allHashs.addAll(master.getStoredHashs());
		if(master.getLeavingHash()!=null)
			allHashs.addAll(master.getLeavingHash());
		
		List<CanOverlayContact> findVID = new LinkedList<CanOverlayContact>();
		
		CanVID oldVID = new CanVID (master.getLocalContact().getArea().getVid().getVIDList());
		
		
		for(int i=0;i<area.size();i++){
			if (area.get(i).toString().equals(master.getLocalContact().getArea().toString()))
				area.remove(i);
		}
		for(int i=0;i<neighbours.size();i++){
			if (neighbours.get(i).getOverlayID().toString().equals(master.getLocalContact().getOverlayID().toString()))
				neighbours.remove(i);
		}
		
		for(int i=0;i<replyContacts.size();i++){
			if (replyContacts.get(i).getOverlayID().toString().equals(master.getLocalContact().getOverlayID().toString()))
				replyContacts.remove(i);
		}
		
		for(int i=0;i<neighbours.size();i++){
			for(int j=0;j<neighbours.size();j++){
				if (i!=j && neighbours.get(i).getOverlayID().toString().equals(neighbours.get(j).getOverlayID().toString())){
					neighbours.remove(j);
					j=0;
				}
			}
		}
		
		if(allHashs!=null){
			for(int i=0;i<allHashs.size();i++){
				if(((CanOverlayContact)allHashs.get(i)[1]).getOverlayID().toString().equals(leavingNode.getOverlayID().toString()))
						allHashs.remove(i);
			}
		}
		
		log.debug(Simulator.getSimulatedRealtime() 
				+ " Leaving node: " + leavingNode.getOverlayID().toString()
				+ " " + leavingNode.getArea().toString()
				+ " own id " + master.getLocalContact().getOverlayID().toString()
				+ " areaList:");
		
		area.add(leavingNode.getArea());
		
		for(int j=0;j<area.size();j++){
			for (int i=0;i<area.size();i++){
				CanArea jArea = area.get(j);
				CanArea iArea = area.get(i);
				if (j!=i && (jArea.getVid().numberCommon(iArea.getVid()) == jArea.getVid().getVIDList().size()-1)){
					newArea = new CanArea(0,0,0,0);
					
					if(jArea.getArea()[0] == iArea.getArea()[0]
							&& jArea.getArea()[1] == iArea.getArea()[1]
							&& (jArea.getArea()[2] == iArea.getArea()[3])){
						newArea.setX0(jArea.getArea()[0]);
						newArea.setX1(jArea.getArea()[1]);
						newArea.setY0(iArea.getArea()[2]);
						newArea.setY1(jArea.getArea()[3]);
						CanVID VIDToSave = new CanVID(jArea.getVid().listCommon(iArea.getVid()));
						newArea.setVid(VIDToSave);
						area.remove(jArea);
						area.remove(iArea);
						area.add(newArea);
						i=0; j=0; break;
					}
					else if(jArea.getArea()[0] == iArea.getArea()[0]
							&& jArea.getArea()[1] == iArea.getArea()[1] 
							&& (jArea.getArea()[3] == iArea.getArea()[2])){
						newArea.setX0(jArea.getArea()[0]);
						newArea.setX1(jArea.getArea()[1]);
						newArea.setY0(jArea.getArea()[2]);
						newArea.setY1(iArea.getArea()[3]);
						CanVID VIDToSave = new CanVID(jArea.getVid().listCommon(iArea.getVid()));
						newArea.setVid(VIDToSave);
						area.remove(iArea);
						area.remove(jArea);
						area.add(newArea);
						i=0; j=0; break;
					}
					else if(jArea.getArea()[2] == iArea.getArea()[2]
							&& jArea.getArea()[3] == iArea.getArea()[3]  
							&& (jArea.getArea()[0] == iArea.getArea()[1])){
						newArea.setX0(iArea.getArea()[0]);
						newArea.setX1(jArea.getArea()[1]);
						newArea.setY0(jArea.getArea()[2]);
						newArea.setY1(jArea.getArea()[3]);
						CanVID VIDToSave = new CanVID(jArea.getVid().listCommon(iArea.getVid()));
						newArea.setVid(VIDToSave);
						area.remove(iArea);
						area.remove(jArea);
						area.add(newArea);
						i=0; j=0; break;
					}
					else if(jArea.getArea()[2] == iArea.getArea()[2]
							&& jArea.getArea()[3] == iArea.getArea()[3] 
							&& (jArea.getArea()[1] == iArea.getArea()[0])){
						newArea.setX0(jArea.getArea()[0]);
						newArea.setX1(iArea.getArea()[1]);
						newArea.setY0(jArea.getArea()[2]);
						newArea.setY1(jArea.getArea()[3]);
						CanVID VIDToSave = new CanVID(jArea.getVid().listCommon(iArea.getVid()));
						newArea.setVid(VIDToSave);
						area.remove(iArea);
						area.remove(jArea);
						area.add(newArea);
						i=0; j=0; break;
					}
				}
			}
		}
		
		log.debug("newArea " + newArea.toString());
		
		neighbours.addAll(master.getNeighbours()); //eigene nachbarn mit den nachbarn des leave knotens zusammenfassen 
		for(int i=0; i<neighbours.size();i++){	//alle nachbarn des leave knotens entfernen
			if(neighbours.get(i).getOverlayID().toString().equals(master.getLocalContact().getOverlayID().toString()))	//master entfernen)
				neighbours.remove(i);
			
			for (int j=0;j<replyContacts.size();j++){
				if(neighbours.get(i).getOverlayID().toString().equals(replyContacts.get(j).getOverlayID().toString())){
					neighbours.remove(i);
					i=0;
				}
			}
		}
		
		List<String> vidToSave = master.getLocalContact().getArea().getVid().listCommon(replyContacts.get(0).getArea().getVid());
		
		master.setNeighbours(neighbours); //neue Nachbarliste setzten
		master.setArea(newArea);		//muss eigentlich nicht gesetzt werden da es versendet wird
		
		master.getLocalContact().getArea().setVID(vidToSave);
		
		log.debug("newVID " + master.getLocalContact().getArea().getVid().toString());
		
		log.debug("NewNeighbours: ");
		for (int x=0; x<master.getNeighbours().size();x++)
			log.debug(master.getNeighbours().get(x).getOverlayID().toString());
		
		
		if(replyContacts.size()!=1){
			CanOverlayContact masterToSend = replyContacts.get(0); //Daten werden in eigenem Knoten gespeichert und später gesendet
			replyContacts.remove(0);
			
			leavingReplyVIDNeighbours.add(master.getVIDNeighbours());
			
			for (int x=0; x<leavingReplyVIDNeighbours.size();x++)
				log.debug("receveived vid: " + leavingReplyVIDNeighbours.get(x)[0].getArea().getVid().toString()
						+" " + leavingReplyVIDNeighbours.get(x)[1].getArea().getVid().toString());
			
			for (int x=0; x<replyContacts.size();x++)
				log.debug((replyContacts.get(x)).getArea().getVid().toString());
			
			//Set new VIDNEighbours depending on the VID Neighbours of the reply Contacts, the leaving and the own vid-neighbours
			
			CanOverlayContact[] vidNeighboursToSave = new CanOverlayContact[2];
			for(int i=0;i<leavingReplyVIDNeighbours.size();i++){
				
				if(vidNeighboursToSave[0]==null
						|| leavingReplyVIDNeighbours.get(i)[0].getArea().getVid().higher(vidNeighboursToSave[0].getArea().getVid())){
					boolean readyToSave=true;
					for(int j=0;j<replyContacts.size();j++){
						if(replyContacts.get(j).getArea().getVid().toString().equals(leavingReplyVIDNeighbours.get(i)[0].getArea().getVid().toString())
								|| masterToSend.getArea().getVid().toString().equals(leavingReplyVIDNeighbours.get(i)[0].getArea().getVid().toString())
								||oldVID.toString().equals(leavingReplyVIDNeighbours.get(i)[0].getArea().getVid().toString()))
							readyToSave=false;
					}
					if(readyToSave)	{
						vidNeighboursToSave[0]= leavingReplyVIDNeighbours.get(i)[0];
					}
				}
					
				if(vidNeighboursToSave[1]==null
						||leavingReplyVIDNeighbours.get(i)[1].getArea().getVid().lower(vidNeighboursToSave[1].getArea().getVid())){
					boolean readyToSave=true;
					for(int j=0;j<replyContacts.size();j++){
						if(replyContacts.get(j).getArea().getVid().toString().equals(leavingReplyVIDNeighbours.get(i)[1].getArea().getVid().toString())
								|| masterToSend.getArea().getVid().toString().equals(leavingReplyVIDNeighbours.get(i)[1].getArea().getVid().toString())
								|| oldVID.toString().equals(leavingReplyVIDNeighbours.get(i)[1].getArea().getVid().toString()))
							readyToSave=false;
					}
					if(readyToSave){	
						vidNeighboursToSave[1]= leavingReplyVIDNeighbours.get(i)[1];
					}
				}
			}
			if(master.getLocalContact().getArea().getVid().toString().equals("0")){
				vidNeighboursToSave[0]= master.getLocalContact();
				vidNeighboursToSave[1]= master.getLocalContact();
			}
				
			master.setVIDNeigbours(vidNeighboursToSave);
			findVID.add(vidNeighboursToSave[0]);
			findVID.add(vidNeighboursToSave[1]);
			
			//ein neues Objekt overloadjoin wird erzeugt
			OverloadJoin overloadJoin = new OverloadJoin(master, replyContacts);
			List<CanOverlayContact> newContacts = overloadJoin.getNewMasters();
			
			
								
			//neue Nachbarnliste erstellen
			List<CanVID[]> allNewVidNeighbours = new LinkedList<CanVID[]>();
			allNewVidNeighbours.addAll(overloadJoin.newVidNeighbours(master.getVIDNeighbours()));
		
			for(int i=0;i<newContacts.size();i++){
				List<CanOverlayContact> newNeighbour = new LinkedList<CanOverlayContact>();
				newNeighbour.addAll(newContacts);
				newNeighbour.addAll(master.getNeighbours());
				
				//die neuen nachbarn zu jedem neuen Knoten werden ausgelessen und danach bekommt jeder knoten eine nachricht mit der neuen fläche und den nachbarn
				newNeighbour=overloadJoin.getNeighboursToNewArea(newContacts.get(i).getArea().getArea(), newNeighbour);
				
				for(int j=0;j<newNeighbour.size();j++){
					if(newNeighbour.get(j).getOverlayID().toString().equals(master.getLocalContact().getOverlayID().toString())){
						newNeighbour.remove(j);
						newNeighbour.add(masterToSend);
					}
				}
				
				//vid Nachbarn einteilen
				CanOverlayContact[] sendVidNeighbours = new CanOverlayContact[2];
				CanVID[] newVidNeighbours = new CanVID[3];
				newVidNeighbours=allNewVidNeighbours.get(i);
				
				
				findVID.addAll(newContacts);
				masterToSend.getArea().setVID(master.getLocalContact().getArea().getVid().getVIDList());
				
				for(int j=0;j<findVID.size();j++){
					if(findVID.get(j).getArea().getVid().toString().equals(newVidNeighbours[0].toString())){
						//Finde Contact zu den VID Nachbarn
						for(int h=0;h<findVID.size();h++){
							if(findVID.get(h).getArea().getVid().toString().equals(newVidNeighbours[1].toString()))
								sendVidNeighbours[0]=findVID.get(h);
							
							if(findVID.get(h).getArea().getVid().toString().equals(newVidNeighbours[2].toString()))
								sendVidNeighbours[1]=findVID.get(h);
						}
					}
				}
				
				log.debug("ready to send: " + newContacts.get(i).getOverlayID().toString()
						+ " " + newContacts.get(i).getArea().toString()
						+ " " + newContacts.get(i).getArea().getVid().toString());
				
				if(!newContacts.get(i).getOverlayID().toString().equals(master.getLocalContact().getOverlayID().toString())){
					CanArea areaToSend = new CanArea(newContacts.get(i).getArea().getArea(), newContacts.get(i).getArea().getVid().getVIDList());
					CanOverlayContact contactToSend = new CanOverlayContact(newContacts.get(i).getOverlayID(), 
							newContacts.get(i).getTransInfo(), areaToSend, true);
					
					List<Object[]> hashToSend = new LinkedList<Object[]>();
					if(allHashs!=null){
						for(int j=0;j<allHashs.size();j++){
							log.debug("allHashs " + ((DataID)allHashs.get(j)[0]).toString());
							
							if(((DataID)allHashs.get(j)[0]).includedInArea(areaToSend))
								hashToSend.add(allHashs.get(j));
						}
					}
					
					LeaveLeftMsg leaveLeft = new LeaveLeftMsg(master.getLocalContact().getOverlayID(), 
							newContacts.get(i).getOverlayID(), contactToSend, 
							newNeighbour, sendVidNeighbours, 
							newContacts.get(i).getArea().getVid(), hashToSend);	
					master.getTransLayer().send(leaveLeft, newContacts.get(i).getTransInfo(), master.getPort(), TransProtocol.TCP);
					
					
				}
				else{
					CanArea areaToSend = new CanArea(newContacts.get(i).getArea().getArea(), newContacts.get(i).getArea().getVid().getVIDList());
					master.setArea(areaToSend);
					newNeighbour = overloadJoin.getNeighboursToNewArea(master.getLocalContact().getArea().getArea(), newNeighbour);
					master.setNeighbours(newNeighbour);
					
					master.removeVidNeighboursOfNeighbours(newNeighbour);
					master.removeNeighboursOfNeighbours(newNeighbour);
					
					master.setVIDNeigbours(sendVidNeighbours);
				}
				
				List<CanOverlayContact> sendNewNeighboursTo = new LinkedList<CanOverlayContact>();
				sendNewNeighboursTo.addAll(newNeighbour);
				
				for(int j=0;j<sendNewNeighboursTo.size();j++){
					for(int h=0;h<replyContacts.size();h++){
						if(sendNewNeighboursTo.get(j).getOverlayID().toString().equals(replyContacts.get(h).getOverlayID().toString())
								|| sendNewNeighboursTo.get(j).getOverlayID().toString().equals(master.getLocalContact().getOverlayID().toString())
								|| sendNewNeighboursTo.get(j).getOverlayID().toString().equals(masterToSend.getOverlayID().toString())){
							sendNewNeighboursTo.remove(j);
							break;
						}
					}
				}
				
				List<CanOverlayContact> newNeighboursToSendList = new LinkedList<CanOverlayContact>();
				
				for(int j=0;j<replyContacts.size();j++){
					if(replyContacts.get(j).getOverlayID().toString().equals(master.getLocalContact().getOverlayID().toString())){
						masterToSend.setArea(master.getLocalContact().getArea());
						newNeighboursToSendList.add(masterToSend);
					}
					else
						newNeighboursToSendList.add(replyContacts.get(j));
				}
				
				List<CanOverlayContact> deleteNeighbours = new LinkedList<CanOverlayContact>();
				deleteNeighbours.addAll(newNeighboursToSendList);
				deleteNeighbours.add(master.getLocalContact().clone());
				
				for(int j=0;j<sendNewNeighboursTo.size();j++){
					NewNeighbourMsg newNeighboursMsg = new NewNeighbourMsg(master.getCanOverlayID(), sendNewNeighboursTo.get(j).getOverlayID(),
							master.getLocalContact().clone(), deleteNeighbours, newNeighboursToSendList);
					master.getTransLayer().send(newNeighboursMsg, sendNewNeighboursTo.get(j).getTransInfo(), master.getPort(), TransProtocol.TCP);
				}
			}
			
			
			log.debug("masterNode: " + master.getLocalContact().getOverlayID().toString()
					+ " " + master.getLocalContact().getArea().toString()
					+ " " + master.getLocalContact().getArea().getVid().toString()
					+ " vid neighbours " + master.getVIDNeighbours()[0].getArea().getVid().toString()
					+ " " + master.getVIDNeighbours()[1].getArea().getVid().toString());
			for(int x=0; x<master.getNeighbours().size();x++)
				log.debug("neighbour " + master.getNeighbours().get(x));
			
			List<Object[]> hashToSend = new LinkedList<Object[]>();
			if(allHashs!=null){
				for(int j=0;j<allHashs.size();j++){
					log.debug("allHashs " + ((DataID)allHashs.get(j)[0]).toString());
					
					if(((DataID)allHashs.get(j)[0]).includedInArea(master.getLocalContact().getArea()))
						hashToSend.add(allHashs.get(j));
				}
			}
			
			LeaveLeftMsg leaveLeft = new LeaveLeftMsg(master.getLocalContact().getOverlayID(), masterToSend.getOverlayID(),
					master.getLocalContact().clone(), master.getNeighbours(), master.getVIDNeighbours(), 
					master.getLocalContact().getArea().getVid(), hashToSend);	
			master.getTransLayer().send(leaveLeft, masterToSend.getTransInfo(), master.getPort(), TransProtocol.TCP);
			
		}
		else{
			CanOverlayContact[] vidNeighboursToSend = {null,null};
			if(oldVID.toString().equals(master.getVIDNeighbours()[0].getArea().getVid().toString()) 
					|| oldVID.toString().equals(leavingReplyVIDNeighbours.get(0)[1].getArea().getVid().toString())){
				vidNeighboursToSend[0] = leavingReplyVIDNeighbours.get(0)[0];
				vidNeighboursToSend[1] = master.getVIDNeighbours()[1];
			}
			else{
				vidNeighboursToSend[0] = master.getVIDNeighbours()[0];
				vidNeighboursToSend[1] = leavingReplyVIDNeighbours.get(0)[1];
				
			}
			List<Object[]> hashToSend = new LinkedList<Object[]>();
			if(allHashs!=null){
				for(int j=0;j<allHashs.size();j++){
					if(((DataID)allHashs.get(j)[0]).includedInArea(master.getLocalContact().getArea()))
						hashToSend.add(allHashs.get(j));
					
				}
			}
			
			LeaveLeftMsg leaveLeft = new LeaveLeftMsg(master.getLocalContact().getOverlayID(), replyContacts.get(0).getOverlayID(),
				master.getLocalContact().clone(), master.getNeighbours(), vidNeighboursToSend, 
				master.getLocalContact().getArea().getVid(), hashToSend);	
			master.getTransLayer().send(leaveLeft, replyContacts.get(0).getTransInfo(), master.getPort(), TransProtocol.TCP);
			
			CanOverlayContact masterToSend = new CanOverlayContact(replyContacts.get(0).getOverlayID(),
					replyContacts.get(0).getTransInfo(), master.getLocalContact().getArea(), true);
			
			List<CanOverlayContact> deleteNeighbours = new LinkedList<CanOverlayContact>();
			deleteNeighbours.add(master.getLocalContact().clone());
			deleteNeighbours.add(replyContacts.get(0));
			
			CanOverlayContact newContactToSend = new CanOverlayContact(replyContacts.get(0).getOverlayID(),
					replyContacts.get(0).getTransInfo(), master.getLocalContact().getArea(), true);
			List<CanOverlayContact> newNeighbours = new LinkedList<CanOverlayContact>();
			newNeighbours.add(newContactToSend);
			
			for (int i=0;i<master.getNeighbours().size();i++){
				NewNeighbourMsg newNeighboursMsg = new NewNeighbourMsg(master.getCanOverlayID(), 
						master.getNeighbours().get(i).getOverlayID(),
						master.getLocalContact().clone(), deleteNeighbours, newNeighbours);
				master.getTransLayer().send(newNeighboursMsg, 
						master.getNeighbours().get(i).getTransInfo(), 
						master.getPort(), TransProtocol.TCP);
			}
		}
		
		log.debug("Changed node: " + master.getLocalContact().getOverlayID().toString()
				+ " area " + master.getLocalContact().getArea().toString()
				+ " alive " + master.getLocalContact().isAlive());
		
		master.setAlive(false);
		master.setPeerStatus(PeerStatus.ABSENT);
		master.getBootstrap().unregisterNode(leavingNode);
		master.emptyLeavingNeighbours();
		master.emptyLeavingReplyContacts();
		master.emptyLeavingReplyVIDNeighbours();
		master.emptyLeavingArea();
	}

	@Override
	public Object getResult() {
		return null;
	}

	

}
