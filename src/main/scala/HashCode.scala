import scala.collection.mutable

class HashTable[K, V <: Vector] {
  private var initCapacity = 5
  private var numBuckets = initCapacity
  private var buckets: Array[mutable.ListBuffer[HashNode[K, V]]] = Array.fill(numBuckets)(mutable.ListBuffer())
  var onRehash: () => Unit = () => {}  // Callback для обновления GUI

  private def getBucketIndex(key: K): Int = key.hashCode() % numBuckets

  def get(key: K): Option[V] = {
    buckets(getBucketIndex(key)).find(_.key == key).map(_.value)
  }

  def put(key: K, value: V): Unit = {
    val index = getBucketIndex(key)
    val bucket = buckets(index)
    bucket.find(_.key == key) match {
      case Some(node) => node.value = value
      case None =>
        bucket += HashNode(key, value)
        if ((size.toDouble / numBuckets) >= 0.7) rehash()
    }
  }

  def rehash(): Unit = {
    val oldBuckets = buckets
    numBuckets *= 2
    buckets = Array.fill(numBuckets)(mutable.ListBuffer())
    oldBuckets.flatten.foreach(node => put(node.key, node.value))
    onRehash() // Уведомляем о перехешировании
  }

  def size: Int = buckets.map(_.size).sum

  def getBucketData: Seq[(Int, Seq[HashNode[K, V]])] = {
    buckets.zipWithIndex.map { case (bucket, index) =>
      (index, bucket.toSeq)
    }.toSeq
  }

  def remove(key: K): Option[V] = {
    val index = getBucketIndex(key)
    val bucket = buckets(index)
    val nodeIndex = bucket.indexWhere(_.key == key)
    if (nodeIndex != -1) {
      val node = bucket(nodeIndex)
      bucket.remove(nodeIndex)
      Some(node.value)
    } else None
  }

  def clear(): Unit = {
    buckets = Array.fill(initCapacity)(mutable.ListBuffer())
    numBuckets = initCapacity  // Сброс до начального количества бакетов
  }
}
