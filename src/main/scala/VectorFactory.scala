object VectorFactory {
  private var types: Map[String, VectorType] = Map(
    "Polar Vector" -> new PolarVectorType,
    "Cartesian Vector" -> new CartesianVectorType
  )

  def registerType(name: String, vectorType: VectorType): Unit = {
    types += (name -> vectorType)
  }

  def createVector(typeName: String): Vector = {
    types(typeName).create
  }
}