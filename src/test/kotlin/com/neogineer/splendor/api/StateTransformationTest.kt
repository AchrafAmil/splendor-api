package com.neogineer.splendor.api

import com.neogineer.splendor.api.data.BoardState
import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.data.Transaction
import com.neogineer.splendor.api.players.TokenCollectorPlayer
import com.neogineer.splendor.api.players.TokenCollectorPlayer.Companion.TOKENS_TO_COLLECT
import com.neogineer.splendor.api.players.TurnSkippingPlayer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Test

class StateTransformationTest {

    @Test
    fun `player should keep tokens he collected in previous turns`() {
        val statesByTurn = getFullGameStates(
            Transaction.TokensExchange(TOKENS_TO_COLLECT),
            listOf(TokenCollectorPlayer("player2"), TokenCollectorPlayer("player3"), TurnSkippingPlayer("player4"))
        )

        val mockedPlayerStates = statesByTurn
            .map { (_, playersStates) -> playersStates.first { it.name == MOCKED_PLAYER_NAME } }

        val firstState = mockedPlayerStates[0]
        val secondState = mockedPlayerStates[1]

        secondState.tokens.forEach { (color, count) ->
            val expected = (firstState.tokens[color] ?: 0) + (if (color in TOKENS_TO_COLLECT.keys) 1 else 0)
            Assert.assertEquals(expected, count)
        }
    }

    private fun getFullGameStates(
        mockedPlayerTransaction: Transaction,
        opponents: List<Player>
    ): List<Pair<BoardState, List<PlayerState>>> {
        val gameMaster = GameMaster()
        val boardStateCaptor = argumentCaptor<BoardState>()
        val playerStateCaptor = argumentCaptor<PlayerState>()
        val opponentsStatesCaptor = argumentCaptor<List<PlayerState>>()
        val player: Player = mock()
        whenever(player.name).thenReturn(MOCKED_PLAYER_NAME)
        whenever(player.playTurn(any(), any(), any()))
            .thenReturn(mockedPlayerTransaction)
        gameMaster.registerPlayer(player)
        opponents.forEach { gameMaster.registerPlayer(it) }

        gameMaster.start()
        verify(player, atLeastOnce()).playTurn(
            opponentsStatesCaptor.capture(),
            playerStateCaptor.capture(),
            boardState = boardStateCaptor.capture()
        )
        val boardStates = boardStateCaptor.allValues
        val playerStates = playerStateCaptor.allValues
        val playersStatesByTurn = opponentsStatesCaptor
            .allValues
            .mapIndexed { turnIndex, opponentsStates ->
                listOf(playerStates[turnIndex]) + opponentsStates
            }

        return boardStates
            .mapIndexed { turnIndex, boardState ->
                boardState to playersStatesByTurn[turnIndex]
            }
    }

    companion object {
        private const val MOCKED_PLAYER_NAME = "mocked player1"
    }
}
