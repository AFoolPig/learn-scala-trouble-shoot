package self.surc.implicit_conversions.custom

import self.surc.implicit_conversions.library.{LibFunction2, Partitioner, RDD}


import scala.language.implicitConversions


class PairWrapper[K, V](rdd: RDD[(K, V)]) {
  def reduceByKey(func: LibFunction2[V, V, V]): PairWrapper[K, V] = {
    // 这不是一个隐式函数
    import self.surc.implicit_conversions.library.Partitioner.defaultPartitioner

    // 所以需要显示的调用它来进行类型转换，因为没有 implicit 关键字声明
    reduceByKey(defaultPartitioner(rdd), func)
  }

  def reduceByKey(partitioner: Partitioner, func: LibFunction2[V, V, V]): PairWrapper[K, V] = {
    // 这是一个隐式函数
    import PairWrapper.libFunction2ToLambda

    // 这里发生了两个隐式转换
    //
    // 转换1 对 rdd 的转换：RDD => PairFunctions
    // 触发原因：源类型 RDD 下，不存在函数 reduceByKey
    // 在 源类型 RDD 的伴生对象中 找到可用的隐式函数 rdd2PairFunctions，因为 PairFunctions 下有所需方法 reduceByKey
    // 可用的隐式函数位于 源类型的伴生对象，无需额外使用 import 引入。
    //
    // 转换2 对 func 的转换：LibFunction2[V, V, V] => lambda表达式 (V, V) => V
    // 触发原因：PairFunction 下的函数 reduceByKey 的第二个参数所需类型是 Scala lambda 表达式 (V, V) => V，
    // --但传入的是 LibFunction2[V, V, V] 类型的对象
    // 在 源类型 LibFunction2[V, V, V] 和 目标类型 Scala lambda 表达式 (V, V) => V 中，未找到可用的隐式函数
    // 因此需要显式地 import 所需隐式函数 PairWrapper.libFunction2ToLambda
    // 因为在 import 覆盖范围中找到了可用的隐式函数，可以进行对 func 的隐式转换

    val result = rdd.reduceByKey(partitioner, func)

    // 这里发生了隐式转换
    // 对 result 的转换：RDD => PairWrapper
    // 触发原因：上面的执行结果，result 的类型是 RDD （根据函数返回值类型自动匹配），但本函数返回值类型为 PairWrapper
    // 在 目标类型 PairWrapper 的伴生对象中 找到可用的隐式函数 rdd2PairWrapper
    // 可用的隐式函数位于 目标类型的伴生对象，无需额外使用 import 引入。
    result
  }
}

object PairWrapper {
  implicit def rdd2PairWrapper[K, V](rdd: RDD[(K, V)]): PairWrapper[K, V] = new PairWrapper[K, V](rdd)

  implicit def libFunction2ToLambda[P1, P2, R](func: LibFunction2[P1, P2, R]): (P1, P2) => R = (x1, x2) => func.call(x1, x2)
}
