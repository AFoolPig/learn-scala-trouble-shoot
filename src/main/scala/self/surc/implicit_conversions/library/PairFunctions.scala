package self.surc.implicit_conversions.library

import self.surc.implicit_conversions.library.Partitioner.defaultPartitioner

import scala.reflect.ClassTag

class PairFunctions[K, V](k: K, v: V)(implicit kt: ClassTag[K], vt: ClassTag[V]) {
  def reduceByKey(partitioner: Partitioner, func: (V, V) => V): RDD[(K, V)] = {
    new RDD[(K, V)]((k, v))
  }

  def reduceByKey(func: (V, V) => V): RDD[(K, V)] = {
    reduceByKey(defaultPartitioner(new RDD[(K, V)]((k, v))), func)
  }
}
