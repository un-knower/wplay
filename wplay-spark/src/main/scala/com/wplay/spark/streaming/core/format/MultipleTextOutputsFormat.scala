package com.wplay.spark.streaming.core.format

import org.apache.hadoop.io.{NullWritable, Text}
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat

/**
  * Created by james on 2017/5/31.
  */
class MultipleTextOutputsFormat extends MultipleOutputsFormat(new TextOutputFormat[NullWritable, Text]) {}
