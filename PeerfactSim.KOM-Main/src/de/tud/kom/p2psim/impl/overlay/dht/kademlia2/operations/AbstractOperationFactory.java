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


package de.tud.kom.p2psim.impl.overlay.dht.kademlia2.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tud.kom.p2psim.impl.overlay.dht.kademlia2.types.KademliaOverlayID;

/**
 * An abstract operation factory that saves configuration information that is
 * useful for subclasses.
 * 
 * @author Tobias Lauinger <tl1003@rbg.informatik.tu-darmstadt.de>
 * @version 05/06/2011
 */
public abstract class AbstractOperationFactory<T extends KademliaOverlayID>
		implements OperationFactory<T> {

	/**
	 * Configuration values ("constants").
	 */
	protected final OperationsConfig config;

	/**
	 * Operations of this Node that have not yet finished.
	 */
	private final Set<KademliaOperation> runningOperations;

	/**
	 * Constructs a new abstract operation factory and sets the given
	 * configuration information.
	 * 
	 * @param conf
	 *            an OperationsConfig reference that permits to retrieve
	 *            configuration "constants".
	 */
	public AbstractOperationFactory(final OperationsConfig conf) {
		this.config = conf;
		this.runningOperations = new HashSet<KademliaOperation>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void operationConstructed(KademliaOperation newOperation) {
		this.runningOperations.add(newOperation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void operationFinished(KademliaOperation finishedOperation) {
		this.runningOperations.remove(finishedOperation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void abortAllOperations() {
		final List<KademliaOperation> runningOpsCopy;
		runningOpsCopy = new ArrayList<KademliaOperation>(runningOperations);
		for (final KademliaOperation op : runningOpsCopy) {
			op.abort();
		}
	}
}
