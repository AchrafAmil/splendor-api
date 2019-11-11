package com.neogineer.splendor.api

import com.neogineer.splendor.api.data.CardCategory
import com.neogineer.splendor.api.data.IllegalTransactionException
import com.neogineer.splendor.api.data.Transaction
import com.neogineer.splendor.api.data.mapToColorMap
import com.neogineer.splendor.api.players.TokenCollectorPlayer
import com.neogineer.splendor.api.players.TurnSkippingPlayer
import com.neogineer.splendor.api.players.aPlayerWhoDoesOnlyOnce
import org.junit.Test

class IllegalTransactionTest {

    @Test(expected = IllegalTransactionException::class)
    fun `continuously collecting tokens should throw IllegalTransactionException`() {
        val gameMaster = GameMaster()

        gameMaster.registerPlayer(TokenCollectorPlayer("Player A"))
        gameMaster.registerPlayer(TurnSkippingPlayer("Player B"))

        gameMaster.start()
    }

    @Test(expected = IllegalTransactionException::class)
    fun `collecting more than 3 tokens should throw IllegalTransactionException`() {
        val gameMaster = GameMaster()

        val player = aPlayerWhoDoesOnlyOnce { _, _, _ ->
            Transaction.TokensExchange(mapToColorMap(1, 1, 1, 1))
        }

        gameMaster.registerPlayer(player)
        gameMaster.registerPlayer(TurnSkippingPlayer("Player B"))

        gameMaster.start()
    }

    @Test(expected = IllegalTransactionException::class)
    fun `buying a card when user cannot afford it should throw IllegalTransactionException`() {
        val gameMaster = GameMaster()

        val player = aPlayerWhoDoesOnlyOnce { _, _, boardState ->
            Transaction.CardBuying(boardState.cards.getValue(CardCategory.FIRST).first().id)
        }

        gameMaster.registerPlayer(player)
        gameMaster.registerPlayer(TurnSkippingPlayer("Player B"))

        gameMaster.start()
    }

    @Test(expected = IllegalTransactionException::class)
    fun `reserving a card that is not revealed should throw IllegalTransactionException`() {
        val gameMaster = GameMaster()

        val player = aPlayerWhoDoesOnlyOnce { _, _, boardState ->
            val revealedCardsIds = boardState.cards.values.flatten().map { it.id }
            val notRevealedCardId = (0..80).first { it !in revealedCardsIds }
            Transaction.CardReservation(notRevealedCardId)
        }

        gameMaster.registerPlayer(player)
        gameMaster.registerPlayer(TurnSkippingPlayer("Player B"))

        gameMaster.start()
    }
}
