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


package de.tud.kom.p2psim.impl.application.KBRApplication.messages;

/**
 * This class holds some static configuration information about the messages
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class MessageConfig {

	/**
	 * The sizes of the messages in bytes
	 * 
	 * @author Julius Rueckert
	 * 
	 */
	public class Sizes {
		static final long AnnounceNewDocumentMessage = 5;

		static final long QueryForDocumentMessage = 5;

		static final long QueryResultMessage = 5;

		static final long RequestDocumentMessage = 5;

		static final long TransferDocumentMessage = 5;
	}

}
