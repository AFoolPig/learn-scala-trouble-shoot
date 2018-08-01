package self.surc.implicit_conversions.library

import scala.language.implicitConversions
import scala.reflect.ClassTag

class RDD[T: ClassTag](val v: T) {
  def rddPrintln(): Unit = {
    println(v)
  }
}

object RDD {
  implicit def rdd2PairFunctions[K: ClassTag, V: ClassTag](rdd: RDD[(K, V)]): PairFunctions[K, V] =
    new PairFunctions[K, V](rdd.v._1, rdd.v._2)
}