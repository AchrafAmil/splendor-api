package com.neogineer.splendor.api.utils

fun <T> MutableSet<T>.draw(count: Int): Set<T> {
    synchronized(this) {
        val elements = take(count)
        this.removeAll(elements)
        return elements.toSet()
    }
}