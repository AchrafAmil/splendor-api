package com.neogineer.splendor.api.data


data class Card(
    val id: Int,
    val cost: Map<Color, Int>,
    val category: CardCategory,
    val color: Color,
    val points: Int
)

data class Board(
    val cards: MutableMap<CardCategory, Set<Card>>,
    val tokens: MutableMap<Color, Int>,
    val nobles: MutableSet<Noble>,
    var gold: Int
)

data class Noble(
    val id: Int,
    val cost: Map<Color, Int>,
    val points: Int
)

data class BoardState(
    val cards: Map<CardCategory, Set<Card>>,
    val tokens: Map<Color, Int>,
    val nobles: Set<Noble>,
    val gold: Int
)

data class PlayerState(
    val name: String,
    val tokens: Map<Color, Int>,
    val cards: Set<Card>,
    val nobles: Set<Noble>,
    val golds: Int
) {
    val points
        get() = cards.sumBy { it.points }
            .plus(nobles.sumBy { it.points })
}

enum class Color { WHITE, BLUE, GREEN, RED, BLACK }

enum class CardCategory { FIRST, SECOND, THIRD }
