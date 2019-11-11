package com.neogineer.splendor.api.players

import com.neogineer.splendor.api.Player
import com.neogineer.splendor.api.data.BoardState
import com.neogineer.splendor.api.data.Noble
import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.data.Transaction
import com.neogineer.splendor.api.data.mapToColorMap

/**
 * Keep in mind this player will, after few turns, make the game throw IllegalTransactionException.
 */
class TokenCollectorPlayer(dummyPlayerName: String) : Player("TokenCollectorPlayer named $dummyPlayerName") {

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
        val TOKENS_TO_COLLECT = mapToColorMap(blue = 1, white = 1, red = 1)
    }
}
