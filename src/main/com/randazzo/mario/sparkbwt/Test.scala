package com.randazzo.mario.sparkbwt

import java.io.BufferedWriter
import java.io.FileWriter

object Test {
  
  def main(args: Array[String]) {
    val bwt = new BWT()

    val l = List(5, 5,25,50,75,100)//,125,150,175,200)
    
    for(k <- 0 to 10) {
      val testStream = new BufferedWriter(new FileWriter("times"+k+".txt")) 
      
      for(i <- l) {
        bwt.basic(getClass.getResource("ecoli"+i+"k.txt").getPath.replace("%20","-"), testStream)
      }
      testStream.close()
    }
    
    
  }
  
}