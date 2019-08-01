package com.randazzo.mario.sparkbwt

import java.io.BufferedWriter
import java.io.FileWriter

object Test {
  
  def main(args: Array[String]) {
    val bwt = new BWT(4, 3)

    
    bwt.run(getClass.getResource("/asd.txt").getPath)
    
    
  }
  
}