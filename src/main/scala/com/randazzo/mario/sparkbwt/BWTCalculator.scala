package com.randazzo.mario.sparkbwt

import org.apache.commons.logging.{Log, LogFactory}
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

abstract class BWTCalculator(session: SparkSession,
                             verbose: Boolean,
                             inputFilePath: String,
                             outputFilePath: String) extends Serializable {

    val LOG: Log = LogFactory.getLog(getClass)
    val sc: SparkContext = session.sparkContext
    if (!verbose) sc.setLogLevel("Error")


    def run() {
        val start: Long = System.currentTimeMillis()
        calculate();
        LOG.info("Elapsed time: " + (System.currentTimeMillis() - start) + "ms")
    }

    def calculate()
}
