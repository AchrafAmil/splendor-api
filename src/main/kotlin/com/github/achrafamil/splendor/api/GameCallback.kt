package com.github.achrafamil.splendor.api

import com.github.achrafamil.splendor.api.data.BoardState
import com.github.achrafamil.splendor.api.data.PlayerState
import com.github.achrafamil.splendor.api.data.Transaction

interface GameCallback {
    fun onGameStarted(boardState: BoardState)
    fun onNewTurn(turnNumber: Int)
    fun onGameFinished(winner: PlayerState, playersStates: List<PlayerState>, boardState: BoardState)
    fun onPlayerSubmittedTransaction(
        playerState: PlayerState,
        otherPlayersStates: List<PlayerState>,
        boardState: BoardState,
        transaction: Transaction
    )
}

open class GameCallbackAdapter : GameCallback {

    override fun onGameStarted(boardState: BoardState) {
        // no-op
    }

    override fun onNewTurn(turnNumber: Int) {
        // no-op
    }

    override fun onGameFinished(winner: PlayerState, playersStates: List<PlayerState>, boardState: BoardState) {
        // no-op
    }

    override fun onPlayerSubmittedTransaction(
        playerState: PlayerState,
        otherPlayersStates: List<PlayerState>,
        boardState: BoardState,
        transaction: Transaction
    ) {
        // no-op
    }
}
