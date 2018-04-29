package me.avo.spring.kotlin.service.classifier

import me.avo.spring.kotlin.ApplicationProperties
import me.avo.spring.kotlin.util.GraphBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.tensorflow.Graph
import org.tensorflow.Session
import org.tensorflow.Tensor
import java.io.File

@Service
class ObjectDetector(private val applicationProperties: ApplicationProperties) {

    private val graphDef = File(applicationProperties.graph).readBytes()
    private val labels = File(applicationProperties.label).readLines()
    private val logger = LoggerFactory.getLogger(ObjectDetector::class.java)

    fun detect(imageLocation: String): Map<String, Any> {
        val image = File(imageLocation).readBytes()
        normalizeImage(image).use {
            val recognitions: List<*> = TODO()


            return TODO()
        }
    }


    private fun normalizeImage(imageBytes: ByteArray): Tensor<Float> = Graph().use { graph ->
        val output = GraphBuilder(graph).run {
            constant("input", imageBytes)
                .let { decodeJpeg(it, 3) }
                .let { cast(it, Float::class.java) }
                .let { expandDims(it, constant("make_batch", 0)) }
                .let {
                    resizeBilinear(
                        it,
                        constant("size", intArrayOf(applicationProperties.imageSize, applicationProperties.imageSize))
                    )
                }
                .let { div(it, constant("scale", applicationProperties.imageMean)) }
        }

        Session(graph).use {
            it.runner()
                .fetch(output.op().name())
                .run()[0]
                .expect(Float::class.java)
        }
    }

}