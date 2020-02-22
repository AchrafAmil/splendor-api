package com.github.achrafamil.splendor.api.utils

import com.github.achrafamil.splendor.api.data.Color

fun <T> MutableSet<T>.draw(count: Int): Set<T> {
    synchronized(this) {
        val elements = take(count)
        this.removeAll(elements)
        return elements.toSet()
    }
}

fun MutableMap<Color, Int>.remove(tokens: Map<Color, Int>) {
    tokens.forEach { (color, count) ->
        this[color] = (this[color] ?: 0) - count
    }
}

fun Map<Color, Int>.mergeWith(other: Map<Color, Int>): Map<Color, Int> {
    return Color
        .values()
        .map { color ->
            color to (this[color] ?: 0) + (other[color] ?: 0)
        }
        .toMap()
        .filterValues { it != 0 }
}
