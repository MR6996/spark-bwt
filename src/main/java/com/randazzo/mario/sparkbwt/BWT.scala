package com.randazzo.mario.sparkbwt

import java.io.BufferedWriter
import java.io.FileWriter

import scala.math.min
import scala.io.Source
import scala.language.implicitConversions
import scala.collection.JavaConverters._

import breeze.linalg._
import breeze.numerics._

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

import util.SuffixArray


class BWT extends Serializable {
  
  val R = 4
  val K = 3
  val alphaSize = 4  
  val maxValue = calcMaxValue(alphaSize, K)
  val alpha = Map('a' -> 0, 'c' -> 1, 'g' -> 2, 't' -> 3)
  //val alpha = Map('$' -> 0, 'a' -> 1, 'b' -> 2)
  val ranges = round(linspace(0, maxValue, R)).toArray

  implicit def int2Integer(v: Array[Int]) : Array[java.lang.Integer] = {
    var tmp = new Array[java.lang.Integer](v.size)
    
    for(i <- 0 to v.size-1)
      tmp(i) = java.lang.Integer.valueOf(v(i))
    
    return tmp
  }
  
  def calcMaxValue(s: Int, k: Int):Int = {
    var max = 0
    for(i <- 0 to k-1)
      max += (s-1)*Math.pow(s, i).toInt
    
    return max;
  }
  
  def toRange(s: String) : Long = {
    var value = 0 

    for(i <- 0 to (s.length - 1)) 
      value = value + alpha(s.charAt(s.length - i - 1))*math.pow(alphaSize, i).toInt
      
    //println(s + " -> "+ value + " | " + value / maxValue.toDouble)      
    return round(value / maxValue.toDouble * (R-1))
  }

  def basic(input_file: String, testStream:BufferedWriter) {
    
    val conf = new SparkConf().setAppName("Testing").setMaster("local[*]")
    
      //Get the spark context
      //val sc = SparkContext.getOrCreate()
      val sc = new SparkContext(conf)
      sc.setLogLevel("ERROR")
    
      var start = System.currentTimeMillis()
      
      //Get the input string from input file and broadcasts it.
      val s = Source.fromFile(input_file).getLines.next()
      val bS = sc.broadcast(s)
      
      //bS.value.charAt((idx + s.size - 1) % s.size)
      val mappedSuffix= sc.parallelize(0 to bS.value.size - 1)
        .map( idx => ( (toRange(bS.value.substring(idx, min(idx+K, bS.value.size)).toLowerCase), idx))) 
        .groupByKey(4)
        .map( { case (k,iter) => ((k, iter.toList.sortWith(bS.value.substring(_) < bS.value.substring(_)))) } )
        //.map( { case (k,iter) => ((k, SuffixArray.createPartialSuffixArray(bS.value, iter.toArray.sorted))) } )
        .sortByKey(true, 4)
        
      var totalTime = System.currentTimeMillis() - start;
      testStream.append(totalTime.toString + ", ");
      println("Elapsed time: " + totalTime/1000.0 + " Secs") 

      //mappedSuffix.collect().foreach( { case (k,l) => (println(k + "|" + l))})
      
      start = System.currentTimeMillis()
    
      //Write to output file the result of BWT
      val outFile = input_file + ".bwt"
      val outStream = new BufferedWriter(new FileWriter(outFile)) 
      mappedSuffix.collect().foreach( t => ( 
          for(idx <- t._2) {
              if(idx > 0) outStream.write(bS.value.charAt(idx-1))
              else outStream.write(bS.value.charAt(bS.value.size - 1))
          }
        )
      )
      outStream.close() 
      
      totalTime = System.currentTimeMillis() - start;
      testStream.append(totalTime.toString);
      testStream.newLine()
      println("Elapsed time: " + totalTime/1000.0 + " Secs") 
      
      println("Saved on: " + outFile)  

      sc.stop()
    
  }
}