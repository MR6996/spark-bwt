package com.randazzo.mario.sparkbwt

import com.randazzo.mario.sparkbwt.io.BWTOutputFormat
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.{Partitioner, RangePartitioner}

import scala.io.{BufferedSource, Source}

abstract class NaiveBWTCalculator(session: SparkSession,
                             verbose: Boolean,
                             inputFilePath: String,
                             outputFilePath: String,
                             k: Int)
    extends BWTCalculator(session, verbose, inputFilePath, outputFilePath) {

    var bS: Broadcast[String] = _
    var primitives: NaivePrimitives = _

    override def calculate(): Unit = {
        // Load and broadcast input string
        val source: BufferedSource = Source.fromFile(inputFilePath)
        val inputString: String = source.getLines().mkString
        val inputStringLength: Int = inputString.length

        bS = sc.broadcast(inputString)
        primitives = new NaivePrimitives(k, bS)

        val kmers: RDD[(String, Int)] = sc
            .parallelize(0 until inputStringLength)
            .map(primitives.getKMer)

        val partitioner: Partitioner = new RangePartitioner[String, Int](sc.defaultParallelism, kmers)

        val sortedIndices = kmers.partitionBy(partitioner)
            .map { case (_, i) => i }
            .mapPartitions(getSortPartitionMethod)

        sortedIndices.map(primitives.previous)
            .saveAsHadoopFile[BWTOutputFormat](outputFilePath)

        bS.destroy()
    }

    def getSortPartitionMethod: Iterator[Int] => Iterator[Int]
}

