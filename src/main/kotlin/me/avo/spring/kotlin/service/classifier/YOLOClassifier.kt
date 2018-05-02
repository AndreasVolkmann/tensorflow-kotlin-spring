package me.avo.spring.kotlin.service.classifier

import me.avo.spring.kotlin.model.BoundingBox
import me.avo.spring.kotlin.model.BoxPosition
import me.avo.spring.kotlin.model.Recognition
import me.avo.spring.kotlin.util.math.ArgMax
import me.avo.spring.kotlin.util.math.SoftMax
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
                    val box = getModel(tensorFlowOutput, cx, cy, b, numClass, offset)
                    boundingBoxPerCell[cx][cy][b] = box
                    calculateTopPredictions(box, priorityQueue, labels)
                    offset += numClass + 5
                }
            }
        }

        return getRecognition(priorityQueue)
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
            val argMax = SoftMax(boundingBox.classes).getValue().let(::ArgMax).getResult()
            val confidenceInClass = argMax.maxValue * boundingBox.confidence
            if (confidenceInClass > THRESHOLD) {
                predictionQueue.add(
                    Recognition(
                        argMax.index, labels[argMax.index], confidenceInClass.toFloat(),
                        BoxPosition(
                            initialLeft = (boundingBox.x - boundingBox.width / 2).toFloat(),
                            initialTop = (boundingBox.y - boundingBox.height / 2).toFloat(),
                            width = boundingBox.width.toFloat(),
                            height = boundingBox.height.toFloat()
                        )
                    )
                )
            }

        }
    }


    private fun getRecognition(priorityQueue: PriorityQueue<Recognition>): List<Recognition> {
        val recognitions = mutableListOf<Recognition>()

        if (priorityQueue.isNotEmpty()) {
            val bestRecognition = priorityQueue.poll()
            recognitions.add(bestRecognition)

            for (i in 0 until Math.min(priorityQueue.size, MAX_RESULTS)) {
                val recognition = priorityQueue.poll()
                val overlaps = recognitions.any {
                    getIntersectionProportion(it.location, recognition.location) > OVERLAP_THRESHOLD
                }
                if (!overlaps) {
                    recognitions.add(recognition)
                }
            }
        }
        return recognitions
    }

    private fun getIntersectionProportion(primaryShape: BoxPosition, secondaryShape: BoxPosition): Float =
        if (overlaps(primaryShape, secondaryShape)) {
            val rightLeft =
                Math.min(primaryShape.right, secondaryShape.right) - Math.max(primaryShape.left, secondaryShape.left)
            val topBottom =
                Math.min(primaryShape.bottom, secondaryShape.bottom) - Math.max(primaryShape.top, secondaryShape.top)
            val intersectionSurface = Math.max(0f, rightLeft) * Math.max(0f, topBottom)

            val surfacePrimary =
                Math.abs(primaryShape.right - primaryShape.left) * Math.abs(primaryShape.bottom - primaryShape.top)

            intersectionSurface / surfacePrimary
        } else 0f

    private fun overlaps(primary: BoxPosition, secondary: BoxPosition): Boolean =
        primary.left < secondary.right
                && primary.right > secondary.left
                && primary.top < secondary.bottom
                && primary.bottom > secondary.top

}