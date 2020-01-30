package com.github.achrafamil.splendor.api.players

import com.github.achrafamil.splendor.api.Player
import com.github.achrafamil.splendor.api.data.BoardState
import com.github.achrafamil.splendor.api.data.Noble
import com.github.achrafamil.splendor.api.data.PlayerState
import com.github.achrafamil.splendor.api.data.Transaction

class TurnSkippingPlayer(dummyPlayerName: String) : Player("TurnSkippingPlayer named $dummyPlayerName") {

    override fun playTurn(
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ): Transaction {
        return Transaction.TokensExchange(emptyMap())
    }

    override fun chooseNoble(
        affordableNobles: List<Noble>,
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ) = affordableNobles.first()
}
