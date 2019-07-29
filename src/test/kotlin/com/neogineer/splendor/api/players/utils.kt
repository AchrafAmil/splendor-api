package com.neogineer.splendor.api.players

import com.neogineer.splendor.api.Player
import com.neogineer.splendor.api.data.BoardState
import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.data.Transaction
import com.neogineer.splendor.api.data.mapToAllColors

fun aPlayerWhoAlwaysDoes(
    name: String = "Anonymous player",
    turnImplementation: (
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ) -> Transaction
): Player {
    return object : Player(name) {
        override fun playTurn(
            opponentsStates: List<PlayerState>,
            selfState: PlayerState,
            boardState: BoardState
        ): Transaction {
            return turnImplementation(opponentsStates, selfState, boardState)
        }
    }
}

fun aPlayerWhoDoesOnlyOnce(
    name: String = "Anonymous one turn player",
    turnImplementation: (
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ) -> Transaction
): Player {
    return object : Player(name) {
        var firstTurn = true
        override fun playTurn(
            opponentsStates: List<PlayerState>,
            selfState: PlayerState,
            boardState: BoardState
        ): Transaction {
            return if (firstTurn) {
                turnImplementation(opponentsStates, selfState, boardState)
            } else {
                Transaction.TokensExchange(mapToAllColors(0))
            }.also { firstTurn = false }
        }
    }
}