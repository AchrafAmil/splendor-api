package com.github.achrafamil.splendor.api

import com.github.achrafamil.splendor.api.data.PlayerState
import com.github.achrafamil.splendor.api.players.BasicPlayer
import com.github.achrafamil.splendor.api.players.TurnSkippingPlayer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert
import org.junit.Test

class GameTest {

    @Test(expected = IllegalStateException::class)
    fun `start the game with only one player should not work`() {
        val game = Game()

        game.registerPlayer(TurnSkippingPlayer("Player 1"))
        game.start()
    }

    @Test
    fun `Turn-skipping players should never win`() {
        val game = Game()
        val expectedWinner = BasicPlayer("Player 3")
        val gameCallbackMock = mock<GameCallback>()
        val winnerCaptor = argumentCaptor<PlayerState>()

        game.registerPlayer(TurnSkippingPlayer("Player 1"))
        game.registerPlayer(TurnSkippingPlayer("Player 2"))
        game.registerPlayer(expectedWinner)
        game.registerPlayer(TurnSkippingPlayer("Player 4"))
        game.start(gameCallbackMock)

        verify(gameCallbackMock).onGameFinished(winnerCaptor.capture(), any(), any())
        Assert.assertEquals(expectedWinner.name, winnerCaptor.firstValue.name)
    }
}
