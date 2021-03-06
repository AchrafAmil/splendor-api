package com.github.achrafamil.splendor.api.rules

import com.github.achrafamil.splendor.api.data.BoardState
import com.github.achrafamil.splendor.api.data.Noble
import com.github.achrafamil.splendor.api.data.PlayerState
import com.github.achrafamil.splendor.api.data.Transaction
import kotlin.math.max

/**
 * @return true if [this] token exchange transaction is either
 * "up to 3 tokens from different colors" or "two tokens from same color".
 */
fun Transaction.TokensExchange.isValid(): Boolean {
    val strictlyPositiveValuesCount = tokens.values.count { it > 0 }

    // make sure: at most 3 strictly positive values, and strictly positive values are either 1 or 2
    if (strictlyPositiveValuesCount > 3) return false
    if (tokens.values.any { it > 2 }) return false

    // either at most one token by color from at most 3 colors,
    // or 2 tokens from the same color
    return (tokens.values.none { it == 2 } || strictlyPositiveValuesCount <= 1)
}

/**
 * @return true if transaction does respect all the rules (valid, affordable, etc).
 *
 * Check the official Splendor Game rules online.
 */
fun BoardState.playerCanSubmitTransaction(
    playerState: PlayerState,
    transaction: Transaction
): Boolean {
    when (transaction) {
        is Transaction.CardBuying -> {
            // make sure :
            // - board or user reservations contain this card,
            // - user can afford it.

            val card = cards
                .values
                .flatten()
                .plus(playerState.reservedCards)
                .firstOrNull { it.id == transaction.cardId }
                ?: return false

            val costGap = card
                .cost
                .map { (color, cardCost) ->
                    val cardsOfSameColorCount = playerState.cards.count { it.color == color }
                    val tokensOfSameColorCount = playerState.tokens[color] ?: 0
                    max(0, cardCost - tokensOfSameColorCount - cardsOfSameColorCount)
                }
                .sum()

            if (costGap > playerState.golds) return false
        }
        is Transaction.CardReservation -> {
            // make sure :
            // - board does contain this card,
            // - user has less than 3 reserved cards.

            val boardDoesNotContainCard = cards.values.flatten().none { it.id == transaction.cardId }
            val userHasThreeOrMoreReservedCards = playerState.reservedCards.size >= 3

            if (boardDoesNotContainCard || userHasThreeOrMoreReservedCards) return false
        }
        is Transaction.TokensExchange -> {
            if (!transaction.isValid()) return false

            // make sure:
            // - board has enough tokens to offer,
            // - player has enough tokens to return,
            // - and '2 tokens from same color' transactions are on colors with at least 4 available tokens.
            transaction.tokens.forEach { (color, transactionColorTokensCount) ->
                val boardColorTokensCount = this.tokens[color] ?: 0
                val playerColorTokensCount = playerState.tokens[color] ?: 0
                val boardHasEnoughTokens = boardColorTokensCount - transactionColorTokensCount >= 0
                val playerHasEnoughTokens = playerColorTokensCount + transactionColorTokensCount >= 0
                val isTwoTokensFromColorWithLessThanFour = transactionColorTokensCount == 2 && boardColorTokensCount < 4

                if (!boardHasEnoughTokens || !playerHasEnoughTokens || isTwoTokensFromColorWithLessThanFour) {
                    return false
                }
            }

            // Plus, make sure player's won't have more than $TOKENS_LIMIT_BY_PLAYER tokens after transaction
            if (transaction.tokens.values.sum() + playerState.tokens.values.sum() > TOKENS_LIMIT_BY_PLAYER) return false
        }
    }

    return true
}

fun PlayerState.canAffordNoble(noble: Noble): Boolean {
    return noble.cost.all { (color, cardsNeededCount) ->
        val playerCardsOfTheSameColor = this.cards.filter { card -> card.color == color }
        playerCardsOfTheSameColor.size >= cardsNeededCount
    }
}

const val TOKENS_LIMIT_BY_PLAYER = 10
