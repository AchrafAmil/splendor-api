package com.github.achrafamil.splendor.api

import com.github.achrafamil.splendor.api.data.CardCategory
import com.github.achrafamil.splendor.api.data.IllegalTransactionException
import com.github.achrafamil.splendor.api.data.Transaction
import com.github.achrafamil.splendor.api.data.mapToColorMap
import com.github.achrafamil.splendor.api.players.TokenCollectorPlayer
import com.github.achrafamil.splendor.api.players.TurnSkippingPlayer
import com.github.achrafamil.splendor.api.players.aPlayerWhoDoesOnlyOnce
import org.junit.Test

class IllegalTransactionTest {

    @Test(expected = IllegalTransactionException::class)
    fun `continuously collecting tokens should throw IllegalTransactionException`() {
        val game = Game()

        game.registerPlayer(TokenCollectorPlayer("Player A"))
        game.registerPlayer(TurnSkippingPlayer("Player B"))

        game.start()
    }

    @Test(expected = IllegalTransactionException::class)
    fun `collecting more than 3 tokens should throw IllegalTransactionException`() {
        val game = Game()

        val player = aPlayerWhoDoesOnlyOnce { _, _, _ ->
            Transaction.TokensExchange(mapToColorMap(1, 1, 1, 1))
        }

        game.registerPlayer(player)
        game.registerPlayer(TurnSkippingPlayer("Player B"))

        game.start()
    }

    @Test(expected = IllegalTransactionException::class)
    fun `buying a card when user cannot afford it should throw IllegalTransactionException`() {
        val game = Game()

        val player = aPlayerWhoDoesOnlyOnce { _, _, boardState ->
            Transaction.CardBuying(boardState.cards.getValue(CardCategory.FIRST).first().id)
        }

        game.registerPlayer(player)
        game.registerPlayer(TurnSkippingPlayer("Player B"))

        game.start()
    }

    @Test(expected = IllegalTransactionException::class)
    fun `reserving a card that is not revealed should throw IllegalTransactionException`() {
        val game = Game()

        val player = aPlayerWhoDoesOnlyOnce { _, _, boardState ->
            val revealedCardsIds = boardState.cards.values.flatten().map { it.id }
            val notRevealedCardId = (0..80).first { it !in revealedCardsIds }
            Transaction.CardReservation(notRevealedCardId)
        }

        game.registerPlayer(player)
        game.registerPlayer(TurnSkippingPlayer("Player B"))

        game.start()
    }
}
