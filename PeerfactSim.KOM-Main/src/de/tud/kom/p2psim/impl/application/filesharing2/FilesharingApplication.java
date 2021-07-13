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


package de.tud.kom.p2psim.impl.application.filesharing2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.tud.kom.p2psim.api.common.ConnectivityEvent;
import de.tud.kom.p2psim.api.common.ConnectivityListener;
import de.tud.kom.p2psim.api.common.SupportOperations;
import de.tud.kom.p2psim.impl.application.AbstractApplication;
import de.tud.kom.p2psim.impl.application.filesharing2.documents.DocumentSpace;
import de.tud.kom.p2psim.impl.application.filesharing2.documents.IDocumentSet;
import de.tud.kom.p2psim.impl.application.filesharing2.operations.FSJoinOperation;
import de.tud.kom.p2psim.impl.application.filesharing2.operations.FSLeaveOperation;
import de.tud.kom.p2psim.impl.application.filesharing2.operations.FSLookupResourceFromSetOperation;
import de.tud.kom.p2psim.impl.application.filesharing2.operations.FSLookupResourceOperation;
import de.tud.kom.p2psim.impl.application.filesharing2.operations.FSPublishResourcesOperation;
import de.tud.kom.p2psim.impl.application.filesharing2.operations.periodic.ExponentialIntervalModel;
import de.tud.kom.p2psim.impl.application.filesharing2.operations.periodic.LinearIntervalModel;
import de.tud.kom.p2psim.impl.application.filesharing2.operations.periodic.PeriodicCapableOperation;
import de.tud.kom.p2psim.impl.application.filesharing2.overlays.IOverlayHandler;
import de.tud.kom.p2psim.impl.common.Operations;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;
import de.tud.kom.p2psim.impl.util.stat.distributions.Distribution;
import de.tud.kom.p2psim.impl.util.stat.distributions.ExponentialDistribution;

/**
 * This filesharing application layer allows to define document sets with a
 * given size and a given popularity distribution in the XML config file. Then
 * it allows to call various random actions using the predefined document sets.
 * A document set can be defined like in this example:
 * 
 * <pre>
 * &lt;Configuration&gt;
 *  &lt;!-- ... --&gt;
 *  &lt;ResourceSpace class="de.tud.kom.p2psim.impl.application.filesharing2.documents.DocumentSpace" static="getInstance" useRanks="true"&gt;
 * 
 *  	&lt;ResourceSet class="de.tud.kom.p2psim.impl.application.filesharing2.documents.ZipfDocumentSet" name="files1" size="$size" zipfExp="0.7" meanReorderIntvl="10m"/&gt;
 * 		&lt;ResourceSet class="de.tud.kom.p2psim.impl.application.filesharing2.documents.ZipfDocumentSet" name="files2" size="$size" zipfExp="0.7" meanReorderIntvl="10m"/&gt;
 * 	
 * 		&lt;ResourceSet class="de.tud.kom.p2psim.impl.application.filesharing2.documents.FlatDocumentSet" name="files1" size="150"/&gt;
 * 		&lt;ResourceSet class="de.tud.kom.p2psim.impl.application.filesharing2.documents.FlatDocumentSet" name="files2" size="150"/&gt;
 * 	&lt;/ResourceSpace&gt;
 *  &lt;!-- ... --&gt;
 * &lt;/Configuration&gt;
 * </pre>
 * 
 * For further information please refer to the javadoc of the corresponding
 * resource sets.
 * 
 * @author Leo Nobach <peerfact@kom.tu-darmstadt.de>
 * 
 * @version 05/06/2011
 */
@SuppressWarnings("unchecked")
public class FilesharingApplication extends AbstractApplication implements
		SupportOperations, ConnectivityListener {

	private final static Logger log = SimLogger
			.getLogger(FilesharingApplication.class);

	private IOverlayHandler overlay;

	private Set<Integer> publishedRanks = new HashSet<Integer>();

	private List<PeriodicCapableOperation> periodicLookupOperations = new Vector<PeriodicCapableOperation>();

	/**
	 * Default Constructor
	 * 
	 * @param overlay
	 */
	public FilesharingApplication(IOverlayHandler overlay) {
		overlay.setFSApplication(this);
		this.overlay = overlay;
	}

	/**
	 * Joins the overlay network initially. Connects to the overlay network
	 * using the method defined by the specific overlay.
	 */
	public void join() {
		this.getHost().getNetLayer().addConnectivityListener(this);
		FSJoinOperation op = new FSJoinOperation(overlay, this,
				Operations.EMPTY_CALLBACK);
		op.scheduleImmediately();
	}

	/**
	 * Leaves the overlay network. Disconnects from the overlay network as
	 * defined by the overlay used.
	 */
	public void leave() {
		this.getHost().getNetLayer().removeConnectivityListener(this);
		FSLeaveOperation op = new FSLeaveOperation(overlay, this,
				Operations.EMPTY_CALLBACK);
		op.scheduleImmediately();
		stopAllLookupOperations();
	}

	/**
	 * Publishes random resources from the given set. The number of resources
	 * drawn from this set and published is randomly taken from an exponential
	 * distribution with the given mean amount
	 * 
	 * @param setName
	 *            : the given string name of the document set to use, as it is
	 *            defined in the configuration.
	 * @param meanAmount
	 *            : the mean amount of documents published (amount is
	 *            exponentially distributed).
	 */
	public void publishResourcesFromSet(String setName, int meanAmount) {
		Distribution meanDocAmount = new ExponentialDistribution(meanAmount);
		int docAmount = (int) meanDocAmount.returnValue();

		Set<Integer> docIDs = DocumentSpace.getInstance()
				.getSomeKeysForPublish(
						DocumentSpace.getInstance().getResourceSet(setName),
						docAmount);

		log.error("Publishing the documents " + docIDs + "...");

		publishResourcesSet(docIDs);

	}

	/**
	 * Draws exactly one resource from the given set and publishes it.
	 * 
	 * @param setName
	 *            : the given string name of the document set to use, as it is
	 *            defined in the configuration.
	 */
	public void publishOneResourceFromSet(String setName) {

		Set<Integer> docIDs = DocumentSpace.getInstance()
				.getSomeKeysForPublish(
						DocumentSpace.getInstance().getResourceSet(setName), 1);

		log.debug("Publishing the document " + docIDs + "...");

		publishResourcesSet(docIDs);

	}

	/**
	 * Looks up a resource from a given set in the overlay network. <b>Only
	 * looks up documents from a set that were already published</b>, and anyone
	 * of the publishers <b>is currently online</b>. If no document of a set is
	 * published or no publisher of a document of this set is currently online,
	 * a runtime error is thrown.
	 * 
	 * @param setName
	 *            : the given string name of the document set to use, as it is
	 *            defined in the configuration.
	 */
	public void lookupResourceFromSet(String setName) {
		lookupResourceFromSetDirect(DocumentSpace.getInstance().getResourceSet(
				setName));
	}

	/**
	 * Looks up a resource from a given set in the overlay network. <b>Only
	 * looks up documents from a set that were already published</b>, and anyone
	 * of the publishers <b>is currently online</b>. If no document of a set is
	 * published or no publisher of a document of this set is currently online,
	 * a runtime error is thrown.
	 * 
	 * @param set
	 *            : the given document set object to use.
	 */
	public void lookupResourceFromSetDirect(IDocumentSet set) {
		lookupResource(getDocumentForLookup(set));
	}

	/**
	 * <b>Periodically</b> looks up a resource from a given set in the overlay
	 * network. <b>Only looks up documents from a set that were already
	 * published</b>, and anyone of the publishers <b>is currently online</b>.
	 * If no document of a set is published or no publisher of a document of
	 * this set is currently online, a runtime error is thrown.
	 * 
	 * The lookups are equally distributed over time, thus the interval between
	 * two lookups <b>is exponentially distributed</b>.
	 * 
	 * @param setName
	 *            : the given string name of the document set to use, as it is
	 *            defined in the configuration.
	 * @param meanPeriod
	 *            : The mean lookup interval
	 */
	public void lookupResourceFromSetPeriodically(String setName,
			long meanPeriod) {
		lookupResourceFromSetPeriodicallyDirect(DocumentSpace.getInstance()
				.getResourceSet(setName), meanPeriod);
	}

	public void lookupResourceFromSetIncreasingRequests(String setName,
			double startRequestsPerHour, double endRequestsPerHour,
			long interval) {
		IDocumentSet set = DocumentSpace.getInstance().getResourceSet(setName);
		FSLookupResourceFromSetOperation op = new FSLookupResourceFromSetOperation(
				set, this, Operations.EMPTY_CALLBACK);
		this.periodicLookupOperations.add(op);
		op.schedulePeriodically(new LinearIntervalModel(startRequestsPerHour,
				endRequestsPerHour, interval));
	}

	/**
	 * <b>Periodically</b> looks up a resource from a given set in the overlay
	 * network. <b>Only looks up documents from a set that were already
	 * published</b>, and anyone of the publishers <b>is currently online</b>.
	 * If no document of a set is published or no publisher of a document of
	 * this set is currently online, a runtime error is thrown.
	 * 
	 * The lookups are equally distributed over time, thus the interval between
	 * two lookups <b>is exponentially distributed</b>.
	 * 
	 * @param set
	 *            : the given document set object to use
	 * @param meanPeriod
	 *            : The mean lookup interval
	 */
	public void lookupResourceFromSetPeriodicallyDirect(IDocumentSet set,
			long meanPeriod) {
		FSLookupResourceFromSetOperation op = new FSLookupResourceFromSetOperation(
				set, this, Operations.EMPTY_CALLBACK);
		this.periodicLookupOperations.add(op);
		op.schedulePeriodically(new ExponentialIntervalModel(meanPeriod));
	}

	protected int getDocumentForLookup(IDocumentSet set) {
		int res = DocumentSpace.getInstance().getKeyForLookup(set);
		// System.out.println(this.getHost().getNetLayer() + "Retrieved" + res +
		// "for lookup.");
		return res;
	}

	/**
	 * <b>Periodically</b> looks up a document with a given document ID.
	 * 
	 * @param documentID
	 *            : the integer ID of the document
	 * @param meanPeriod
	 *            : the mean period in which the document shall be looked up.
	 */
	public void lookupResourcePeriodically(int documentID, long meanPeriod) {
		FSLookupResourceOperation op = new FSLookupResourceOperation(overlay,
				documentID, this, Operations.EMPTY_CALLBACK);
		op.schedulePeriodically(new ExponentialIntervalModel(meanPeriod));
		this.periodicLookupOperations.add(op);
	}

	/**
	 * Publishes a set of resources.
	 * 
	 * @param docs
	 *            : a string containing a comma-separated list of resources,
	 *            e.g. "1,2,3,4,5"
	 */
	public void publishResources(String docs) {

		String[] docIDStr = docs.split(",");
		Set<Integer> docIDs = new HashSet<Integer>();

		for (int i = 0; i < docIDStr.length; i++) {
			try {
				docIDs.add(Integer.parseInt(docIDStr[i]));
			} catch (NumberFormatException e) {
				log.warn(docIDStr[i]
						+ " is not a valid integer identifier. Skipping this document identifier.");
			}

		}

		if (docIDs.size() > 0) {
			publishResourcesSet(docIDs);
		} else {
			log.warn("No valid identifiers were given in the publish action. Skipping this request.");
		}

	}

	/**
	 * Publishes a set of resources.
	 * 
	 * @param docs
	 *            : a set of integer document identifiers.
	 */
	public void publishResourcesSet(Set<Integer> docs) {
		publishedRanks.addAll(docs);
		if (this.getHost().getNetLayer().isOnline()) {
			// System.out.println(this.getHost().getNetLayer() +
			// "Beginning sharing, adding " + publishedRanks +
			// "to shared set.");
			DocumentSpace.getInstance().activateMyPublishedKeys(docs);
		}
		FSPublishResourcesOperation op = new FSPublishResourcesOperation(
				overlay, docs, this, Operations.EMPTY_CALLBACK);
		op.scheduleImmediately();
	}

	/**
	 * Looks up a resource given its document ID.
	 * 
	 * @param documentID
	 */
	public void lookupResource(int documentID) {
		if (this.getHost().getNetLayer().isOffline()) {
			log.warn("Host is offline, suspending lookup.");
			return;
		}
		FSLookupResourceOperation op = new FSLookupResourceOperation(overlay,
				documentID, this, Operations.EMPTY_CALLBACK);
		op.scheduleImmediately();
	}

	@Override
	public void connectivityChanged(ConnectivityEvent ce) {
		if (ce.isOffline()) {
			// System.out.println(this.getHost().getNetLayer() +
			// "Going offline, removing " + publishedRanks +
			// " from shared set.");
			DocumentSpace.getInstance().deactivateMyPublishedKeys(
					publishedRanks);

		} else {
			// System.out.println(this.getHost().getNetLayer() +
			// "Going online, adding " + publishedRanks + "to shared set.");
			DocumentSpace.getInstance().activateMyPublishedKeys(publishedRanks);
		}
	}

	public void stopAllLookupOperations() {
		for (PeriodicCapableOperation pco : periodicLookupOperations) {
			pco.stop();
			periodicLookupOperations.remove(pco);
		}
	}

	/**
	 * Lets this node fail with a given probability in percent (0 <=
	 * failProbabilityPercent < 100).
	 * 
	 * @param failProbabilityPercent
	 *            : the probability for this node to fail given in percent.
	 */
	public void fail(int failProbabilityPercent) {
		int rand = Simulator.getRandom().nextInt(100);
		if (rand < failProbabilityPercent) {
			this.getHost().getNetLayer().goOffline();
			this.getHost().getNetLayer().removeConnectivityListener(this);
		}
	}

}
