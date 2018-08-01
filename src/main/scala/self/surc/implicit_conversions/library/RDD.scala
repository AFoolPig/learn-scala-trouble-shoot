package self.surc.implicit_conversions.library

import scala.language.implicitConversions

class RDD[T](val v: T) {
  def rddPrintln(): Unit = {
    println(v)
  }
}

object RDD {
  implicit def rdd2PairFunctions[K, V](rdd: RDD[(K, V)]): PairFunctions[K, V] = new PairFunctions[K, V](rdd.v._1, rdd.v._2)
}