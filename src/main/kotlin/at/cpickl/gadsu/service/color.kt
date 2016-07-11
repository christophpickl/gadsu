package at.cpickl.gadsu.service

import java.awt.Color

// http://stackoverflow.com/questions/340209/generate-colors-between-red-and-green-for-a-power-meter
private val REDGREEN_PERCENTAGE_HUE = 0.4F // 0.4 is green in the java hue
private val REDGREEN_PERCENTAGE_SATURATION = 0.9F
private val REDGREEN_PERCENTAGE_BRIGHTNESS = 0.9F
fun colorByPercentage(percentage: Double) = Color.getHSBColor(
        (percentage * REDGREEN_PERCENTAGE_HUE).toFloat(),
        REDGREEN_PERCENTAGE_SATURATION,
        REDGREEN_PERCENTAGE_BRIGHTNESS
)