package self.surc.implicit_conversions.custom

import self.surc.implicit_conversions.library.{LibFunction2, RDD}
import PairWrapper.libFunction2ToLambda
import self.surc.implicit_conversions.library.RDD.rdd2PairFunctions

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * 相对于 implicit_conversions_example1 中的代码，本代码删除了前一部分讲解内容的注释，并且将本类中原来的两个 reduceByKey 函数
  * 合并成了一个。
  *
  * 以上修改后，程序仍可正常编译运行。
  *
  * 继续修改：为 RDD 的泛型类型 添加 ClassTag 定界，为 PairWrapper 添加 构造函数的隐式参数
  * --以及 为 RDD 伴生对象中 隐式函数 rdd2PairFunctions 的泛型类型 添加 ClassTag 定界
  *
  * 于是，代码就跑不了了。实际上，这是一个缺少隐式参数的错误。但是编译错误提示却并非如此。
  */

class PairWrapper[K, V](rdd: RDD[(K, V)]) {
  /**
    * 为了后面方便，这里定义 fakeClassTag
    */
  def fakeClassTag[U]: ClassTag[U] = ClassTag.AnyRef.asInstanceOf[ClassTag[U]]

  /**
    * 错误位置：指向本函数中那条语句
    * 错误信息：value reduceByKey is not a member of self.surc.implicit_conversions.library.RDD[(K, V)]
    * --翻译过来就是 RDD 中没有 reduceByKey 这个名字。
    * 根据 implicit_conversions_example1 中知道的内容，reduceByKey 确实不是 RDD 中的名字，而是 PairWrapper 中的函数名
    * 这是一个提示，说明 隐式转换 失败了。
    *
    * 这里提供一个小技巧：为了定位隐式转换失败的原因，可以显式的调用隐式转换所使用的隐式函数，来定位问题所在。
    * 这里使用的原理是：忽略因伴生对象引用引起的错误外，如果直接显式调用隐式函数可完成转换，隐式转换就可以完成 或者
    * --因为多个可用的隐式函数导致失败
    *
    * 使用这个方法调试的过程如下
    */
  def reduceByKey(func: LibFunction2[V, V, V]): PairWrapper[K, V] = {
    /*
     * 直接显式调用隐式函数可完成转换:
     * import self.surc.implicit_conversions.library.RDD.rdd2PairFunctions
     * rdd2PairFunctions(rdd).reduceByKey(func)
     *
     * 用上面两行代码替换 报错语句 rdd.reduceByKey(func)，其中 rdd2PairFunctions 是选定用来执行 隐式转换 的 隐式函数
     * 发现编译错误变了，这次的两条错误就是缺少隐式参数的因上下文定界引起的典型错误提示。一条表明缺少相关定界，另一条表明缺少隐式参数。
     *
     * 放弃隐式转换的解决方案：直接在函数调用时添加所需的隐式参数
     * import self.surc.implicit_conversions.library.RDD.rdd2PairFunctions
     * rdd2PairFunctions(rdd)(fakeClassTag, fakeClassTag).reduceByKey(func)
     *
     * 使用 fakeClassTag 生成所需的隐式参数并传入。fakeClassTag 会根据需要，自动获取所需泛型类型。
     * 这样解决到了问题，只是不是隐式转换了。
     * 因为相比于所需的直接调用的写法 rdd2PairFunctions(rdd)，需要添加额外的隐式参数(fakeClassTag, fakeClassTag)
     * 才可以完成类型转换，因为不可以直接用 rdd2PairFunctions(rdd) 完成转换，所以不能执行隐式转换。
     *
     * 保持隐式转换的解决方案：
     * import self.surc.implicit_conversions.library.RDD.rdd2PairFunctions
     * implicit val kClassTag: ClassTag[K] = fakeClassTag
     * implicit val vClassTag: ClassTag[V] = fakeClassTag
     * rdd2PairFunctions(rdd).reduceByKey(func)
     *
     * 因为配合上面的两句 implicit，rdd2PairFunctions(rdd)可以完成类型转换，所以最后一行换成
     * rdd.reduceByKey(func)
     * 同样可以通过隐式转换完成编译。
     *
     * 以上提供的两种解决方案并不是所有的解决方案。能解决问题的代码改写方式至少还有4种。
     * 请注意，本问题的关键在于缺少调用隐式函数所需的隐式参数，并不是隐式转换本身机制的问题。
     * 所以一切可以解决缺少隐式参数问题的代码改写，都可以从某个角度解决本问题。这就会有很多种组合。
     * 值得注意的是，其中的一些方法，会将本问题的后遗症遗留给方法或类的使用者。
     * 这并不是严重的问题，但是根据需求进行解决方案选择时，这是需要考虑的一个方面。
     */
    rdd.reduceByKey(func)
  }
}

object PairWrapper {
  implicit def rdd2PairWrapper[K, V](rdd: RDD[(K, V)]): PairWrapper[K, V] = new PairWrapper[K, V](rdd)

  implicit def libFunction2ToLambda[P1, P2, R](func: LibFunction2[P1, P2, R]): (P1, P2) => R = (x1, x2) => func.call(x1, x2)
}
