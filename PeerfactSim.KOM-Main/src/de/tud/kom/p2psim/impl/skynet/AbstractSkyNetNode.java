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


package de.tud.kom.p2psim.impl.skynet;

import de.tud.kom.p2psim.api.common.ConnectivityListener;
import de.tud.kom.p2psim.api.common.Host;
import de.tud.kom.p2psim.api.common.Message;
import de.tud.kom.p2psim.api.overlay.KBR;
import de.tud.kom.p2psim.api.overlay.KBRForwardInformation;
import de.tud.kom.p2psim.api.overlay.KBRListener;
import de.tud.kom.p2psim.api.overlay.OverlayContact;
import de.tud.kom.p2psim.api.overlay.OverlayID;
import de.tud.kom.p2psim.api.overlay.OverlayKey;
import de.tud.kom.p2psim.api.overlay.OverlayNode;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInfo;
import de.tud.kom.p2psim.api.service.skynet.SkyNetNodeInterface;
import de.tud.kom.p2psim.api.service.skynet.SupportPeer;
import de.tud.kom.p2psim.api.service.skynet.overlay2SkyNet.MetricsCollectorDelegator;
import de.tud.kom.p2psim.api.service.skynet.overlay2SkyNet.TreeHandlerDelegator;
import de.tud.kom.p2psim.api.transport.TransLayer;
import de.tud.kom.p2psim.impl.skynet.analyzing.writers.AttributeWriter;
import de.tud.kom.p2psim.impl.skynet.attributes.AttributeInputStrategy;
import de.tud.kom.p2psim.impl.skynet.attributes.AttributeUpdateStrategy;
import de.tud.kom.p2psim.impl.skynet.attributes.SPAttributeInputStrategy;
import de.tud.kom.p2psim.impl.skynet.attributes.SPAttributeUpdateStrategy;
import de.tud.kom.p2psim.impl.skynet.components.MessageCounter;
import de.tud.kom.p2psim.impl.skynet.components.SkyNetMessageHandler;
import de.tud.kom.p2psim.impl.skynet.components.SkyNetNode;
import de.tud.kom.p2psim.impl.skynet.components.TreeHandler;
import de.tud.kom.p2psim.impl.skynet.metrics.MetricInputStrategy;
import de.tud.kom.p2psim.impl.skynet.metrics.MetricUpdateStrategy;
import de.tud.kom.p2psim.impl.skynet.metrics.MetricsInterpretation;
import de.tud.kom.p2psim.impl.skynet.queries.QueryHandler;
import de.tud.kom.p2psim.impl.skynet.queries.SPQueryHandler;

/**
 * This abstract class implements all methods from the interfaces, of which a
 * SkyNet-node consists (except the <code>SimulationEventHandler</code>
 * -interface) and which are utilized by a SkyNet-node to address its several
 * components and by a the corresponding host to enable the communication
 * between its different layers. By putting the listed methods in this abstract
 * class, it relieves the {@link SkyNetNode} of implementing all accessing
 * methods. Instead, <code>SkyNetNode</code> is used to implement the
 * communication and interaction with other SkyNet-nodes.
 * 
 * @author Dominik Stingl <peerfact@kom.tu-darmstadt.de>
 * @version 1.0, 15.11.2008
 * 
 */
public abstract class AbstractSkyNetNode implements SkyNetNodeInterface,
		SupportPeer, ConnectivityListener, KBRListener {

	// Object-references for common usage
	private SkyNetMessageHandler messageHandler;

	private MessageCounter skyNetMsgCounter;

	private TreeHandler treeHandler;

	private TransLayer transLayer;

	private OverlayNode overlayNode;

	private Host host;

	// Object-references for metrics
	private MetricInputStrategy metricInput;

	private MetricUpdateStrategy metricUpdate;

	private MetricsInterpretation metricsInterpretation;

	// Object-references for attributes
	private AttributeInputStrategy attributeInput;

	private AttributeUpdateStrategy attributeUpdate;

	private QueryHandler queryHandler;

	// Object-references for sp-attributes
	private SPAttributeInputStrategy spAttributeInput;

	private SPAttributeUpdateStrategy spAttributeUpdate;

	private SPQueryHandler spQueryHandler;

	// Variables and object-references for information
	private boolean isSupportPeer;

	private short port;

	private SkyNetNodeInfo nodeInfo;

	public AbstractSkyNetNode(SkyNetNodeInfo nodeInfo, short port,
			TransLayer transLayer, OverlayNode overlayNode,
			TreeHandlerDelegator treeHandlerDelegator,
			MetricsCollectorDelegator metricsCollectorDelegator) {
		this.overlayNode = overlayNode;
		if (this.overlayNode instanceof KBR) {
			((KBR) this.overlayNode).setKBRListener(this);
		}
		this.messageHandler = new SkyNetMessageHandler(this, this);
		this.skyNetMsgCounter = new MessageCounter();
		this.nodeInfo = nodeInfo;
		this.port = port;
		this.transLayer = transLayer;
		this.transLayer.addTransMsgListener(messageHandler, getPort());
		metricUpdate = new MetricUpdateStrategy(this);
		metricInput = new MetricInputStrategy(this, metricsCollectorDelegator,
				metricUpdate.getStorage());
		metricsInterpretation = new MetricsInterpretation(this,
				metricUpdate.getStorage());
		treeHandler = new TreeHandler(this, treeHandlerDelegator);

		// Object-references for attributes
		attributeUpdate = new AttributeUpdateStrategy(this, this);
		attributeInput = new AttributeInputStrategy(this,
				attributeUpdate.getStorage());
		queryHandler = new QueryHandler(this, attributeUpdate.getStorage());

		// Object-references for sp-attributes
		spAttributeUpdate = new SPAttributeUpdateStrategy(this,
				attributeUpdate.getStorage());
		spAttributeInput = new SPAttributeInputStrategy(this,
				attributeUpdate.getStorage());
		spQueryHandler = new SPQueryHandler(this, attributeUpdate.getStorage());
	}

	// ---------------------------------------------------------
	// Getter and Setter-Methods of the variables listed above
	// ---------------------------------------------------------

	// methods for the object-references for common usage
	public SkyNetMessageHandler getSkyNetMessageHandler() {
		return messageHandler;
	}

	public MessageCounter getMessageCounter() {
		return skyNetMsgCounter;
	}

	public TreeHandler getTreeHandler() {
		return treeHandler;
	}

	public TransLayer getTransLayer() {
		return transLayer;
	}

	public OverlayNode getOverlayNode() {
		return overlayNode;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	// methods for the variables and object-references for information
	public SkyNetNodeInfo getSkyNetNodeInfo() {
		return nodeInfo;
	}

	public void setSkyNetNodeInfo(SkyNetNodeInfo nodeInfo) {
		this.nodeInfo = nodeInfo;
	}

	public void setSupportPeer(boolean flag) {
		this.isSupportPeer = flag;
		if (flag) {
			AttributeWriter.getInstance().incrementAmountOfSupportPeers();
		} else {
			AttributeWriter.getInstance().decrementAmountOfSupportPeers();
		}
	}

	public boolean isSupportPeer() {
		return isSupportPeer;
	}

	public short getPort() {
		return port;
	}

	public OverlayID getOverlayID() {
		return getSkyNetNodeInfo().getSkyNetID();
	}

	// Object-references for metrics
	public MetricInputStrategy getMetricInputStrategy() {
		return metricInput;
	}

	public MetricUpdateStrategy getMetricUpdateStrategy() {
		return metricUpdate;
	}

	// Object-references for attributes
	public AttributeInputStrategy getAttributeInputStrategy() {
		return attributeInput;
	}

	public AttributeUpdateStrategy getAttributeUpdateStrategy() {
		return attributeUpdate;
	}

	public QueryHandler getQueryHandler() {
		return queryHandler;
	}

	// Object-references for sp-attributes
	public SPAttributeInputStrategy getSPAttributeInputStrategy() {
		return spAttributeInput;
	}

	public SPAttributeUpdateStrategy getSPAttributeUpdateStrategy() {
		return spAttributeUpdate;
	}

	public SPQueryHandler getSPQueryHandler() {
		return spQueryHandler;
	}

	public MetricsInterpretation getMetricsInterpretation() {
		return metricsInterpretation;
	}

	@Override
	public boolean isPresent() {
		return overlayNode.isPresent();
	}

	// -----------------------------------------------------
	// Methods, which are not needed
	// -----------------------------------------------------

	@Override
	public void deliver(OverlayKey key, Message msg) {
		// not needed yet
	}

	@Override
	public void forward(KBRForwardInformation information) {
		// not needed yet
	}

	@Override
	public void update(OverlayContact contact, boolean joined) {
		// not needed yet
	}

}
