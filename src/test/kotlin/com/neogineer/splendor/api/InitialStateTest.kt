package com.neogineer.splendor.api

import com.neogineer.splendor.api.data.BoardState
import com.neogineer.splendor.api.data.Color
import com.neogineer.splendor.api.players.TurnSkippingPlayer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentCaptor

class InitialStateTest {

    @Test
    fun `initial state for 2 players should provide 4 tokens by color`() {
        testTokensCountByPlayersCount(2, 4)
    }

    @Test
    fun `initial state for 3 players should provide 5 tokens by color`() {
        testTokensCountByPlayersCount(3, 5)
    }

    @Test
    fun `initial state for 4 players should provide 7 tokens by color`() {
        testTokensCountByPlayersCount(4, 7)
    }

    private fun testTokensCountByPlayersCount(playersCount: Int, expectedTokensCount: Int) {
        val gameMaster = GameMaster()
        val boardStateCaptor = ArgumentCaptor.forClass(BoardState::class.java)
        val player: Player = mock()
        whenever(player.name).thenReturn("player1")
        gameMaster.registerPlayer(player)
        gameMaster.registerPlayer(TurnSkippingPlayer("dummy player2"))
        if (playersCount > 2) gameMaster.registerPlayer(TurnSkippingPlayer("dummy player3"))
        if (playersCount > 3) gameMaster.registerPlayer(TurnSkippingPlayer("dummy player4"))

        gameMaster.start()
        verify(player).playTurn(any(), any(), boardState = capture<BoardState>(boardStateCaptor))
        val initialBoardState = boardStateCaptor.value

        Color.values().forEach { color ->
            Assert.assertEquals(expectedTokensCount, initialBoardState.tokens[color])
        }
    }
}