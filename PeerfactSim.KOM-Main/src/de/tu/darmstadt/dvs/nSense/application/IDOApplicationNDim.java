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

package de.tu.darmstadt.dvs.nSense.application;

import org.apache.log4j.Logger;

import de.tu.darmstadt.dvs.nSense.application.moveModels.IMoveModelNDim;
import de.tu.darmstadt.dvs.nSense.application.moveModels.IPositionDistributionNDim;
import de.tu.darmstadt.dvs.nSense.application.operations.MoveOperationNDim;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tud.kom.p2psim.api.common.Operation;
import de.tud.kom.p2psim.api.common.OperationCallback;
import de.tud.kom.p2psim.impl.application.AbstractApplication;
import de.tud.kom.p2psim.impl.application.ido.IDOApplication;
import de.tud.kom.p2psim.impl.application.ido.IDOApplicationFactory;
import de.tud.kom.p2psim.impl.simengine.Simulator;
import de.tud.kom.p2psim.impl.util.logging.SimLogger;

/**
 * This is an application for the IDO-Overlays. Over this class can be control
 * the players actions. The actions are:
 * <ul>
 * <li>startGame - The player connect to the overlay</li>
 * <li>leaveGame - The player leave the overlay</li>
 * <li>startMovingPlayer - The player start to move</li>
 * <li>stopMovingPlayer - The player stop to move
 * <li>startDescreaseSpeed - Decrease the speed for a move of the player</li>
 * <li>startIncreaseSpeed - Increase the speed for a move of the player</li>
 * <li>stopSpeedChanging - Stop the speed changing</li>
 * </ul>
 * 
 * In this class is stored the move models as static attribute. One instance of
 * a move model is used for all players/applications. The move models can be set
 * over the {@link IDOApplicationFactory}. See at
 * de.tud.kom.p2psim.impl.application.ido.moveModels, for the moveModels.
 * 
 * @author Maribel Zamorano
 * @version 24/11/2013
 */
public class IDOApplicationNDim extends AbstractApplication {
	/**
	 * Logger for this class
	 */
	final static Logger log = SimLogger.getLogger(IDOApplicationNDim.class);

	/**
	 * The stored move model
	 */
	private static IMoveModelNDim moveModel;

	/**
	 * The model to distribute the player by starting the game.
	 */
	private static IPositionDistributionNDim positionDistribution;

	/**
	 * The change speed. <br>
	 * 0 for no change.<br>
	 * positive for increase speed.<br>
	 * negative for decrease speed.
	 */
	private int changeSpeed = 0;

	/**
	 * The IDO node to this application.
	 */
	private IDONodeNDim node;

	/**
	 * The players position in the world.
	 */
	private VectorN playerPosition;
	private VectorN playerPositionDimensions;

	/**
	 * The operation fpr the move.
	 */
	protected MoveOperationNDim moveOp = null;

	/**
	 * The waiting time between two move computations.
	 */
	private long intervalBetweenMove;

	/**
	 * The current move vector. Calculate from the last position and the
	 * actually position.
	 */
	private final int[] currentMoveVector = getFirstCurrentMoveVector();
	private final int[] currentMoveDimensionalVector = getFirstCurrentMoveDimensionalVector();
	/**
	 * The constructor. It sets the associated node to this application and the
	 * time between two moves.
	 * 
	 * @param node
	 *            The associated node for this application.
	 * @param intervallBetweenMove
	 *            The time between two moves.
	 */
	public IDOApplicationNDim(IDONodeNDim node, long intervallBetweenMove) {
		this.node = node;
		setIntervalBetweenMove(intervallBetweenMove);
	}

	/**
	 * The player starts to move.
	 */
	public void startMovingPlayer() {
		if (moveModel != null && moveOp == null)
			moveOp();
		else if (moveModel == null)
			log.error("MoveModel not set!");
	}

	/**
	 * The player stops to move.
	 */
	public void stopMovingPlayer() {
		if (moveOp != null) {
			moveOp.stopOperation();
			moveOp = null;
		}
	}

	/**
	 * This class adds an event for the movement.
	 */
	protected void moveOp() {
		moveOp = new MoveOperationNDim(this, new OperationCallback<Object>() {

			@Override
			public void calledOperationFailed(Operation<Object> op) {
				moveOp = null;
				startMovingPlayer();

			}

			@Override
			public void calledOperationSucceeded(Operation<Object> op) {
				moveOp();
			}
		});
		moveOp.scheduleWithDelay(intervalBetweenMove);
	}

	/**
	 * Start the game. The player will be connected to the overlay.
	 * 
	 * @param startMoving
	 *            start moving, if the peer has started the game.
	 */
	public void startGame(boolean startMoving) {
		if (positionDistribution == null)
			log.error("No PositionDistribution set");
		
		playerPositionDimensions = getPositionDistribution().getNextPositionDimensions();
		playerPosition = getPositionDistribution().getNextPosition();
		node.setPositionDimensions(playerPositionDimensions);
		node.join(playerPosition);
		if (startMoving) {
			startMovingPlayer();
		}
	}

	/**
	 * The player leave the game. The player will be disconnected from the
	 * overlay. The parameter is used to identifier a crash or a normal leaving
	 * of the player.
	 * 
	 * @param crash
	 *            A crash or a normal leaving of the player from the game.
	 */
	public void leaveGame(boolean crash) {
		stopMovingPlayer();
		node.leave(crash);
	}

	/**
	 * 0 for no change.<br>
	 * positive for increase speed.<br>
	 * negative for decrease speed.
	 * 
	 * @return Gets the rate of the speed changing.
	 */
	public int speedChanging() {
		return changeSpeed;
	}

	/**
	 * start the increasing of the speed. Every computation of a move, the speed
	 * will be increase.
	 */
	public void startIncreaseSpeed() {
		this.changeSpeed = 1;
	}

	/**
	 * start the decreasing of the speed. Every computation of a move, the speed
	 * will be decrease.
	 */
	public void startDecreaseSpeed() {
		this.changeSpeed = -1;
	}

	/**
	 * stop the changing of the speed.
	 */
	public void stopSpeedChanging() {
		this.changeSpeed = 0;
	}

	/**
	 * Gets the current move vector
	 * 
	 * @return the {@link IDOApplication.currentMoveVector}
	 */
	public int[] getCurrentMoveVector() {
		return currentMoveVector;
	}

	/**
	 * Sets the current move vector.
	 * 
	 * @param moveVector
	 *            The direction of the vector
	 */
	public void setCurrentMoveVector(VectorN moveVector) {

		for (int i = 0; i<moveVector.getSize(); i++) {
			this.currentMoveVector[i] = (int) moveVector.getValue(i);
		}
	}

	public void setCurrentMoveDimensionalVector(VectorN moveVector) {

		for (int i = 0; i<moveVector.getSize(); i++) {
			this.currentMoveDimensionalVector[i] = (int) moveVector.getValue(i);
		}
	}
	
	
	public int[] getCurrentMoveDimensionalVector() {
		return currentMoveDimensionalVector;
	}

	public int[] getFirstCurrentMoveVector() {
		int[] dimensions = new int[6];
		for (int i = 0; i < dimensions.length; i++) {
			dimensions[i] = Simulator.getRandom().nextInt(3) - 1;
		}
		return dimensions;
	}
	
	public int[] getFirstCurrentMoveDimensionalVector() {
		int[] dimensions = new int[6];
		for (int i = 0; i < dimensions.length; i++) {
			dimensions[i] = Simulator.getRandom().nextInt(3) - 1;
		}
		return dimensions;
	}

	/**
	 * Gets the associated node to the application back.
	 * 
	 * @return The associated node to this application.
	 */
	public IDONodeNDim getNode() {
		return node;
	}

	/**
	 * Sets the players Position.
	 * 
	 * @param position
	 *            The position of the player.
	 */
	public void setPlayerPosition(VectorN position) {
		this.playerPosition = position;
	}
	public void setPlayerPositionDimensions(VectorN position) {
		this.playerPositionDimensions = position;
	}

	/**
	 * Gets the players Position.
	 * 
	 * @return The position of the player.
	 */
	public VectorN getPlayerPosition() {
		return playerPosition;
	}
	public VectorN getPlayerPositionDimensions() {
		return playerPositionDimensions;
	}
	/**
	 * Sets the interval between two moves.
	 * 
	 * @param time
	 *            The time between two moves.
	 */
	public void setIntervalBetweenMove(long time) {
		this.intervalBetweenMove = time;
	}

	/**
	 * Sets the move model for the players moves.
	 * 
	 * @param model
	 *            The move model, which should be use.
	 */
	public static void setMoveModel(IMoveModelNDim model) {
		moveModel = model;
	}

	/**
	 * Sets the position distribution for the players.
	 * 
	 * @param posDistribution
	 *            The position distribution model, which should be use.
	 */
	public static void setPositionDistribution(
			IPositionDistributionNDim posDistribution) {
		positionDistribution = posDistribution;
	}

	/**
	 * Gets the move model for this class
	 * 
	 * @return The move model
	 */
	public static IMoveModelNDim getMoveModel() {
		return moveModel;
	}

	/**
	 * Gets the position distribution for this class
	 * 
	 * @return The position distribution
	 */
	public static IPositionDistributionNDim getPositionDistribution() {
		return positionDistribution;
	}

}
