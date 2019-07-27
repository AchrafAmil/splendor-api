package com.neogineer.splendor.api

import com.neogineer.splendor.api.data.BoardState
import com.neogineer.splendor.api.data.IllegalTransactionException
import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.data.Transaction
import com.neogineer.splendor.api.data.mapToAllColors
import com.neogineer.splendor.api.data.mapToColorMap
import com.neogineer.splendor.api.players.TokenCollectorPlayer
import com.neogineer.splendor.api.players.TurnSkippingPlayer
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

        val player = object : Player("Player A") {
            var firstTurn = true
            override fun playTurn(
                opponentsStates: List<PlayerState>,
                selfState: PlayerState,
                boardState: BoardState
            ): Transaction {
                return if (firstTurn) {
                    Transaction.TokensExchange(mapToColorMap(1, 1, 1, 1))
                } else {
                    Transaction.TokensExchange(mapToAllColors(0))
                }.also { firstTurn = false }
            }
        }
        gameMaster.registerPlayer(player)
        gameMaster.registerPlayer(TurnSkippingPlayer("Player B"))

        gameMaster.start()
    }
}
