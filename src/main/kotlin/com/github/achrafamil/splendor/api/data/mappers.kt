package com.github.achrafamil.splendor.api.data

/**
 * utility method to get a map of the 5 colors with zero as value of unspecified colors.
 */
fun colorMap(
    white: Int = 0,
    blue: Int = 0,
    green: Int = 0,
    red: Int = 0,
    black: Int = 0
): Map<Color, Int> {
    return mapOf(
        Color.WHITE to white,
        Color.BLUE to blue,
        Color.GREEN to green,
        Color.RED to red,
        Color.BLACK to black
    )
        .filter { it.value != 0 }
}

/**
 * utility method to create a map of colors with [color] as value for each of the 5 colors
 */
fun mapToAllColors(color: Int) = colorMap(color, color, color, color, color)

internal fun mapToCardCategory(categoryCode: Int): CardCategory {
    return when (categoryCode) {
        0 -> CardCategory.FIRST
        1 -> CardCategory.SECOND
        2 -> CardCategory.THIRD
        else -> throw IllegalArgumentException("Card category code $categoryCode not recognized")
    }
}

internal fun mapToColor(colorString: String): Color {
    return when (colorString.toUpperCase()) {
        "WHITE" -> Color.WHITE
        "BLUE" -> Color.BLUE
        "GREEN" -> Color.GREEN
        "RED" -> Color.RED
        "BLACK" -> Color.BLACK
        else -> throw IllegalArgumentException("Card color $colorString not recognized")
    }
}