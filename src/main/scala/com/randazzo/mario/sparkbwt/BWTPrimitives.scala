package com.randazzo.mario.sparkbwt

import com.randazzo.mario.sparkbwt.jni.SAPartial
import org.apache.spark.broadcast.Broadcast

import scala.language.implicitConversions
import scala.math.{min, pow, round}

/**
 *
 * @param r
 * @param k
 * @param bS
 */
class BWTPrimitives(r : Int, k : Int, bS : Broadcast[String]) extends Serializable {

    val alphaSize = 5
    val alpha: Map[Char, Int] = Map('\0' -> 0,
                                    'a' -> 1, 'c' -> 2, 'g' -> 3, 't' -> 4,
                                    'A' -> 1, 'C' -> 2, 'G' -> 3, 'T' -> 4)
    val maxValue: Long = calcMaxValue()

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
     * @param idx
     * @return
     */
    def map(idx : Int) : (Long, Int) = { ( toRange(idx), idx) }

    /**
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

    /**
     *
     * @param t
     * @return
     */
    def reduce( t : (Long, Iterable[Int]) ): (Long, Array[Int]) = {
        (t._1, SAPartial.calculatePartialSA(bS.value, t._2.toArray.sorted, 256))
    }

    /**
     *
     * @param v
     * @return
     */
    implicit def int2Integer(v : Array[Int]) : Array[java.lang.Integer] = {
        val tmp = new Array[java.lang.Integer](v.length)

        for (i <- v.indices)
            tmp(i) = java.lang.Integer.valueOf(v(i))

        tmp
    }
}
