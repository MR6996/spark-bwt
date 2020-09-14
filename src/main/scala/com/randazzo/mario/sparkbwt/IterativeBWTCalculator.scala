package com.randazzo.mario.sparkbwt

import com.randazzo.mario.sparkbwt.io.{BWTInputFormat, BWTOutputFormat}
import org.apache.hadoop.io.{ByteWritable, NullWritable}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

class IterativeBWTCalculator(session: SparkSession,
                             verbose: Boolean,
                             inputFilePath: String,
                             outputFilePath: String)
    extends BWTCalculator(session, verbose, inputFilePath, outputFilePath) {

    val input: RDD[(Long, Byte)] = sc
        .hadoopFile(
            inputFilePath,
            classOf[BWTInputFormat], classOf[NullWritable], classOf[ByteWritable],
            sc.defaultParallelism
        )
        .zipWithIndex
        .map { case ((_, c), i) => (i, c.get) }
        .cache

    override def calculate(): Unit = {
        val n: Long = input.count

        val sa: RDD[(Long, Long)] = new SuffixArray(session, input, n).get

        val previousChar: RDD[(Long, Byte)] = input.map { case (i, c) => (if (i < n - 1) i + 1 else 0, c) }

        val bwt: RDD[(Long, Byte)] = sa.join(previousChar).values.sortByKey()

        bwt.map { case (_, c) => (NullWritable.get(), new ByteWritable(c)) }
            .saveAsHadoopFile[BWTOutputFormat](outputFilePath)
    }
}
