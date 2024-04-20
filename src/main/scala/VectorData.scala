class VectorData[T](implicit ord: Ordering[T]) {
  private var elements: List[T] = List.empty[T]

  def add(element: T): Unit = {
    elements = elements :+ element
  }

  def get(index: Int): Option[T] = elements.lift(index)

  def insert(index: Int, element: T): Unit = {
    val (before, after) = elements.splitAt(index)
    elements = before ++ (element +: after)
  }

  def remove(index: Int): Option[T] = {
    if (index >= 0 && index < elements.length) {
      val (before, after) = elements.splitAt(index)
      elements = before ++ after.tail
      after.headOption
    } else None
  }

  def forEach(action: T => Unit): Unit = {
    elements.foreach(action)
  }

  def sort(): Unit = {
    elements = elements.sorted(ord)
  }

  def size: Int = elements.length

  def allElements: List[T] = elements
}