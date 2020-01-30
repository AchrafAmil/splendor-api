package com.github.achrafamil.splendor.api.players

import com.github.achrafamil.splendor.api.Player
import com.github.achrafamil.splendor.api.data.BoardState
import com.github.achrafamil.splendor.api.data.Noble
import com.github.achrafamil.splendor.api.data.PlayerState
import com.github.achrafamil.splendor.api.data.Transaction
import com.github.achrafamil.splendor.api.data.mapToAllColors

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
                    .also { firstTurn = false }
            } else {
                Transaction.TokensExchange(mapToAllColors(0))
            }
        }

        override fun chooseNoble(
            affordableNobles: List<Noble>,
            opponentsStates: List<PlayerState>,
            selfState: PlayerState,
            boardState: BoardState
        ) = affordableNobles.first()
    }
}
