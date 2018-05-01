package me.avo.spring.kotlin.model

/**
 * Model to store the position of the bounding boxes
 */
class BoxPosition(

    initialLeft: Float,
    initialTop: Float,
    val width: Float,
    val height: Float
) {

    val left: Float
    val top: Float

    val right: Float
    val bottom: Float

    init {
        val tmpRight = initialLeft + width
        val tmpBottom = initialTop + height

        left = Math.min(initialLeft, tmpRight) // left should be lower value than right
        top = Math.min(initialTop, tmpBottom)  // top should be lower value than bottom
        right = Math.max(initialLeft, tmpRight)
        bottom = Math.max(initialTop, tmpBottom)
    }

    constructor(boxPosition: BoxPosition) : this(
        boxPosition.left,
        boxPosition.top,
        boxPosition.width,
        boxPosition.height
    )

    constructor(boxPosition: BoxPosition, scaleX: Float, scaleY: Float) : this(
        boxPosition.left * scaleX,
        boxPosition.top * scaleY,
        boxPosition.width * scaleX,
        boxPosition.height * scaleY
    )

}