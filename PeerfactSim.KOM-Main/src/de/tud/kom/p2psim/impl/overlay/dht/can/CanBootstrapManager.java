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


package de.tud.kom.p2psim.impl.overlay.dht.can;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.transport.TransInfo;
import de.tud.kom.p2psim.impl.overlay.BootstrapManager;
import de.tud.kom.p2psim.impl.overlay.AbstractOverlayNode.PeerStatus;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * CanBootstrpManager know about every present peer
 * 
 * @author Bjoern Dollak <peerfact@kom.tu-darmstadt.de>
 * @version February 2010 
 *
 */
public class CanBootstrapManager implements BootstrapManager {
	/*
	 * all active nodes
	 */

	private static Logger log = SimLogger.getLogger(CanNode.class);
	private List<OverlayNode> activeNodes;
	
	
	public CanBootstrapManager(){
		this.activeNodes = new LinkedList<OverlayNode>();
		
	}
	
	public List<TransInfo> getBootstrapInfo() {
		List<TransInfo> list = new LinkedList<TransInfo>();
		for (OverlayNode node : this.activeNodes) {
			list.add(((CanNode)node).getLocalContact().getTransInfo());
		}
		return list;
	}
	
	

	public void registerNode(OverlayNode node) {
		this.activeNodes.add(node);

	}

	public void unregisterNode(OverlayNode node) {
		this.activeNodes.remove(node);

	}
	
	public void unregisterNode(CanOverlayContact node){
		log.debug("unregister Node: " + node.getOverlayID().toString());
		for (int i=0;i<activeNodes.size();i++){
			if(activeNodes.get(i).getOverlayID().toString().equals(node.getOverlayID().toString()))
					activeNodes.remove(i);
		}
	}

	/**
	 * give one node for the join operation.
	 * Either the CAN is uniform distributed (CanConfig.distribution==0)
	 * or the nodes are picked randomly. Randomly gives problems in scalling
	 * the CAN
	 * 
	 * @return
	 * 		CanOverlayContact
	 */
	public CanNode pick() {
		if (activeNodes.isEmpty())		
			return null;
		
		CanNode pickedNode =null;
		if(CanConfig.distribution==0){
			pickedNode= (CanNode)activeNodes.get(0);
			int i=0;
			while(pickedNode.getPeerStatus()!=PeerStatus.PRESENT){
				pickedNode = (CanNode)activeNodes.get(i);
				i++;
			}
			
			for(i=0; i<activeNodes.size(); i++){
				int squarePicked = (pickedNode.getLocalContact().getArea().getArea()[1]-
						pickedNode.getLocalContact().getArea().getArea()[0]) *
						(pickedNode.getLocalContact().getArea().getArea()[3]-
						pickedNode.getLocalContact().getArea().getArea()[2]) ;
				
				CanNode newTry = (CanNode)activeNodes.get(i);
				int newTrySquare = (newTry.getLocalContact().getArea().getArea()[1]-
						newTry.getLocalContact().getArea().getArea()[0]) *
						(newTry.getLocalContact().getArea().getArea()[3]-
						newTry.getLocalContact().getArea().getArea()[2]) ;
				if( (newTrySquare > squarePicked) 
						&& newTry.getPeerStatus()== PeerStatus.PRESENT )
					pickedNode = newTry;
			}
		}
		else{
			pickedNode =(CanNode)activeNodes.get(Simulator.getRandom().nextInt(activeNodes.size()));
			while( ((pickedNode.getLocalContact().getArea().getArea()[1]-pickedNode.getLocalContact().getArea().getArea()[1]) == 1
					|| (pickedNode.getLocalContact().getArea().getArea()[3]-pickedNode.getLocalContact().getArea().getArea()[2]) == 1) 
					|| pickedNode.getPeerStatus()!=PeerStatus.PRESENT)
				pickedNode = (CanNode)activeNodes.get(Simulator.getRandom().nextInt(activeNodes.size()));
		}
		
		return pickedNode;
	}
	
	public String toString(){
		String output = new String();
		for (int i=0; i<activeNodes.size();i++){
			output = output + " " + activeNodes.get(i).getOverlayID().toString();
			
		}
		return output;
	}
	
	public List<CanNode> getBootstrap(){
		List<CanNode> output = new LinkedList<CanNode>();
		for (int i=0; i<activeNodes.size();i++)
			output.add((CanNode)activeNodes.get(i));
		return output;
	}
	
	public void update(OverlayNode node){
		for (int i=0;i<this.activeNodes.size();i++){
			if( activeNodes.get(i).getOverlayID().toString().equals(node.getOverlayID().toString()) )
				activeNodes.remove(i);
		}
		activeNodes.add(node);
		for (int i=0;i<this.activeNodes.size();i++){
			if( activeNodes.get(i).getOverlayID().toString().equals(node.getOverlayID().toString()) ){
				((CanNode)activeNodes.get(i)).setArea(((CanNode)activeNodes.get(i)).getLocalContact().getArea());
				((CanNode)activeNodes.get(i)).getLocalContact().getArea().setVid(((CanNode)activeNodes.get(i)).getLocalContact().getArea().getVid());
			}
		}
	}
	
	public void  visualize(){
		for (int j=0;j<activeNodes.size();j++){
			log.debug("Own ID: " + ((CanNode)activeNodes.get(j)).getLocalContact().getOverlayID().toString()
					+ " own VID " + ((CanNode)activeNodes.get(j)).getLocalContact().getArea().getVid().toString()
					+ " own area " + ((CanNode)activeNodes.get(j)).getLocalContact().getArea().toString() 
					+ " is allive: " + ((CanNode)activeNodes.get(j)).getLocalContact().isAlive()
					+ " Neighbours: ");
			try{
				for(int i=0; i<((CanNode)activeNodes.get(j)).getNeighbours().size();i++)
					log.debug(((CanNode)activeNodes.get(j)).getNeighbours().get(i).getOverlayID().toString());
					log.debug("VID Neighbours " + ((CanNode)activeNodes.get(j)).getVIDNeighbours()[0].getArea().getVid().toString() 
						+ " " + ((CanNode)activeNodes.get(j)).getVIDNeighbours()[0].getOverlayID().toString()
						+ " " + ((CanNode)activeNodes.get(j)).getVIDNeighbours()[1].getArea().getVid().toString() 
						+ " " + ((CanNode)activeNodes.get(j)).getVIDNeighbours()[1].getOverlayID().toString());
			}catch(Exception e){
				//just in case
			}
		}
	}
}
