package com.github.achrafamil.splendor.api.data

sealed class Transaction {
    data class TokensExchange(
        val tokens: Map<Color, Int>
    ) : Transaction()

    data class CardBuying(
        val cardId: Int
    ) : Transaction()

    data class CardReservation(
        val cardId: Int
    ) : Transaction()
}