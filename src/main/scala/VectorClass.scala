//sealed trait Vector {
//  def keyRepresentation: String
//  def valueRepresentation: String
//}

case class PolarVector(length: Double, angle: Double) extends Vector {
  def keyRepresentation: String = s"Polar: Length=$length, Angle=$angle"
  def valueRepresentation: String = s"($length, $angle)"
}

case class CartesianVector(x: Double, y: Double) extends Vector {
  def keyRepresentation: String = s"Cartesian: X=$x, Y=$y"
  def valueRepresentation: String = s"($x, $y)"
}

class PolarVectorType extends VectorType {
  def create: Vector = PolarVector(0, 0)
  def clone(vector: Vector): Vector = vector.asInstanceOf[PolarVector].copy()
  def parse(s: String): PolarVector = {
    val parts = s.split(",")
    PolarVector(parts(0).toDouble, parts(1).toDouble)
  }
  def typeName: String = "Polar Vector"
}

class CartesianVectorType extends VectorType {
  def create: Vector = CartesianVector(0, 0)
  def clone(vector: Vector): Vector = vector.asInstanceOf[CartesianVector].copy()
  def parse(s: String): CartesianVector = {
    val parts = s.split(",")
    CartesianVector(parts(0).toDouble, parts(1).toDouble)
  }
  def typeName: String = "Cartesian Vector"
}

