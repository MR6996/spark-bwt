package com.randazzo.mario.sparkbwt

import java.io.BufferedWriter
import java.io.FileWriter

import scala.io.Source
import scala.language.implicitConversions
import scala.math.min

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast

import com.randazzo.mario.sparkbwt.jni.SAPartial

import breeze.linalg.linspace
import breeze.numerics.pow
import breeze.numerics.round

/**
 *
 *
 * @author Mario Randazzo
 *
 */
class BWT(r : Int, k : Int) extends Serializable {

    var bS : Broadcast[String] = null
    val alphaSize = 4
    val maxValue = calcMaxValue()
    val alpha = Map('A' -> 0, 'C' -> 1, 'G' -> 2, 'T' -> 3)
    val ranges = round(linspace(0, maxValue, r)).toArray

    implicit def int2Integer(v : Array[Int]) : Array[java.lang.Integer] = {
        var tmp = new Array[java.lang.Integer](v.size)

        for (i <- 0 to v.size - 1)
            tmp(i) = java.lang.Integer.valueOf(v(i))

        return tmp
    }

    /**
     *
     *
     * @return the maximum value of a k-mer
     */
    def calcMaxValue() : Long = {
        var max : Long = 0
        for (i <- k - 1 to 0 by -1)
            max = max + (alphaSize - 1) * pow(alphaSize, i).toLong

        return max;
    }

    /**
     *	
     *
     * @param i index of the k-mer in s
     * @return the value of i-th k-mer in {0,...,r-1} 
     */
    def toRange(i : Int) : Long = {
        var value : Long = 0

        for ((j, l) <- (k - 1 to 0 by -1) zip (i to min(i + k - 1, bS.value.length - 1)))
            value = value + alpha(bS.value.charAt(l)) * pow(alphaSize, j).toLong

        return round(value / maxValue.toDouble * (r - 1))
    }

    /**
     *
     *
     * @param inputFilePath the path of the file that contains input string.
     */
    def run(inputFilePath : String) {

        val conf = new SparkConf().setAppName("BWT").setMaster("local[*]")

        //Get the spark context
        //val sc = SparkContext.getOrCreate()
        val sc = new SparkContext(conf)
        sc.setLogLevel("ERROR")

        var start = System.currentTimeMillis()

        //Get the input string from input file and broadcasts it.
        val s = Source.fromFile(inputFilePath).getLines.next()
        bS = sc.broadcast(s)

        val mappedSuffix = sc.parallelize(0 to bS.value.size - 1)
            .map(idx => ((toRange(idx), idx)))
            .groupByKey(4)
            //.map({ case (k, iter) => ((k, iter.toList.sortWith(bS.value.substring(_) < bS.value.substring(_)))) })
            .map({ case (k, iter) => ((k, SAPartial.calculatePartialSA(bS.value, iter.toArray.sorted, 256))) })
            .sortByKey(true, 4)

        var totalTime = System.currentTimeMillis() - start;
        println("Elapsed time: " + totalTime / 1000.0 + " Secs")


        start = System.currentTimeMillis()

        //Write to output file the result of BWT
        val outFile = inputFilePath + ".bwt"
        val outStream = new BufferedWriter(new FileWriter(outFile))
        mappedSuffix.collect().foreach(t => (
            for (idx <- t._2) {
                if (idx > 0) outStream.write(bS.value.charAt(idx - 1))
                else outStream.write(bS.value.charAt(bS.value.size - 1))
            }))
        outStream.close()

        totalTime = System.currentTimeMillis() - start;
        println("Elapsed time: " + totalTime / 1000.0 + " Secs")

        println("Saved on: " + outFile)

        sc.stop()
    }
}