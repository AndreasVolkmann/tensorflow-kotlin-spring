package me.avo.spring.kotlin.model

/**
 * Model to store the data of a bounding box
 */
class BoundingBox(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double,
    val confidence: Double,
    val classes: DoubleArray
)