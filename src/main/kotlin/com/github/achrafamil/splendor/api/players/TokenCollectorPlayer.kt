package com.github.achrafamil.splendor.api.players

import com.github.achrafamil.splendor.api.Player
import com.github.achrafamil.splendor.api.data.BoardState
import com.github.achrafamil.splendor.api.data.Noble
import com.github.achrafamil.splendor.api.data.PlayerState
import com.github.achrafamil.splendor.api.data.Transaction
import com.github.achrafamil.splendor.api.data.colorMap

/**
 * Keep in mind this player will, after few turns, make the game throw IllegalTransactionException.
 */
class TokenCollectorPlayer(dummyPlayerName: String) : Player {
    override val name: String = "TokenCollectorPlayer named $dummyPlayerName"

    override fun playTurn(
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ): Transaction {
        // supposed to crash after few turns
        return Transaction.TokensExchange(TOKENS_TO_COLLECT)
    }

    override fun chooseNoble(
        affordableNobles: List<Noble>,
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ) = affordableNobles.first()

    companion object {
        val TOKENS_TO_COLLECT = colorMap(blue = 1, white = 1, red = 1)
    }
}
