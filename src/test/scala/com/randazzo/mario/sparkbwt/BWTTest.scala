package com.randazzo.mario.sparkbwt

import scala.collection.mutable
import scala.io.Source

object BWTTest {

    def main(args: Array[String]) {
        Seq("/test_genome.txt", "/ecoli_genome.txt").foreach( file => {
            val path = getClass.getResource(file).getFile
            val bwt = new BWTBuilder(path).build

            bwt.run()
            println("Checking inverse bwt...")
            println("Check [" + file + "]: " + checkBWT(path))
        })
    }

    def checkBWT(inputFilePath: String): Boolean = {
        var source = Source.fromFile(inputFilePath + ".bwt")
        val calculatedS = ibwt(source.getLines.next())
        source.close()

        source = Source.fromFile(inputFilePath)
        val s = source.getLines.next()
        source.close()

        s.equals(calculatedS)
    }

    def ibwt(bwt: String): String = {
        val c = mutable.HashMap[Char, Int]().withDefaultValue(0)
        val occ = new Array[Int](bwt.length)
        val lf = new Array[Int](bwt.length)

        for(i <- bwt.indices) {
            occ(i) = c(bwt(i))
            c(bwt(i)) += 1
        }
        val keyIt = c.keys.toBuffer.sorted.iterator
        val char = keyIt.next
        var prevCount = c(char)
        var nextCount = c(char)
        c(char) = 0
        while(keyIt.hasNext) {
            val nextChar = keyIt.next
            nextCount += c(nextChar)
            c(nextChar) = prevCount
            prevCount = nextCount
        }


        for(i <- bwt.indices)
            lf(i) = c(bwt(i)) + occ(i)

        val s = new StringBuilder
        var r = 0
        var currentChar = bwt(r)

        while(currentChar != '\0') {
            s.append(currentChar)
            r = lf(r)
            currentChar = bwt(r)
        }

        s.reverse.toString
    }

}