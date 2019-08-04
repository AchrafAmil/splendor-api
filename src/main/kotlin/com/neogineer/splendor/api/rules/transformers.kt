package com.neogineer.splendor.api.rules

import com.neogineer.splendor.api.data.Board
import com.neogineer.splendor.api.data.Card
import com.neogineer.splendor.api.data.IllegalTransactionException
import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.data.Transaction
import com.neogineer.splendor.api.utils.PrintLogger
import com.neogineer.splendor.api.utils.mergeWith
import com.neogineer.splendor.api.utils.remove

/**
 *
 * Commits transaction to board, and returns the new playerState.
 *
 */
fun Board.commit(playerState: PlayerState, transaction: Transaction): PlayerState {
    if (!state.playerCanSubmitTransaction(playerState, transaction)) {
        throw IllegalTransactionException("$transaction is not valid or board/player can't afford it")
    } else {
        PrintLogger().i("Board", "processing transaction $transaction for player: ${playerState.name}")
        return when (transaction) {
            is Transaction.TokensExchange -> {
                tokens.remove(transaction.tokens)
                playerState.copy(
                    tokens = playerState.tokens.mergeWith(transaction.tokens)
                )
            }
            is Transaction.CardBuying -> {
                val card = removeCard(transaction.cardId)
                playerState.copy(
                    cards = playerState.cards + card
                )
            }
            is Transaction.CardReservation -> {
                val card = removeCard(transaction.cardId)
                playerState.copy(
                    reservedCards = playerState.reservedCards + card
                )
            }
        }
    }
}

private fun Board.removeCard(cardId: Int): Card {
    val card = cards.values.flatten().find { it.id == cardId }!!
    cards.getValue(card.category).remove(card)
    return card
}
