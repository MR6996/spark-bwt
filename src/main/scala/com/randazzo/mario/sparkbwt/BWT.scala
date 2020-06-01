package com.randazzo.mario.sparkbwt

import com.randazzo.mario.sparkbwt.io.{BWTInputFormat, BWTOutputFormat}
import org.apache.commons.logging.{Log, LogFactory}
import org.apache.hadoop.io.{ByteWritable, NullWritable}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession


class BWT(session: SparkSession,
          verbose: Boolean,
          inputFilePath: String,
          outputFilePath: String) extends Serializable {

    val LOG: Log = LogFactory.getLog(getClass)
    val sc: SparkContext = session.sparkContext
    if (!verbose) sc.setLogLevel("Error")

    val input: RDD[(Long, Byte)] = sc
        .hadoopFile(
            inputFilePath,
            classOf[BWTInputFormat], classOf[NullWritable], classOf[ByteWritable],
            sc.defaultParallelism
        )
        .zipWithIndex
        .map { case ((_, c), i) => (i, c.get) }
        .cache


    /*val input: RDD[(Long, Byte)] = sc
        .hadoopFile(
            inputFilePath,
            classOf[TextInputFormat], classOf[LongWritable], classOf[Text],
            sc.defaultParallelism
        )
        .values
        .flatMap(_.toString.getBytes)
        .zipWithIndex
        .map(_.swap)
        .cache*/

    def run() {

        val start: Long = System.currentTimeMillis()

        val n: Long = input.count

        val sa: RDD[(Long, Long)] = new SuffixArray(session, input, n).get

        val previousChar: RDD[(Long, Byte)] = input.map { case (i, c) => (if (i < n - 1) i + 1 else 0, c) }

        val bwt: RDD[(Long, Byte)] = sa.join(previousChar).values.sortByKey()

        bwt.map { case (_, c) => (NullWritable.get(), new ByteWritable(c)) }
            .saveAsHadoopFile[BWTOutputFormat](outputFilePath)

        //bwt.values.mapPartitions(v => Iterator(v.mkString)).saveAsTextFile(outputFilePath)


        println("Elapsed time: " + (System.currentTimeMillis() - start) + "ms")
        LOG.info("Elapsed time: " + (System.currentTimeMillis() - start) + "ms")

        //scala.io.StdIn.readLine()

    }

}