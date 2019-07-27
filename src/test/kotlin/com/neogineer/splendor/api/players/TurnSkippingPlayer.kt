package com.neogineer.splendor.api.players

import com.neogineer.splendor.api.Player
import com.neogineer.splendor.api.data.BoardState
import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.data.Transaction

class TurnSkippingPlayer(dummyPlayerName: String) : Player("TurnSkippingPlayer named $dummyPlayerName") {

    override fun playTurn(
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ): Transaction {
        return Transaction.TokensExchange(emptyMap())
    }
}