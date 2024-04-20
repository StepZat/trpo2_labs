trait UserType [T] {
  def typeName: String

  def create: T

  def clone(obj: T): T

  def readValue(input: String): T

  def parseValue(input: String): T

  def getTypeComparator: Ordering[T]

}
