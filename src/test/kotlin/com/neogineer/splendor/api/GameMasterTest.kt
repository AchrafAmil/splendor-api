package com.neogineer.splendor.api

import com.neogineer.splendor.api.players.TurnSkippingPlayer
import org.junit.Test
import java.lang.IllegalStateException

class GameMasterTest {

    @Test
    fun `start the game with 4 players should work`() {
        val gameMaster = GameMaster()

        for (playerNumber in 0..3) {
            gameMaster.registerPlayer(TurnSkippingPlayer("Player $playerNumber"))
        }

        gameMaster.start()
    }

    @Test(expected = IllegalStateException::class)
    fun `start the game with only one player should not work`() {
        val gameMaster = GameMaster()

        gameMaster.registerPlayer(TurnSkippingPlayer("Player 1"))

        gameMaster.start()
    }
}
