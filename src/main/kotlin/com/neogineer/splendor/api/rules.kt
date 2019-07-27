package com.neogineer.splendor.api

import com.neogineer.splendor.api.data.BoardState
import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.data.Transaction

fun Transaction.TokensExchange.isValid(): Boolean {
    val strictlyPositiveValuesCount = tokens.values.count { it > 0 }

    // make sure: at most 3 strictly positive values, and strictly positive values are either 1 or 2
    if (strictlyPositiveValuesCount > 3) return false
    if (tokens.values.any { it > 2 }) return false

    // either at most one token by color from at most 3 colors,
    // or 2 tokens from the same color
    return (tokens.values.none { it == 2 } || strictlyPositiveValuesCount <= 1)
}

fun BoardState.playerCanSubmitTransaction(
    playerState: PlayerState,
    transaction: Transaction.TokensExchange
): Boolean {
    if (!transaction.isValid()) return false

    // make sure:
    // - board has enough tokens to offer,
    // - player has enough tokens to return,
    // - and '2 tokens from same color' transactions are on colors with at least 4 available tokens.
    transaction.tokens.forEach { (color, transactionColorTokensCount) ->
        val boardColorTokensCount = this.tokens[color] ?: 0
        val playerColorTokensCount = playerState.tokens[color] ?: 0
        if (
            boardColorTokensCount - transactionColorTokensCount < 0
            || playerColorTokensCount + transactionColorTokensCount < 0
            || transactionColorTokensCount == 2 && boardColorTokensCount < 4
        ) {
            return false
        }
    }

    return true
}