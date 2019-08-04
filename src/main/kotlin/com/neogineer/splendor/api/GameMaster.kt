package com.neogineer.splendor.api

import com.neogineer.splendor.api.data.Board
import com.neogineer.splendor.api.data.Card
import com.neogineer.splendor.api.data.CardCategory
import com.neogineer.splendor.api.data.NameAlreadyTakenException
import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.data.ResourceLoader
import com.neogineer.splendor.api.data.mapToAllColors
import com.neogineer.splendor.api.data.mapToColorMap
import com.neogineer.splendor.api.rules.commit
import com.neogineer.splendor.api.utils.Logger
import com.neogineer.splendor.api.utils.PrintLogger
import com.neogineer.splendor.api.utils.draw
import kotlin.math.min

class GameMaster {

    private val logger: Logger = PrintLogger()

    private val players = mutableMapOf<Player, PlayerState>()

    private lateinit var cardsPiles: Map<CardCategory, MutableSet<Card>>
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
                    name = player.name,
                    tokens = mapToColorMap(),
                    cards = emptySet(),
                    reservedCards = emptySet(),
                    nobles = emptySet(),
                    golds = 0
                )
            }
        }
    }

    fun start() {
        logger.i(LOG_TAG, "starting game")
        makeSureInitialStateIsLegal()
        initializeBoard()

        repeat(2) {
            players.forEach { (player, playerState) ->
                val boardState = board.state
                val transaction = player.playTurn(
                    players.values.minus(playerState),
                    playerState,
                    boardState
                )
                val newPlayerState = board.commit(playerState, transaction)
                players[player] = newPlayerState
                drawMissingCards()

                // TODO
            }
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
        val cards = resourceLoader.loadCards().shuffled()
        val nobles = resourceLoader.loadNobles().shuffled()


        val tokensByColor = when (players.size) {
            2 -> 4
            3 -> 5
            4 -> 7
            else -> throw IllegalStateException()
        }

        cardsPiles = cards.groupBy { it.category }.mapValues { it.value.toMutableSet() }
        board = Board(
            cards = CardCategory.values().map { it to mutableSetOf<Card>() }.toMap(),
            tokens = mapToAllColors(tokensByColor).toMutableMap(),
            nobles = nobles.take(players.size + 1).toMutableSet(),
            gold = 5
        )

        drawMissingCards()
    }

    private fun drawMissingCards() {
        CardCategory.values().forEach { category ->
            val categoryPile = cardsPiles.getValue(category)
            val categoryRevealedCards = board.cards.getValue(category)
            val missingCardsCount = 4 - categoryRevealedCards.size
            if (missingCardsCount > 0) {
                categoryRevealedCards.addAll(
                    categoryPile.draw(min(categoryPile.size, missingCardsCount))
                )
            }
        }
    }

    companion object {
        private val LOG_TAG = GameMaster::class.java.simpleName
    }
}
