package com.wplay.spark.streaming.core.sinks

import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{StreamingContext, Time}

/**
  * Created by james on 16/8/4.
  */
class ShowSink[T](val ssc: StreamingContext) extends Sink[T] {


  /**
    * 输出
    *
    */
  override def output(rdd: RDD[T], time: Time = Time(System.currentTimeMillis())): Unit = {
    val firstNum = rdd.take(10 + 1)
    println("-------------------------------------------")
    println("Time: " + time)
    println("-------------------------------------------")
    firstNum.take(10).foreach(println)
    if (firstNum.length > 10) println("...")
    println()
  }
}
