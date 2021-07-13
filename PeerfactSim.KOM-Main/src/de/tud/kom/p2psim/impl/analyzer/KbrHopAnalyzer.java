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


package de.tud.kom.p2psim.impl.analyzer;

import java.io.Writer;
import java.util.HashSet;

import de.tud.kom.p2psim.api.analyzer.Analyzer.KBROverlayAnalyzer;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.OverlayContact;

/**
 * This Analyzer collects data about the hop count of a simulation using the KBR
 * interface for routing
 * 
 * @author Julius Rueckert <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
public class KbrHopAnalyzer implements KBROverlayAnalyzer {

	int numberOfKeyRoutedQueries = 0;

	int sumOfKeyRoutedHops = 0;

	int overallNumberOfQueries = 0;

	int overallSumOfHops = 0;

	HashSet<Message> queryMsgs = new HashSet<Message>();

	@Override
	public void messageDelivered(OverlayContact contact, Message msg, int hops) {
		if (queryMsgs.remove(msg.getPayload())) {
			numberOfKeyRoutedQueries++;
			sumOfKeyRoutedHops += hops;
		}

		overallNumberOfQueries++;
		overallNumberOfQueries += hops;
	}

	@Override
	public void messageForwarded(OverlayContact sender,
			OverlayContact receiver, Message msg, int hops) {
		// Do nothing
	}

	@Override
	public void queryFailed(OverlayContact failedHop, Message appMsg) {
		queryMsgs.remove(appMsg);
	}

	@Override
	public void queryStarted(OverlayContact contact, Message appMsg) {
		queryMsgs.add(appMsg);
	}

	@Override
	public void start() {
		// Do nothing
	}

	@Override
	public void stop(Writer output) {
		System.out.println("======= BEGIN KbrHopAnalyzer Output =======");
		System.out
				.println("Average hop count for messages routed towards a key: "
						+ ((numberOfKeyRoutedQueries == 0) ? 0
								: ((double) sumOfKeyRoutedHops / numberOfKeyRoutedQueries)));
		System.out.println("Overall number of queries: "
				+ overallNumberOfQueries);
		System.out.println("Number of routed queries: "
				+ overallNumberOfQueries);
		System.out.println("======= END KbrHopAnalyzer Output =======");
	}

}
