package com.randazzo.mario.sparkbwt

import java.io.{BufferedWriter, FileWriter}

import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast

import scala.io.Source

class BWT (sc: SparkContext,
           r : Int,
           k : Int,
           startIdx : Int,
           endIdx : Int,
           inputFilePath : String,
           outputFilePath : String) {

    var bS : Broadcast[String] = _
    var primitives : BWTPrimitives = _

    def run()  {
        var start = System.currentTimeMillis()

        //Get the input string from input file and broadcasts it.
        val source = Source.fromFile(inputFilePath)
            val s = source.getLines.next().substring(startIdx, endIdx) + "\0"
        source.close()
        bS = sc.broadcast(s)

        primitives = new BWTPrimitives(r, k, bS)

        val mappedSuffix = sc.parallelize(0 until bS.value.length)
          .map( primitives.map )
          .groupByKey()
          .map( primitives.reduce )
          .sortByKey(ascending = true)

        var totalTime = System.currentTimeMillis() - start
        println("Elapsed time: " + totalTime / 1000.0 + " Secs")


        start = System.currentTimeMillis()

        //Write to output file the result of BWT
        val outStream = new BufferedWriter(new FileWriter(outputFilePath))
        mappedSuffix.collect().foreach(t =>
            for (idx <- t._2) {
              if (idx > 0) outStream.write(bS.value.charAt(idx - 1))
              else outStream.write(bS.value.charAt(bS.value.length - 1))
            }
        )
        outStream.close()

        totalTime = System.currentTimeMillis() - start
        println("Elapsed time: " + totalTime / 1000.0 + " Secs")

        sc.stop()
    }


}