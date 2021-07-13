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


package de.tud.kom.p2psim.impl.analyzer.csvevaluation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import de.tud.kom.p2psim.api.analyzer.Analyzer.NetAnalyzer;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.network.NetID;
import de.tud.kom.p2psim.api.network.NetMessage;

/**
 *
 * @author <peerfact@kom.tu-darmstadt.de>
 * @version 05/06/2011
 *
 */
public class MessageCounter implements NetAnalyzer {

	static final String FILENAME_PREFIX = "outputs/";

	Map<Class<? extends Message>, Integer> msgs = new TreeMap<Class<? extends Message>, Integer>(
			new ClassComparator());

	private boolean active;

	private int hostCount;

	private String fileName;

	@Override
	public void netMsgDrop(NetMessage msg, NetID id) {
		// TODO Auto-generated method stub

	}

	protected Message getOverlayMsg(NetMessage msg) {
		return msg.getPayload().getPayload();
	}

	@Override
	public void netMsgReceive(NetMessage msg, NetID id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void netMsgSend(NetMessage msg, NetID id) {
		if (active)
			countMessage(getOverlayMsg(msg));
	}

	@Override
	public void start() {
		active = true;
	}

	public void setHostCount(int hostCount) {
		this.hostCount = hostCount;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void stop(Writer output) {
		active = false;
		try {
			dumpMsgs(new PrintWriter(System.out));
			writeToFile(FILENAME_PREFIX + fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void countMessage(Message olMsg) {

		Integer occurs = msgs.get(olMsg.getClass());
		int newOccurs;

		if (occurs != null)
			newOccurs = occurs + 1;
		else
			newOccurs = 1;

		msgs.put(olMsg.getClass(), newOccurs);

	}

	public void dumpMsgs(Writer w) throws IOException {

		Set<Entry<Class<? extends Message>, Integer>> entries = msgs.entrySet();

		w.write("# Hosts ");
		for (Entry<Class<? extends Message>, Integer> entry : entries) {
			w.write(entry.getKey().getSimpleName() + "	");
		}
		w.write('\n');
		w.write(hostCount + "	");
		for (Entry<Class<? extends Message>, Integer> entry : entries) {
			w.write(entry.getValue() + "	");
		}
		w.write('\n');
	}

	public void writeToFile(String fileName) throws IOException {
		FileWriter fstream = new FileWriter(fileName, true);
		BufferedWriter out = new BufferedWriter(fstream);
		dumpMsgs(out);
		out.close();
	}

	public class ClassComparator implements Comparator<Class> {

		@Override
		public int compare(Class o1, Class o2) {
			return o1.getSimpleName().compareTo(o2.getSimpleName());
		}

	}

}
