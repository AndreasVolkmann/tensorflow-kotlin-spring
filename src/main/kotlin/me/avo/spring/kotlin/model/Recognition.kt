package me.avo.spring.kotlin.model

/**
 * An immutable result returned by a recognizer describing what was recognized.
 */
class Recognition(
    val id: Int,
    val title: String,
    val confidence: Float,
    val location: BoxPosition
) {

    fun getScaledLocation(scaleX: Float, scaleY: Float) = BoxPosition(location, scaleX, scaleY)

}