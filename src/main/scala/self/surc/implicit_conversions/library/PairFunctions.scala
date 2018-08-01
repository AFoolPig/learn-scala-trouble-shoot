package self.surc.implicit_conversions.library

class PairFunctions[K, V](k: K, v: V) {
  def reduceByKey(partitioner: Partitioner, func: (V, V) => V): RDD[(K, V)] = {
    new RDD[(K, V)]((k, v))
  }
}
