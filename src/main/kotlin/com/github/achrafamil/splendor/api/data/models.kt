package com.github.achrafamil.splendor.api.data


data class Card(
    val id: Int,
    val cost: Map<Color, Int>,
    val category: CardCategory,
    val color: Color,
    val points: Int
)

/**
 * Mutable state of the board
 */
internal data class Board(
    val cards: Map<CardCategory, MutableSet<Card>>,
    val tokens: MutableMap<Color, Int>,
    val nobles: MutableSet<Noble>,
    var gold: Int
) {
    val state: BoardState
        get() = BoardState(cards.toMap(), tokens.toMap(), nobles.toSet(), gold)
}

data class Noble(
    val id: Int,
    val cost: Map<Color, Int>,
    val points: Int
)

/**
 * an immutable snapshot of [Board]
 */
data class BoardState(
    val cards: Map<CardCategory, Set<Card>>,
    val tokens: Map<Color, Int>,
    val nobles: Set<Noble>,
    val gold: Int
)

/**
 * State of a player, represents a single player's owned assets (cards, tokens etc.)
 */
data class PlayerState(
    val name: String,
    val tokens: Map<Color, Int>,
    val cards: Set<Card>,
    val reservedCards: Set<Card>,
    val nobles: Set<Noble>,
    val golds: Int
) {
    /** points earned by the player so far, inferred from its stat
     */
    val points
        get() = cards.sumBy { it.points }
            .plus(nobles.sumBy { it.points })
}

enum class Color { WHITE, BLUE, GREEN, RED, BLACK }

/**
 * Categories from most affordable to most expensive
 * (from cards providing zero or one point to those providing four or five points)
 */
enum class CardCategory {
    FIRST,
    SECOND,
    THIRD
}
