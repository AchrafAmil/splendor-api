package com.neogineer.splendor.api

import com.neogineer.splendor.api.data.BoardState
import com.neogineer.splendor.api.data.Noble
import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.data.Transaction

abstract class Player(open val name: String) {

    abstract fun playTurn(
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ): Transaction

    abstract fun chooseNoble(
        affordableNobles: List<Noble>,
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ): Noble
}
