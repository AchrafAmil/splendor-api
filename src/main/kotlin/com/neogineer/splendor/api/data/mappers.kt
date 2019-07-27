package com.neogineer.splendor.api.data

import java.lang.IllegalArgumentException

fun mapToColorMap(
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
}

fun mapToAllColors(color: Int) = mapToColorMap(color, color, color, color, color)

fun mapToCardCategory(categoryCode: Int): CardCategory {
    return when (categoryCode) {
        0 -> CardCategory.FIRST
        1 -> CardCategory.SECOND
        2 -> CardCategory.THIRD
        else -> throw IllegalArgumentException("Card category code $categoryCode not recognized")
    }
}

fun mapToColor(colorString: String): Color {
    return when (colorString.toUpperCase()) {
        "WHITE" -> Color.WHITE
        "BLUE" -> Color.BLUE
        "GREEN" -> Color.GREEN
        "RED" -> Color.RED
        "BLACK" -> Color.BLACK
        else -> throw IllegalArgumentException("Card color $colorString not recognized")
    }
}