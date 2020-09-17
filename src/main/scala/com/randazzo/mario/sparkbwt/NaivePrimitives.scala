package com.randazzo.mario.sparkbwt

import com.randazzo.mario.sparkbwt.jni.PartialSorting
import org.apache.hadoop.io.{ByteWritable, NullWritable}
import org.apache.spark.broadcast.Broadcast

import scala.math.min


class NaivePrimitives(k: Int, bS: Broadcast[String]) extends Serializable {

    def getKMer: Int => (String, Int) =
        i => (bS.value.substring(i, min(i + k, bS.value.length - 1)), i)


    def previous: Int => (NullWritable, ByteWritable) =
        i => {
            val c = if (i > 0) bS.value.charAt(i - 1)
            else bS.value.charAt(bS.value.length - 1)

            (NullWritable.get(), new ByteWritable(c.toByte))
        }

    def sortPartition: Iterator[Int] => Iterator[Int] =
        it => it.toArray.sortWith(compareSuffix).iterator

    def calculatePartialArray:Iterator[Int] => Iterator[Int] =
        it => PartialSorting.calculatePartialSA(bS.value, it.toArray.sorted, 256).iterator

    def compareSuffix(i1: Int, i2: Int): Boolean = {
        var a = i1
        var b = i2
        while (a < bS.value.length && b < bS.value.length) {
            val cA = bS.value.charAt(a)
            val cB = bS.value.charAt(b)
            val comparison = cA.compareTo(cB)
            if (comparison != 0)
                return comparison < 0


            a = a + 1
            b = b + 1
        }

        a.compareTo(b) > 0
    }
}
