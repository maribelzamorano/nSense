package de.tu.darmstadt.dvs.nSense.overlay.operations
import scala.io._
import scala.math._
import scala.Array.canBuildFrom
/**
 * @author Maribel Zamorano
 * @version 11/23/2013
 */
class NSenseSector {
  var totalSectorsPerCollar: Array[Array[Array[Int]]] = null
  var collarAngles: Array[Array[Array[Float]]] = null
  var collarSubdivision: Int = 0
  var i: Int = 1;
  val PI: Double = Math.PI;

  /**
   * Finds the sector for the given position of a node. The sector number
   * is obtained by applying the function recursively until the collar
   * consists of only one partition(m=1).
   * This function is used by {@link de.tud.kom.p2psim.impl.overlay.ido.nsense.NSense}
   *
   *
   * @param position
   *            The position of the node.
   * @param dimension
   *            The dimension.
   * @param sectorCount
   *            The sectorCount is the number of partitions.
   * @return collarSubdivision, is the number of the sector containing the node's position.
   */
  def getSector(position: VectorN, dimension: Int, sectorCount: Int): Int = {
    var angle: Float = getAngle(position, dimension);
    var collar: Int = 1
    val dimensionInArray = dimension - 2
    for (collarAngle <- 0 until (collarAngles(dimensionInArray)(sectorCount - 1)).length) {
      if ((angle > collarAngles(dimensionInArray)(sectorCount - 1)(collarAngle)) && (angle <= collarAngles(dimensionInArray)(sectorCount - 1)(collarAngle + 1))) {
        collar += collarAngle;
      }
    }
    val collarSectors: Int = totalSectorsPerCollar(dimensionInArray)(sectorCount - 1)(collar - 1) // this value corresponds to m in the algorithm, is the number of sectors the collar contains.
    var firstSectorInCollar: Int = 1;
    for (currentCollar <- 1 until (collar)) {
      firstSectorInCollar += (totalSectorsPerCollar(dimensionInArray)(sectorCount - 1)(currentCollar - 1));
    }
    collarSubdivision += firstSectorInCollar;

    if (collarSectors == 1) {
      return (collarSubdivision - (i - 1));
    } else {
      i += 1
      getSector(position: VectorN, dimension - 1, collarSectors);
    }

  }

  /**
   * Computes the angle between a point(p1,...,pn) and the basis vector ei(0,0..,1)
   * for the n-space
   *
   * @param position
   *            The position of the node.
   * @param dim
   *            The dimension required.
   * @return The Angle between the position vector and the basis vector.
   */
  def getAngle(position: VectorN, dim: Int): Float = {

    var sum: Double = 0.0f;
    for (dimension <- 1 to dim) {
      if (dimension != (dim)) {
        sum = sum + (Math.pow(position.getValue(dimension - 1), 2));
      }
    }

    var normCross: Double = Math.sqrt(sum);
    var dot: Double = position.getValue(dim - 1).toDouble;
    var angle: Double = Math.atan2(normCross, dot);

    if (dim == 2) { //correct angle for positions that include negative points in the vector for dimension=2.
      if ((dot >= 0.0) && (normCross > 0.0)) {
        if (position.getValue(0) < 0) { angle = (3 * (PI / 2)) - (PI - angle); }
        else
          angle = (PI / 2) - angle;
      }
      if ((normCross >= 0.0) && (dot < 0.0)) {
        if (position.getValue(0) < 0) { angle = (PI / 2) + angle; }
        else
          angle = (3 * (PI / 2)) + (PI - angle);
      }
    }
    return angle.toFloat;
  }

  /**
   * This method fills the respective arrays with the precomputed collar angles and with the number of sectors contained
   * in each collar. The precomputed data was obtained using the Recursive Zonal Equal Area (EQ) Sphere Partitioning Toolbox.
   * Release 1.10 2005-06-26, by Paul Leopardi, University of New South Wales.
   * The data was calculated for Dimensions = 2 to 10 and Sector count = 1 to 64.
   *
   *
   * @param dimension
   *            The dimension required.
   */
  def setDimData(dimension: Int): Unit = {
    collarAngles = new Array[Array[Array[Float]]](dimension)
    totalSectorsPerCollar = new Array[Array[Array[Int]]](dimension)
    for (dim <- 0 until dimension) {
      collarAngles(dim) = List("src/de/tu/darmstadt/dvs/NSense/PrecomputedSectors/sectorsCap" + (dim + 2) + ".cvs").map(io.Source.fromFile(_).getLines.map(_.split(",").map(_.toFloat))).flatten.toArray;
      totalSectorsPerCollar(dim) = List("src/de/tu/darmstadt/dvs/NSense/PrecomputedSectors/sectorsRegions" + (dim + 2) + ".cvs").map(io.Source.fromFile(_).getLines.map(_.split(",").map(_.toInt))).flatten.toArray;
    }
  }
  def collarSubdivision(n: Int): Unit = {
    i = 1;
    collarSubdivision = n;
  }

}
 