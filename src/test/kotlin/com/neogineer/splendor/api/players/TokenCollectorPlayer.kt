package com.neogineer.splendor.api.players

import com.neogineer.splendor.api.Player
import com.neogineer.splendor.api.data.BoardState
import com.neogineer.splendor.api.data.Color
import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.data.Transaction

class TokenCollectorPlayer(dummyPlayerName: String) : Player("TokenCollectorPlayer named $dummyPlayerName") {

    override fun playTurn(
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ): Transaction {
        // supposed to crash after few turns
        return Transaction.TokensExchange(
            mapOf(
                Color.BLUE to 1,
                Color.WHITE to 1,
                Color.RED to 1
            )
        )
    }
}