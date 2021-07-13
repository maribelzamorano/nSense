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


package de.tud.kom.p2psim.impl.overlay.gnutella04.filesharing;

import java.util.HashMap;
import java.util.Map;

import de.tud.kom.p2psim.impl.util.stat.distributions.ZipfDistribution;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class FileFactory {

	private ZipfDistribution distribution;

	private static FileFactory singletonInstance;

	private Map<Integer, FilesharingDocument> files = new HashMap<Integer, FilesharingDocument>();

	public static FileFactory getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new FileFactory();
		}
		return singletonInstance;
	}

	public FileFactory() {
		distribution = new ZipfDistribution(10000, 1.0);
	}

	public FilesharingDocument getFile(Integer rank) {
		if (!files.containsKey(rank)) {
			files.put(rank, new FilesharingDocument(rank));
		}
		return files.get(rank);
	}

	public FilesharingDocument getFile() {
		Integer rank = distribution.returnRank();
		return getFile(rank);
	}

	public FilesharingKey getKey(Integer rank) {
		return (FilesharingKey) this.getFile(rank).getKey();
	}

	public FilesharingKey getKey() {
		return (FilesharingKey) this.getFile().getKey();
	}
}
