package org.apache.spark.streaming

import java.text.SimpleDateFormat
import java.util.concurrent.atomic.AtomicInteger

import com.wplay.spark.streaming.core.WplayConfig
import org.apache.spark.streaming.scheduler._

import scala.collection.mutable
import scala.language.postfixOps

/**
  * Created by james on 2017/6/6.
  *
  * 拥堵监控
  */
class CongestionMonitorListener(ssc: StreamingContext) extends StreamingListener with WplayConfig {

  private lazy val sc = ssc.sparkContext
  private lazy val conf = sc.getConf

  private lazy val batchInfos = new mutable.Queue[BatchInfo]()
  var lastCompletedBatchInfo: Option[BatchInfo] = None

  // 活跃批处理基数
  val activeBatchCounter = new AtomicInteger(0)
  var isAlerted = false
  // 最后一次告警时间
  var lastAlertTime = ""

  private lazy val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  private lazy val name = sc.appName
  private lazy val master = sc.master
  private lazy val batchDuration = ssc.graph.batchDuration.milliseconds
  private lazy val appId = sc.applicationId

  // 钉钉发送接口
  private lazy val sendApi = conf.getOption("spark.monitor.congestion.send.api")
    .getOrElse(config.getString("spark.monitor.congestion.send.api"))
  private lazy val batch = conf.getInt("spark.monitor.congestion.batch", 0)
  // 拥堵多少批次自杀
  private lazy val suicide = conf.getInt("spark.monitor.suicide.batch", 0)
  private lazy val sendToEmail = conf.getOption("spark.monitor.congestion.email.to")
  private lazy val sendToDing = conf.getOption("spark.monitor.congestion.ding.to")


  // 是否启用告警
  private def isEnableAlert: Boolean = batch > 0 && sendToDing.isDefined

  // 拥堵发生
  private def getBad: Boolean = activeBatchCounter.get() >= batch && !isAlerted

  // 拥堵消除
  private def getWell: Boolean = lastCompletedBatchInfo match {
    case Some(bi) => bi.schedulingDelay.get == 0 && activeBatchCounter.get() < batch && isAlerted
    case None => activeBatchCounter.get() < batch && isAlerted
  }


  override def onBatchSubmitted(batchSubmitted: StreamingListenerBatchSubmitted): Unit = {

    val counter = activeBatchCounter.incrementAndGet()

    val batchInfo = batchSubmitted.batchInfo

    batchInfos.enqueue(batchInfo)

    val time = sdf.format(batchInfo.batchTime.milliseconds)


    /**
      * 开启自杀模式
      */
    if (suicide > 0 && counter >= suicide) {

      if (sendToDing.isDefined) {
        val message =
          s"""
             |$name 自杀了~~~~(>_<)~~~~
             |自杀时间:$time
             |App  Id:$appId
             |执行频度:$batchDuration
             |拥堵情况:$counter
             |拥堵数据:${batchInfos.map(_.numRecords).sum}
        """.stripMargin
        import notice._
        send a Ding(sendApi, sendToDing.get, message)
      }

      import scala.sys.process._

      val cmd = s"yarn application -kill $appId"
      val result = cmd !!
    }

    /**
      * 启用告警模式
      */
    if (isEnableAlert) {

      val (schedulingDelay, processingDelay): (Long, Long) = lastCompletedBatchInfo match {
        case Some(bi) => (bi.schedulingDelay.getOrElse(0), bi.processingDelay.getOrElse(0))
        case None => (System.currentTimeMillis() - batchInfo.batchTime.milliseconds, System.currentTimeMillis() - batchInfo.batchTime.milliseconds)
      }

      if (getBad) {
        isAlerted = true
        lastAlertTime = time
        val message =
          s"""
             |$name 拥堵告警
             |告警时间:$time
             |App  Id:$appId
             |执行频度:$batchDuration
             |拥堵情况:$counter
             |拥堵数据:${batchInfos.map(_.numRecords).sum}
             |调度延时:$schedulingDelay
             |执行延时:$processingDelay
        """.stripMargin
        import notice._
        send a Ding(sendApi, sendToDing.get, message)
      } else if (getWell) {
        isAlerted = false
        val message =
          s"""
             |$name 已经恢复运行
             |拥堵时间:[$lastAlertTime ~ $time]
        """.stripMargin
        import notice._
        send a Ding(sendApi, sendToDing.get, message)
      }
    }
  }


  override def onBatchCompleted(batchCompleted: StreamingListenerBatchCompleted): Unit = {
    activeBatchCounter.decrementAndGet()
    batchInfos.dequeue()
    lastCompletedBatchInfo = Some(batchCompleted.batchInfo)
  }
}
