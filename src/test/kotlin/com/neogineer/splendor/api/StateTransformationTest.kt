package com.github.achrafamil.splendor.api

import com.github.achrafamil.splendor.api.data.BoardState
import com.github.achrafamil.splendor.api.data.Color
import com.github.achrafamil.splendor.api.data.IllegalTransactionException
import com.github.achrafamil.splendor.api.data.PlayerState
import com.github.achrafamil.splendor.api.data.Transaction
import com.github.achrafamil.splendor.api.players.TokenCollectorPlayer
import com.github.achrafamil.splendor.api.players.TokenCollectorPlayer.Companion.TOKENS_TO_COLLECT
import com.github.achrafamil.splendor.api.players.TurnSkippingPlayer
import org.junit.Assert
import org.junit.Test

class StateTransformationTest {

    @Test
    fun `player should keep tokens he collected in previous turns`() {
        val firstPlayer = TokenCollectorPlayer("player1")
        val statesByTurn = getFullGameStates(
            listOf(
                firstPlayer,
                TurnSkippingPlayer("player2"),
                TurnSkippingPlayer("player3"),
                TurnSkippingPlayer("player4")
            )
        )

        val player1States = statesByTurn
            .mapNotNull { (_, playersStates) -> playersStates.firstOrNull() { it.name == firstPlayer.name } }

        val firstState = player1States[0]
        val secondState = player1States[1]

        secondState.tokens.forEach { (color, count) ->
            val expected = (firstState.tokens[color] ?: 0) + (if (color in TOKENS_TO_COLLECT.keys) 1 else 0)
            Assert.assertEquals(expected, count)
        }
    }

    @Test
    fun `after transformation all tokens in the game should sum up to 7 by color`() {
        val statesByTurn = getFullGameStates(
            listOf(
                TokenCollectorPlayer("player1"),
                TokenCollectorPlayer("player2"),
                TurnSkippingPlayer("player3"),
                TurnSkippingPlayer("player4")
            )
        )

        statesByTurn.forEach { (boardState, playersStates) ->
            Color.values().forEach { color ->
                val colorTokensCount =
                    boardState.tokens.getOrDefault(color, 0) + playersStates.sumBy { it.tokens.getOrDefault(color, 0) }
                Assert.assertEquals(7, colorTokensCount)
            }
        }
    }

    /**
     * return type is not converted to map in order to preserve,
     * in a very unlikely case (yet possible), duplicate keys.
     *
     */
    private fun getFullGameStates(players: List<Player>): List<Pair<BoardState, List<PlayerState>>> {
        val gameMaster = GameMaster()
        players.forEach { gameMaster.registerPlayer(it) }
        val boardStates = mutableListOf<Pair<BoardState, List<PlayerState>>>()

        val callback = object : GameCallbackAdapter() {
            override fun onPlayerSubmittedTransaction(
                playerState: PlayerState,
                otherPlayersStates: List<PlayerState>,
                boardState: BoardState,
                transaction: Transaction
            ) {
                if (playerState.name == players.first().name) {
                    boardStates.add(boardState to otherPlayersStates.plus(playerState))
                }
            }
        }

        try {
            gameMaster.start(callback)
        } catch (ite: IllegalTransactionException) {
            // no-op: players are not smart to avoid IllegalTransactionException
        }

        return boardStates
    }
}
