package me.avo.spring.kotlin.service.classifier

import me.avo.spring.kotlin.model.BoundingBox
import me.avo.spring.kotlin.model.Recognition
import org.apache.commons.math3.analysis.function.Sigmoid
import org.tensorflow.Tensor
import java.util.*
import kotlin.Comparator

object YOLOClassifier {

    private const val OVERLAP_THRESHOLD = 0.5f
    private val anchors = doubleArrayOf(1.08, 1.19, 3.42, 4.41, 6.63, 11.38, 9.42, 5.11, 16.62, 10.52)
    private const val SIZE = 13
    private const val MAX_RECOGNIZED_CLASSES = 24
    private const val THRESHOLD = 0.5f
    private const val MAX_RESULTS = 24
    private const val NUMBER_OF_BOUNDING_BOX = 5

    /**
     * Gets the number of classes based on the tensor shape
     *
     * @param result - the tensorflow output
     * @return the number of classes
     */
    fun getOutputSizeByShape(result: Tensor<Float>): Int = (result.shape()[3] * Math.pow(SIZE.toDouble(), 2.0)).toInt()

    fun classifyImage(tensorFlowOutput: FloatArray, labels: List<String>): List<Recognition> {
        val numClass = (tensorFlowOutput.size / (Math.pow(SIZE.toDouble(), 2.0) * NUMBER_OF_BOUNDING_BOX) - 5).toInt()
        val boundingBoxPerCell =
            Array(SIZE) { Array<Array<BoundingBox?>>(SIZE) { arrayOfNulls(NUMBER_OF_BOUNDING_BOX) } }

        val priorityQueue = PriorityQueue(MAX_RECOGNIZED_CLASSES, comparator)

        var offset = 0
        for (cy in 0..SIZE) { // SIZE * SIZE cells
            for (cx in 0..SIZE) {
                for (b in 0..NUMBER_OF_BOUNDING_BOX) { // 5 bounding boxes per each cell
                    boundingBoxPerCell[cx][cy][b] = getModel(tensorFlowOutput, cx, cy, b, numClass, offset)

                }
            }
        }

        return TODO()
    }

    private fun getModel(
        tensorFlowOutput: FloatArray,
        cx: Int,
        cy: Int,
        b: Int,
        numClass: Int,
        offset: Int
    ): BoundingBox {
        val sigmoid = Sigmoid()
        return BoundingBox(
            x = (cx + sigmoid.value(tensorFlowOutput[offset])) * 32,
            y = (cy + sigmoid.value(tensorFlowOutput[offset + 1])) * 32,
            width = exp(tensorFlowOutput[offset + 2]) * anchors[2 * b] * 32,
            height = exp(tensorFlowOutput[offset + 3]) * anchors[2 * b + 1] * 32,
            confidence = sigmoid.value(tensorFlowOutput[offset + 4]),
            classes = DoubleArray(numClass) {
                tensorFlowOutput[it + offset + 5].toDouble()
            }
        )
    }

    private val comparator = Comparator<Recognition> { first, second ->
        first.confidence.compareTo(second.confidence)
    }

    private fun Sigmoid.value(x: Float) = value(x.toDouble())
    private fun exp(a: Float) = Math.exp(a.toDouble())

    private fun calculateTopPredictions(
        boundingBox: BoundingBox,
        predictionQueue: PriorityQueue<Recognition>,
        labels: List<String>
    ) {
        for (i in 0..boundingBox.classes.size) {
            //val argMax TODO
        }
    }

}