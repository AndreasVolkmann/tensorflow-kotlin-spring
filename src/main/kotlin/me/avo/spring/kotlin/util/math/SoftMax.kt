package me.avo.spring.kotlin.util.math

class SoftMax(val params: DoubleArray) {

    fun getValue(): DoubleArray {
        var sum = 0.0

        for (i in 0 until params.size) {
            params[i] = Math.exp(params[i])
            sum += params[i]
        }

        if (java.lang.Double.isNaN(sum) || sum < 0) {
            for (i in 0 until params.size) {
                params[i] = 1.0 / params.size
            }
        } else {
            for (i in 0 until params.size) {
                params[i] = params[i] / sum
            }
        }

        return params
    }

}