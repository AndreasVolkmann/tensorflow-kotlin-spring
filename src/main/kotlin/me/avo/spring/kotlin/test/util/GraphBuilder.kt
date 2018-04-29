package me.avo.spring.kotlin.test.util

import org.tensorflow.DataType
import org.tensorflow.Graph
import org.tensorflow.Output
import org.tensorflow.Tensor
import org.tensorflow.types.UInt8

class GraphBuilder(private val graph: Graph) {

    fun div(x: Output<Float>, y: Output<Float>) = binaryOp("Div", x, y)

    fun <T> resizeBilinear(images: Output<T>, size: Output<Int>): Output<Float> =
        binaryOp3("ResizeBilinear", images, size)

    fun <T> expandDims(input: Output<T>, dim: Output<Int>): Output<T> =
        binaryOp3("ExpandDims", input, dim)

    fun <T, U> cast(value: Output<T>, type: Class<U>): Output<U> {
        val dtype = DataType.fromClass(type)
        return graph
            .opBuilder("Cast", "Cast")
            .addInput(value)
            .setAttr("DstT", dtype)
            .build()
            .output(0)
    }

    fun decodeJpeg(contents: Output<String>, channels: Long): Output<UInt8> = graph
        .opBuilder("DecodeJpeg", "DecodeJpeg")
        .addInput(contents)
        .setAttr("channels", channels)
        .build()
        .output(0)

    fun <T> constant(name: String, value: Any, type: Class<T>): Output<T> = Tensor.create(value, type).use {
        graph.opBuilder("Const", name)
            .setAttr("dtype", DataType.fromClass(type))
            .setAttr("value", it)
            .build()
            .output(0)
    }

    fun constant(name: String, value: ByteArray): Output<String> = constant(name, value, String::class.java)

    fun constant(name: String, value: Int): Output<Int> = constant(name, value, Int::class.java)

    fun constant(name: String, value: IntArray): Output<Int> = constant(name, value, Int::class.java)

    fun constant(name: String, value: Float): Output<Float> = constant(name, value, Float::class.java)

    private fun <T> binaryOp(type: String, in1: Output<T>, in2: Output<T>): Output<T> =
        graph.opBuilder(type, type).addInput(in1).addInput(in2).build().output(0)

    private fun <T, U, V> binaryOp3(type: String, in1: Output<U>, in2: Output<V>): Output<T> =
        graph.opBuilder(type, type).addInput(in1).addInput(in2).build().output(0)

}