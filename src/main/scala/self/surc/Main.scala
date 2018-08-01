package self.surc

import self.surc.implicit_conversions.custom.PairWrapper
import self.surc.implicit_conversions.library.{LibFunction2, RDD}

/**
  * 仿照 Spark 源码，在 RDD 和 PairFunctions 的相应位置加入了 ClassTag 或者 ClassTag 类型的 implicit 字段
  * 从而导致需要对 implicit_conversions_example1 的代码进行进一步的修改。
  *
  * 本部分内容，主要主要涉及范畴就是 隐式参数。但是当 ClassTag 需求 跟 隐式参数 结合在一起时，问题标书不是很明显
  * 因此特地将这部分问题单独提出来，并提供问题排查方法。
  *
  * 因保留了导致错误的代码，所以本示例无法正确编译。请根据错误解析，尝试修改代码以通过编译。
  */

object Main {
  def main(args: Array[String]): Unit = {
    val rdd = new RDD[(String, Int)](("value", 1))
    val pairWrapper = new PairWrapper[String, Int](rdd)
    //noinspection ConvertExpressionToSAM
    pairWrapper.reduceByKey(new LibFunction2[Int, Int, Int] {
      override def call(p1: Int, p2: Int): Int = {
        p1 + p2
      }
    })
  }
}
