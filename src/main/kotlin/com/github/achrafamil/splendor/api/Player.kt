package com.github.achrafamil.splendor.api

import com.github.achrafamil.splendor.api.data.BoardState
import com.github.achrafamil.splendor.api.data.IllegalTransactionException
import com.github.achrafamil.splendor.api.data.Noble
import com.github.achrafamil.splendor.api.data.PlayerState
import com.github.achrafamil.splendor.api.data.Transaction

/**
 * Where playing strategies should be implemented.
 */
abstract class Player(open val name: String) {

    /**
     * Will be called exactly once on each turn to ask for a decision.
     *
     * Takes game status as arguments and expects a Transaction (aka a decision) in return.
     * Here you decide what to do every time it's your turn to play.
     * Collect tokens or buy cards or pass your turn, etc.
     *
     * @param opponentsStates current states of opponents.
     * @param selfState state of the Player instance being called.
     * @param boardState current state of the board (available cards and tokens etc.)
     *
     * Warning: *Make sure you return a valid transaction* by calling .rules.playerCanSubmitTransaction method.
     * Returning a transaction for which
     * boardState.playerCanSubmitTransaction(selfState, transaction) == false
     * will immediately throw a [IllegalTransactionException] and end the game.
     */
    abstract fun playTurn(
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ): Transaction

    /**
     * Will be called at most once on each turn to offer a choice between affordable nobles.
     *
     * Called at the end of turns only when there are one or more nobles for which the player
     * does match the requirement in cards (passed as [affordableNobles])
     *
     * Should return one of the objects in [affordableNobles]
     * returned noble will be added to self state nobles set.
     */
    abstract fun chooseNoble(
        affordableNobles: List<Noble>,
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ): Noble
}
