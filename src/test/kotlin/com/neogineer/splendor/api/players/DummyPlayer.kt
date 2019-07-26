package com.neogineer.splendor.api.players

import com.neogineer.splendor.api.Player
import com.neogineer.splendor.api.data.BoardState
import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.data.Transaction

class DummyPlayer(dummyPlayerName: String) : Player("DummyPlayer named $dummyPlayerName") {

    override fun playTurn(
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ): Transaction {
        TODO("not implemented")
    }
}