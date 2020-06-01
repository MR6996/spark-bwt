package com.randazzo.mario.sparkbwt

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.{HashPartitioner, SparkContext}


class SuffixArray(session: SparkSession, input: RDD[(Long, Byte)], n: Long) {

    val sc: SparkContext = session.sparkContext

    def get: RDD[(Long, Long)] = {

        // Contains the number of occurrences of each character in the input string.
        val counts: RDD[(Byte, Long)] = input.map(x => (x._2, 1L)).reduceByKey(_ + _)

        val countsSorted: Array[(Byte, Long)] = counts.collect.sorted

        // It's a map the contains the cumulative sum of counts (ordered previously by character).
        val cumulative: Broadcast[Map[Byte, Long]] = sc.broadcast(
            countsSorted.map(_._1).zip(countsSorted.map(_._2).scanLeft(0L)(_ + _)).toMap
        )

        // Contains for each character in input a tuple of the type (i, r), where i is the index of the character
        // and r is the rank of suffix S_i based only by the first character.
        var base: RDD[(Long, Long)] = input.map { case (i, c) => (i, cumulative.value(c)) }.cache
        var base12sorted: RDD[((Long, Long), Long)] = null

        for (i <- 0 until Math.ceil(Math.log(n) / Math.log(2)).toInt) {
            val k = sc.broadcast(i)

            if (base12sorted != null) base12sorted.unpersist()

            // Contains for each suffix in base a tuple of the type ((p, n), i), where i is the index of the suffix
            // p is the current rank and n is the rank of the suffix shifted to left by 2?k
            base12sorted = base
                .map { case (i, r) => (i, (r, 0L)) }
                .union(base.map { case (i, r) => (i - (1 << k.value), (-r, 0L)) })
                .partitionBy(new HashPartitioner(sc.defaultParallelism))
                .reduceByKey { case ((r1, _), (r2, _)) => if (r1 >= 0) (r1, -r2) else (r2, -r1) }
                .filter { case (i, _) => i >= 0 }
                .map(_.swap)
                .sortByKey()
                .cache

            val lengths: Array[Long] = sc.runJob(base12sorted, LocalUtils.getIteratorSize _)
            val offsets: Array[Long] = lengths.scanLeft(0L)(_ + _)

            base.unpersist()
            base = base12sorted
                .mapPartitionsWithIndex((i, iter) => {
                    iter.toSeq
                        .zip(offsets(i) until offsets(i) + lengths(i))
                        .scanLeft(((-1L, -1L), (0L, 0L))) { case ((prev_t, (_, prev_j)), ((t, i), j)) =>
                            if (t != prev_t) (t, (i, j)) else (t, (i, prev_j))
                        }
                        .tail
                        .map(_._2)
                        .toIterator
                })
                .cache

            //k.destroy()
        }

        base
    }


}