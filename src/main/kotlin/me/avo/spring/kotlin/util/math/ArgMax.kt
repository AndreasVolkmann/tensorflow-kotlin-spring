package me.avo.spring.kotlin.util.math

class ArgMax(val params: DoubleArray) {

    fun getResult(): Result {
        var maxIndex = 0
        for (i in 0 until params.size) {
            if (params[maxIndex] < params[i]) {
                maxIndex = i
            }
        }
        return Result(maxIndex, params[maxIndex])
    }


    class Result(val index: Int, val maxValue: Double)

}