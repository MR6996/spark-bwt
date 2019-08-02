package com.randazzo.mario.sparkbwt

object Test {
  
  def main(args: Array[String]) {
    val bwt = new BWT(4, 3)

    
    bwt.run(getClass.getResource("/ecoli_genome.txt").getPath)
    
    
  }
  
}