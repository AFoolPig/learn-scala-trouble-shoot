package self.surc

import self.surc.implicit_conversions.custom.PairWrapper
import self.surc.implicit_conversions.library.{LibFunction2, RDD}

/**
  * 隐式转换
  * 在类型不匹配的时候，可能触发隐式转换，具体可归结为如下两种可能情况：
  * 1 赋值时 t : T = obj
  * --如果 obj 的类型是 S，却使用 obj 为 类型 T 的量 t 赋值，而且不满足 S 继承 T 的情况
  * --此处赋值含义广泛，除普通赋值外，还包括函数入口（传入参数）出口（返回值）这类相当于赋值的情况
  * 2 函数调用时 obj.func()
  * --如果 obj 的类型 S 未定义 func 方法（无相同的重载，后面将为关于重载情况下的具体分析提供额外的例子）
  *
  * 可用的隐式函数
  * 当发生以上两种情况时，Scala 编译器在 编译过程 中会寻找 可用的隐式函数 来完成隐式转换。对于上述两种情况，可用的隐式函数的含义分别为：
  * 情况1下，可用的隐式函数为 implitic def cast(S) : T
  * 情况2下，可用的隐式函数为 implitic def cast(S) : T 且 T 中定义了 func 方法
  *
  * 寻找范围：
  * Scala 编译器仅会在特定的范围内寻找隐式函数：
  * 1 源类型 S 的伴生对象中
  * 2 目标类型 T 的伴生对象中
  * 3 所有 import 覆盖范围中的可见范围
  *
  * 隐式函数编写规则：
  * implitic def cast(S) : T
  * 关键字 implitic 表示本函数为隐式函数，除支持所有普通函数的用法外，还可被选用执行隐式转换
  * 仅有一个传入参数，函数需编写将 源类型 S 的对像 转换成 目标类型 T 的对象 的操作。
  * 简单来说，就是要实现 implitic S => T
  *
  * 本例中，将会对隐式转换的执行进行简单的分析。具体请查看 PairWrapper 类。
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
