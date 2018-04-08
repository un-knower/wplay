package com.wplay.spark.streaming.core.sinks

import org.apache.spark.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{StreamingContext, Time}

/**
  * Created by james on 16/8/2.
  */
trait Sink[T] extends Serializable with Logging {

  @transient
  val ssc: StreamingContext
  @transient
  lazy val sparkConf = ssc.sparkContext.getConf

  /**
    * 输出
    *
    */
  def output(dStream: DStream[T]): Unit = {
    dStream.foreachRDD((rdd, time) => output(rdd, time))
  }

  /**
    * 输出
    *
    * @param rdd
    * @param time
    */
  def output(rdd: RDD[T], time: Time)
}
