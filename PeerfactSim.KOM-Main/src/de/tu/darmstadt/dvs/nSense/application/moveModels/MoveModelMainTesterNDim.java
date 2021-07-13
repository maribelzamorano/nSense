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

package de.tu.darmstadt.dvs.nSense.application.moveModels;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.tu.darmstadt.dvs.nSense.application.IDOApplicationNDim;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;
import de.tud.kom.p2psim.Constants;
import de.tud.kom.p2psim.impl.simengine.Simulator;

/**
 * This class provides the testing of the move models. It execute the the
 * defined move model in getMoveModel, and write a gnuPlot data with the derived
 * position out.
 * 
 * @author Maribel Zamorano
 * @version 24/11/2013
 */
public class MoveModelMainTesterNDim {

	private static String outputName = "gnuMoveModelNDim.dat";

	private static boolean withIncreasingSpeed = false;

	private static int NUMBER_OF_EXECUTIONS = 10000;

	private static int Dimensions = 4;

	private static VectorN worldDimensions = new VectorN(
			getFirstCurrentMoveVector());

	private static IMoveModelNDim getMoveModel() {

		IPortalComponentNDim portal = new RandomPortalNDim();
		portal.setProbability(1.0 / NUMBER_OF_EXECUTIONS);

		// ----------- RANDOM PATH MOVE ---------------//
		outputName = "RandomPathPortalModeNDim.dat";
		RandomPathMoveModelNDim model = new RandomPathMoveModelNDim();
		model.setMoveSpeedLimit(4);
		model.setPortal(portal);
		model.setWorldDimensions(worldDimensions);
		model.setSpeedChangingRate(0.025);

		return model;
	}

	public static float[] getFirstCurrentMoveVector() {
		float[] dimensions = new float[Dimensions];
		for (int i = 0; i < dimensions.length; i++) {
			dimensions[i] = 1200f;
		}
		return dimensions;
	}

	public static void main(String[] args) throws IOException {
		IMoveModelNDim model = getMoveModel();

		IDOApplicationNDim app = new IDOApplicationTestNDim();
		float[] playerPosition = new float[Dimensions];
		float[] currentMoveVector = new float[Dimensions];
		for (int i = 0; i < Dimensions; i++) {
			playerPosition[i] = worldDimensions.getValue(i) / 2;
			currentMoveVector[i] = Simulator.getRandom().nextInt(5);
		}
		app.setPlayerPosition(new VectorN(playerPosition));
		app.setCurrentMoveVector(new VectorN(currentMoveVector));
		File output = new File(Constants.OUTPUTS_DIR + File.separator
				+ outputName);
		FileWriter writer = new FileWriter(output);
		writer.write("# counter\n");
		for (int i = 0; i < NUMBER_OF_EXECUTIONS; i++) {
			VectorN position = model.getNextPosition(app);
			app.setPlayerPosition(position);
			if (withIncreasingSpeed && i == NUMBER_OF_EXECUTIONS / 2)
				app.startIncreaseSpeed();
			writer.write(i + "\t");
			for (int ii = 0; ii < Dimensions; ii++) {
				writer.write(position.getValue(ii) + "\t");
			}
			writer.write("\n");
		}
		writer.close();
		System.out.println("Finished");
	}
}

class IDOApplicationTestNDim extends IDOApplicationNDim {

	public IDOApplicationTestNDim() {
		super(null, 0);
	}

}
