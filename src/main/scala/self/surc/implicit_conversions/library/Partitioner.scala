package self.surc.implicit_conversions.library

class Partitioner() {

}

object Partitioner {
  def defaultPartitioner(rdd:RDD[_]) : Partitioner = new Partitioner
}
