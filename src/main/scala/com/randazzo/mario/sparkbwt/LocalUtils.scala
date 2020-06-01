package com.randazzo.mario.sparkbwt

object LocalUtils extends Serializable {

    def getIteratorSize[T](iterator: Iterator[T]): Long = {
        var count = 0L
        while (iterator.hasNext) {
            count += 1
            iterator.next()
        }
        count
    }

}
