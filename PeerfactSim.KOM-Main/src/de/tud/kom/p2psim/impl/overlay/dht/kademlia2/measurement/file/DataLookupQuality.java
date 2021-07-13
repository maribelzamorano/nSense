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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.measurement.file;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Information about the quality of a data lookup.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public final class DataLookupQuality {

	// # RESULT_CORRECT LATENCY DEPTH=MSGS_SENT ALL_DATA ONLINE_DATA KCN_DATA
	// ALL_CLOSER ONLINE_CLOSER ONLINE_DATA_CLOSER SENDER_KCN PERFECT_CONTACTS
	// OFFLINE_CONTACTS MISSED_CLOSER_CONTACTS
	// true 0.032991 [4=5,] 42 35 20 13 11 11 true - - -
	// false 2.175604 [4=21,] 30 25 20 - - - - 0 2 20
	// true 4.3E-3 [4=5,] 42 35 20 13 11 11 true - - -
	private static final Pattern filePattern = Pattern
			.compile("^(\\w+) (\\d+\\.\\d+(?:E-\\d+)?) \\[((\\d+=\\d+,)*)\\] (\\d+) (\\d+) (\\d+) (\\d+|-) (\\d+|-) (\\d+|-) (\\w+|-) (\\d+|-) (\\d+|-) (\\d+|-) $");

	private static final Pattern item = Pattern.compile("(\\d+)=(\\d+),");

	final boolean successful;

	final double latency;

	final Map<Integer, Integer> sentMessages;

	final int allData, onlineData, kCNData;

	// in successful lookups
	final int allCloser, onlineCloser, onlineDataCloser;

	final boolean senderKCN;

	// in failed lookups
	final int perfectContacts, offlineContacts, missedCloserContacts;

	/**
	 * Constructs a new DataLookupQuality. All arguments are Strings and parsed
	 * internally.
	 * 
	 */
	public DataLookupQuality(final String success, final String latency,
			final Map<String, String> sentMessagesPerCluster,
			final String allData, final String onlineData,
			final String kCNData, final String allCloser,
			final String onlineCloser, final String onlineDataCloser,
			final String senderKCN, final String perfectContacts,
			final String offlineContacts, final String missedCloserContacts) {
		this.successful = Boolean.valueOf(success);
		this.latency = Double.valueOf(latency);
		this.sentMessages = new HashMap<Integer, Integer>(
				sentMessagesPerCluster.size(), 1.0f);
		for (final Map.Entry<String, String> entry : sentMessagesPerCluster
				.entrySet()) {
			this.sentMessages.put(Integer.valueOf(entry.getKey()), Integer
					.valueOf(entry.getValue()));
		}
		this.allData = Integer.valueOf(allData);
		this.onlineData = Integer.valueOf(onlineData);
		this.kCNData = Integer.valueOf(kCNData);
		if (successful) {
			this.allCloser = Integer.valueOf(allCloser);
			this.onlineCloser = Integer.valueOf(onlineCloser);
			this.onlineDataCloser = Integer.valueOf(onlineDataCloser);
			this.senderKCN = Boolean.valueOf(senderKCN);
			this.perfectContacts = -1;
			this.offlineContacts = -1;
			this.missedCloserContacts = -1;
		} else {
			this.allCloser = -1;
			this.onlineCloser = -1;
			this.onlineDataCloser = -1;
			this.senderKCN = false;
			this.perfectContacts = Integer.valueOf(perfectContacts);
			this.offlineContacts = Integer.valueOf(offlineContacts);
			this.missedCloserContacts = Integer.valueOf(missedCloserContacts);
		}
	}

	/**
	 * Constructs and returns a new DataLookupQuality from the given String that
	 * represents one line of a file.
	 * 
	 * @param str
	 *            one line.
	 * @return the new DataLookupQuality.
	 */
	public static DataLookupQuality fromString(final String str) {
		final Matcher line, items;
		final String clusters;
		final Map<String, String> sentMsgs = new HashMap<String, String>();

		line = filePattern.matcher(str);
		if (!line.matches()) {
			System.err.println("Wrong line: '" + str + "'");
			return null;
		}
		clusters = line.group(3);
		items = item.matcher(clusters);
		while (items.find()) {
			sentMsgs.put(items.group(1), items.group(2));
		}
		return new DataLookupQuality(line.group(1), line.group(2), sentMsgs,
				line.group(5), line.group(6), line.group(7), line.group(8),
				line.group(9), line.group(10), line.group(11), line.group(12),
				line.group(13), line.group(14));
	}

}
