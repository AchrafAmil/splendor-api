package com.github.achrafamil.splendor.api.data

import com.github.achrafamil.splendor.api.Player

/**
 * Transaction is what a player can do during one turn (plus eventually choosing a noble if applicable).
 *
 * This is returned by [Player] as a result of playing a turn and making a decision.
 *
 */
sealed class Transaction {
    /**
     * @param tokens tokens to collect from [BoardState]
     */
    data class TokensExchange(
        val tokens: Map<Color, Int>
    ) : Transaction()

    /**
     * @param cardId id of one of the cards present at the current provided [BoardState] you want to buy.
     * Make sure you can afford the specified card
     */
    data class CardBuying(
        val cardId: Int
    ) : Transaction()

    /**
     * @param cardId id of one of the cards present at the current provided [BoardState] you want to reserve
     * for further eventual buying.
     */
    data class CardReservation(
        val cardId: Int
    ) : Transaction()
}
