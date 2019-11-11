package com.neogineer.splendor.api

import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.players.BasicPlayer
import com.neogineer.splendor.api.players.TurnSkippingPlayer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert
import org.junit.Test

class GameMasterTest {

    @Test(expected = IllegalStateException::class)
    fun `start the game with only one player should not work`() {
        val gameMaster = GameMaster()

        gameMaster.registerPlayer(TurnSkippingPlayer("Player 1"))
        gameMaster.start()
    }

    @Test
    fun `Turn-skipping players should never win`() {
        val gameMaster = GameMaster()
        val expectedWinner = BasicPlayer("Player 3")
        val gameCallbackMock = mock<GameCallback>()
        val winnerCaptor = argumentCaptor<PlayerState>()

        gameMaster.registerPlayer(TurnSkippingPlayer("Player 1"))
        gameMaster.registerPlayer(TurnSkippingPlayer("Player 2"))
        gameMaster.registerPlayer(expectedWinner)
        gameMaster.registerPlayer(TurnSkippingPlayer("Player 4"))
        gameMaster.start(gameCallbackMock)

        verify(gameCallbackMock).onGameFinished(winnerCaptor.capture(), any(), any())
        Assert.assertEquals(expectedWinner.name, winnerCaptor.firstValue.name)
    }
}
