package com.neogineer.splendor.api

import com.neogineer.splendor.api.data.Board
import com.neogineer.splendor.api.data.IllegalTransactionException
import com.neogineer.splendor.api.data.NameAlreadyTakenException
import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.data.ResourceLoader
import com.neogineer.splendor.api.data.Transaction
import com.neogineer.splendor.api.data.mapToAllColors
import com.neogineer.splendor.api.data.mapToColorMap
import com.neogineer.splendor.api.utils.Logger
import com.neogineer.splendor.api.utils.PrintLogger

class GameMaster {

    private val logger: Logger = PrintLogger()

    private val players = mutableMapOf<Player, PlayerState>()

    private lateinit var board: Board

    fun registerPlayer(player: Player) {
        logger.i(LOG_TAG, "registering player: ${player.name}")
        when {
            players.size >= 4 ->
                throw IllegalStateException("can not add more than 4 players")
            players.any { it.key.name == player.name } ->
                throw NameAlreadyTakenException("there's already a player named ${player.name}")
            else -> {
                players[player] = PlayerState(
                    player.name,
                    mapToColorMap(),
                    emptySet(),
                    emptySet(),
                    0
                )
            }
        }
    }

    fun start() {
        logger.i(LOG_TAG, "starting game")
        makeSureInitialStateIsLegal()
        initializeBoard()

        players.forEach { (player, playerState) ->
            val boardState = board.state
            val transaction = player.playTurn(
                players.values.minus(playerState),
                playerState,
                boardState
            )
            if (transaction is Transaction.TokensExchange) {
                if (boardState.playerCanSubmitTransaction(playerState, transaction)) {
                    // TODO
                } else {
                    throw IllegalTransactionException("$transaction is not valid or board/player can't afford it")
                }
            }
            // TODO
        }
        // TODO
    }

    private fun makeSureInitialStateIsLegal() {
        when {
            players.size < 2 -> {
                throw IllegalStateException("Should register at least two players before starting the game")
            }
            else -> {
                logger.v(LOG_TAG, "initial state is legal")
            }
        }
    }

    private fun initializeBoard() {
        val resourceLoader = ResourceLoader()
        val cards = resourceLoader.loadCards()
        val nobles = resourceLoader.loadNobles()
        val tokensByColor = when (players.size) {
            2 -> 4
            3 -> 5
            4 -> 7
            else -> throw IllegalStateException()
        }

        board = Board(
            cards = cards.groupBy { it.category }.mapValues { it.value.toSet() }.toMutableMap(),
            tokens = mapToAllColors(tokensByColor).toMutableMap(),
            nobles = nobles.toMutableSet(),
            gold = 5
        )
    }

    companion object {
        private val LOG_TAG = GameMaster::class.java.simpleName
    }
}