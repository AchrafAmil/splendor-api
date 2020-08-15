package com.github.achrafamil.splendor.api

import com.github.achrafamil.splendor.api.data.BoardState
import com.github.achrafamil.splendor.api.data.CardCategory
import com.github.achrafamil.splendor.api.data.Color
import com.github.achrafamil.splendor.api.data.Transaction
import com.github.achrafamil.splendor.api.data.colorMap
import com.github.achrafamil.splendor.api.players.BasicPlayer
import com.github.achrafamil.splendor.api.players.TurnSkippingPlayer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.firstValue
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
        val initialBoardState = getInitialState(playersCount)

        Color.values().forEach { color ->
            Assert.assertEquals(expectedTokensCount, initialBoardState.tokens[color])
        }
    }

    @Test
    fun `initial state for 4 players should provide 4 cards by card category`() {
        val initialBoardState = getInitialState(4)

        CardCategory.values().forEach { cardCategory ->
            Assert.assertEquals(4, initialBoardState.cards[cardCategory]?.size)
        }
    }

    @Test
    fun `initial state for 2 players should provide 3 nobles`() {
        val initialBoardState = getInitialState(2)

        Assert.assertEquals(3, initialBoardState.nobles.size)
    }

    private fun getInitialState(playersCount: Int): BoardState {
        val game = Game()
        val boardStateCaptor = ArgumentCaptor.forClass(BoardState::class.java)
        val player: Player = mock()
        whenever(player.name).thenReturn("player1")
        whenever(player.playTurn(any(), any(), any())).thenReturn(Transaction.TokensExchange(colorMap()))
        game.registerPlayer(player)
        game.registerPlayer(BasicPlayer("dummy player2"))
        if (playersCount > 2) game.registerPlayer(TurnSkippingPlayer("dummy player3"))
        if (playersCount > 3) game.registerPlayer(TurnSkippingPlayer("dummy player4"))

        game.start()
        verify(player, atLeastOnce()).playTurn(any(), any(), boardState = capture<BoardState>(boardStateCaptor))
        return boardStateCaptor.firstValue
    }
}
