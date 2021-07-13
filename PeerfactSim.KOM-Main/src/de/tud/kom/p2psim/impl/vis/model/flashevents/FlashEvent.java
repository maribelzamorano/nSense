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


package de.tud.kom.p2psim.impl.vis.model.flashevents;

import de.tud.kom.p2psim.impl.vis.model.ModelIterator;
import de.tud.kom.p2psim.impl.vis.model.TypeObject;

/**
 * Ereignis, das keine Dauer hat und daher unendlich kurz ist. Es wird
 * von der Visualisierung speziell gezeichnet. Beispiele:
 * Message-Versendungen ohne festgelegte Zeit.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * @version 3.0, 21.10.2008
 *
 */
public interface FlashEvent extends TypeObject {
	
	/**
	 * Iterator-Aufruf. Ruft die zum Objekt passende Visitorfunktion auf.
	 * @param it
	 */
	public void iterate(ModelIterator it);
	
	/**
	 * Ist von besonderer Bedeutung bei FlashEvents. Es werden nie zwei Events
	 * in einem Frame gezeichnet, die sich ähneln, damit das Bild nicht "zugemüllt"
	 * wird von vielen FlashEvents.
	 * @param e
	 * @return ob GLeichheit herrscht
	 */
	public boolean equals(Object o);
	
	/**
	 * konsistent zu equals(Object).
	 * Siehe http://www.geocities.com/technofundo/tech/java/equalhash.html
	 * @return
	 */
	public int hashCode();
}
