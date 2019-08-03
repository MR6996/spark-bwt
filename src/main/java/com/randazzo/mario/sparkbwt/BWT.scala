package com.randazzo.mario.sparkbwt

import java.io.{BufferedWriter, FileWriter}

import breeze.numerics.{pow, round}
import com.randazzo.mario.sparkbwt.jni.SAPartial
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast

import scala.io.Source
import scala.language.implicitConversions
import scala.math.min

/**
 *
 *
 * @author Mario Randazzo
 *
 */
class BWT(r : Int, k : Int) extends Serializable {

    val maxValue: Long = calcMaxValue()
    val alphaSize = 4
    val alpha: Map[Char, Int] = Map('A' -> 0, 'C' -> 1, 'G' -> 2, 'T' -> 3)
    var bS : Broadcast[String] = _

    implicit def int2Integer(v : Array[Int]) : Array[java.lang.Integer] = {
        val tmp = new Array[java.lang.Integer](v.length)

        for (i <- v.indices)
            tmp(i) = java.lang.Integer.valueOf(v(i))

        tmp
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

        max
    }

    /**
     *
     *
     * @param inputFilePath the path of the file that contains input string.
     */
    def run(inputFilePath : String) {

        //val conf = new SparkConf().setAppName("BWT").setMaster("local[*]")

        //Get the spark context
        val sc = SparkContext.getOrCreate()
        //val sc = new SparkContext(conf)
        //sc.setLogLevel("ERROR")

        var start = System.currentTimeMillis()

        //Get the input string from input file and broadcasts it.
        val source = Source.fromFile(inputFilePath)
        val s = source.getLines.next()
        source.close()
        bS = sc.broadcast(s)

        val mappedSuffix = sc.parallelize(0 until bS.value.length)
            .map( idx => (toRange(idx), idx) )
            .groupByKey()
            //.map({ case (k, iter) => ((k, iter.toList.sortWith(bS.value.substring(_) < bS.value.substring(_)))) })
            .map({ case (k, iter) => (k, SAPartial.calculatePartialSA(bS.value, iter.toArray.sorted, 256)) })
            .sortByKey(ascending = true)

        var totalTime = System.currentTimeMillis() - start
        println("Elapsed time: " + totalTime / 1000.0 + " Secs")


        start = System.currentTimeMillis()

        //Write to output file the result of BWT
        val outFile = inputFilePath + ".bwt"
        val outStream = new BufferedWriter(new FileWriter(outFile))
        mappedSuffix.collect().foreach(t => (
            for (idx <- t._2)
                if (idx > 0) outStream.write(bS.value.charAt(idx - 1))
                else outStream.write(bS.value.charAt(bS.value.length - 1))
            ))
        outStream.close()

        totalTime = System.currentTimeMillis() - start
        println("Elapsed time: " + totalTime / 1000.0 + " Secs")

        println("Saved on: " + outFile)

        sc.stop()
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

        round(value / maxValue.toDouble * (r - 1))
    }

}