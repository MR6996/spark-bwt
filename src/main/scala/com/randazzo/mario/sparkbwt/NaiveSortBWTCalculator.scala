package com.randazzo.mario.sparkbwt

import org.apache.spark.sql.SparkSession

class NaiveSortBWTCalculator(session: SparkSession,
                             verbose: Boolean,
                             inputFilePath: String,
                             outputFilePath: String,
                             k: Int)
    extends NaiveBWTCalculator(session, verbose, inputFilePath, outputFilePath, k) {

    override def getSortPartitionMethod: Iterator[Int] => Iterator[Int] = primitives.sortPartition
}
