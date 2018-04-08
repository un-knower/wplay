package org.apache.spark.streaming

//
import java.text.SimpleDateFormat
import java.util.{Properties, UUID}

import _root_.kafka.producer.KeyedMessage
import com.wplay.spark.kafka.KafkaWriter._
import com.wplay.spark.streaming.core.WplayConfig
import org.apache.spark.streaming.kafka.OffsetRange
import org.apache.spark.streaming.scheduler.{BatchInfo, StreamingListener, StreamingListenerBatchCompleted, StreamingListenerBatchStarted}

import scala.collection.mutable


/**
  * Created by james on 16/9/8.
  */
class InkeStatsReportListener(ssc: StreamingContext) extends StreamingListener with WplayConfig {


  // Queue containing latest completed batches
  private val batchInfos = new mutable.Queue[BatchInfo]()

  private val producerConf = new Properties()
  producerConf.put("serializer.class", "kafka.serializer.DefaultEncoder")
  producerConf.put("key.serializer.class", "kafka.serializer.StringEncoder")
  producerConf.put("metadata.broker.list",
    ssc.conf.getOption("spark.monitor.kafka.metadata.broker.list")
      .getOrElse(config.getString("spark.monitor.kafka.metadata.broker.list")))


  private val sinkTopic = ssc.conf.getOption("spark.monitor.kafka.topic")
    .getOrElse(config.getString("spark.monitor.kafka.topic"))

  private val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  private val name = ssc.sparkContext.appName
  private val master = ssc.sparkContext.master
  private val batchDuration = ssc.graph.batchDuration.milliseconds
  private val appId = ssc.sparkContext.applicationId

  /**
    * 批处理计算完成
    *
    * @param batchStarted
    */
  override def onBatchCompleted(batchStarted: StreamingListenerBatchCompleted): Unit = {


    val batchInfo = batchStarted.batchInfo

    val offsetRanges = batchInfo.streamIdToInputInfo.values.flatMap(_.metadata("offsets").asInstanceOf[List[OffsetRange]])

    val offsetMap = mutable.Map.empty[String, Long]

    for (or <- offsetRanges) {
      offsetMap.put(or.topic, offsetMap.getOrElse(or.topic, 0L) + (or.untilOffset - or.fromOffset))
    }

    val batchTime = batchStarted.batchInfo.batchTime.milliseconds
    val schedulingDelay = batchStarted.batchInfo.schedulingDelay.getOrElse(0L)
    val processingDelay = batchStarted.batchInfo.processingDelay.getOrElse(0L)

    val time = sdf.format(batchTime)
    val sparkAppInfos = offsetMap.map {
      case (topic, numRecords) =>
        SparkAppInfo(name, master, batchDuration, appId, topic, time, numRecords, schedulingDelay, processingDelay, "completed")
    }
    sparkAppInfos.iterator
      .writeToKafka(producerConf, x => new KeyedMessage[String, Array[Byte]](sinkTopic, UUID.randomUUID().toString, x.toString.getBytes))


  }

  /**
    * 批处理计算开始
    *
    * @param batchSubmitted
    */
  override def onBatchStarted(batchSubmitted: StreamingListenerBatchStarted): Unit = {

    val batchInfo = batchSubmitted.batchInfo

    val offsetRanges = batchInfo.streamIdToInputInfo.values.flatMap(_.metadata("offsets").asInstanceOf[List[OffsetRange]])

    val offsetMap = mutable.Map.empty[String, Long]

    for (or <- offsetRanges) {
      offsetMap.put(or.topic, offsetMap.getOrElse(or.topic, 0L) + (or.untilOffset - or.fromOffset))
    }

    val batchTime = batchInfo.batchTime.milliseconds
    val schedulingDelay = batchInfo.schedulingDelay.getOrElse(0L)
    val time = sdf.format(batchTime)
    val sparkAppInfos = offsetMap.map {
      case (topic, numRecords) =>
        SparkAppInfo(name, master, batchDuration, appId, topic, time, numRecords, schedulingDelay, 0L, "started")
    }
    sparkAppInfos.iterator
      .writeToKafka(producerConf, x => new KeyedMessage[String, Array[Byte]](sinkTopic, UUID.randomUUID().toString, x.toString.getBytes))

  }
}

/**
  *
  * @param name
  * @param master
  * @param batchDuration
  * @param appId
  * @param topic
  * @param batchTime
  * @param numRecords
  * @param schedulingDelay
  * @param startedOrCompleted
  */
case class SparkAppInfo(name: String, master: String, batchDuration: Long, appId: String, topic: String, batchTime: String, numRecords: Long, schedulingDelay: Long, processingDelay: Long, startedOrCompleted: String) {
  override def toString: String = {
    s"""{"name":"$name","master":"$master","batchDuration":$batchDuration,"appId":"$appId","topic":"$topic","batchTime":"$batchTime","numRecords":$numRecords,"schedulingDelay":$schedulingDelay,"processingDelay":$processingDelay,"startedOrCompleted":"$startedOrCompleted"}"""
  }
}



