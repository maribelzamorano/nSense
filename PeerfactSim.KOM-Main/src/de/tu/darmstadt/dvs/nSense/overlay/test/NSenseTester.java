package de.tu.darmstadt.dvs.nSense.overlay.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import de.tu.darmstadt.dvs.nSense.overlay.operations.NSenseSector;
import de.tu.darmstadt.dvs.nSense.overlay.operations.VectorN;

/**
 * Calculates the sector number for a given dimension and a given sector count
 * that specifies the number of partitions desired. The positions given as input
 * for the test, were obtained with the EQ_POINT_SET function in the EQ
 * partition Algorithm by Paul Leopardi, University of New South Wales, which
 * returns the Center points of regions
 * 
 * @author Maribel Zamorano
 * @version 11/23/2013
 */
public class NSenseTester {

	private static int Dim = 6;// Dimensions posible to test: 2-6

	private static int sectorCount = 24;// Sector partitions posible to test:
										// 5-25

	public static void main(String[] args) throws IOException {

		CSVReader csvReader = new CSVReader(new FileReader(new File(
				"src/de/tu/darmstadt/dvs/NSense/Test/PrecomputedEqualPointSet/eq_point_set"
						+ Dim + "-" + sectorCount + ".cvs")));
		java.util.List<String[]> list = csvReader.readAll();

		String[][] pointsArr = new String[list.size()][];
		pointsArr = list.toArray(pointsArr);

		for (int ii = 0; ii < sectorCount; ii++) {
			float[] position = new float[Dim];
			for (int i = 0; i < Dim; i++) {
				position[i] = Float.parseFloat(pointsArr[i][ii]);
			}
			VectorN pos = new VectorN(position);
			NSenseSector sectorOp = new NSenseSector();
			sectorOp.setDimData(Dim);
			// sectorOp.collarSubdivision(0);
			int sector = sectorOp.getSector(pos, Dim, sectorCount);

			System.out.println("Real Sector is: " + (ii + 1) + ", got:"
					+ sector);
		}

	}
}
