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


package de.tud.kom.p2psim.impl.vis.model.events;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.tud.kom.p2psim.impl.vis.model.overlay.AttributeObject;

/**
 * Ändert Attribute in einem Objekt
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 27.10.2008
 *
 */
public class AttributesChanged extends Event implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6124586429312315638L;

	/**
	 * Attribute die geändert werden sollen, und ihre neuen Werte
	 */
	private Map<String, Serializable> newAttributes;
	
	/**
	 * Schlüssel und Werte der geänderten Attribute,
	 * wie sie unmittelbar vor dem Aufruf des Ereignisses war.
	 */
	private Map<String, Serializable> pastAttributes;

	private AttributeObject obj;

	public AttributesChanged(AttributeObject obj, Map<String, Serializable> changedAttributes) {
		this.newAttributes=changedAttributes;
		this.obj = obj;
	}
	
	@Override
	public void makeHappen() {
		Map<String, Serializable> objAttrs = obj.getAttributes();
		if (objAttrs == null) {
			System.out.println("Warning: Trying to change attributes from an object " +
					"that does not exist or has no attributes.");
			return;
		}
		pastAttributes = new HashMap<String, Serializable>();
		for (String key : newAttributes.keySet()) {
			pastAttributes.put(key, objAttrs.get(key));	//Speichert den alten Wert, um ihn ggf. zu "undo"en.
		}
		objAttrs.putAll(newAttributes);
		
	}

	@Override
	public void undoMakeHappen() {
		
		for (String key: pastAttributes.keySet()) {
			Serializable value = pastAttributes.get(key);
			if (value != null) pastAttributes.put(key, value);
			else pastAttributes.remove(key);
		}
		
		obj.getAttributes().putAll(pastAttributes);
	}
	
	public String toString() {
		return "AttributesChanged: " + newAttributes.toString();
	}
}
