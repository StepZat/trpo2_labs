trait VectorType {
  def create: Vector
  def clone(vector: Vector): Vector
  def parse(s: String): Vector
  def typeName: String
}
