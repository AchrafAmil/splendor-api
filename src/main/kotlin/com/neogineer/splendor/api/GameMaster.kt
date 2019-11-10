package com.neogineer.splendor.api

import com.neogineer.splendor.api.data.Board
import com.neogineer.splendor.api.data.BoardState
import com.neogineer.splendor.api.data.Card
import com.neogineer.splendor.api.data.CardCategory
import com.neogineer.splendor.api.data.NameAlreadyTakenException
import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.data.ResourceLoader
import com.neogineer.splendor.api.data.TooManyTurnsException
import com.neogineer.splendor.api.data.mapToAllColors
import com.neogineer.splendor.api.data.mapToColorMap
import com.neogineer.splendor.api.rules.commit
import com.neogineer.splendor.api.utils.Logger
import com.neogineer.splendor.api.utils.PrintLogger
import com.neogineer.splendor.api.utils.draw
import kotlin.math.min

class GameMaster {

    val turnsCountLimit = 1000

    private val logger: Logger = PrintLogger()

    private val players = mutableMapOf<Player, PlayerState>()

    private lateinit var cardsPiles: Map<CardCategory, MutableSet<Card>>
    private lateinit var board: Board

    private val winner: PlayerState?
        get() = players.values
            .filter { it.points >= WINNING_POINTS_THRESHOLD }
            .maxBy { it.points }

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

    fun start(gameCallback: GameCallback = GameCallbackAdapter()) {
        logger.i(LOG_TAG, "starting game")
        makeSureInitialStateIsLegal()
        initializeBoard()
        gameCallback.onGameStarted(board.state)

        var turnNumber = 0
        while (winner == null) {
            onNewTurn(turnNumber, gameCallback)
            turnNumber++
        }

        gameCallback.onGameFinished(winner!!, players.values.toList(), board.state)
        logger.i(LOG_TAG, "*******  WINNER IS : ${winner!!}")
    }

    private fun onNewTurn(turnNumber: Int, gameCallback: GameCallback) {
        logger.v(LOG_TAG, "--- turn N°$turnNumber")
        gameCallback.onNewTurn(turnNumber)
        if (turnNumber > turnsCountLimit) {
            throw TooManyTurnsException("Exceeded the maximum number of turns. Infinite game loop?")
        }

        players.forEach { (player, playerState) ->
            val boardState = board.state
            logState(boardState, playerState)
            val opponentsStates = players.values.minus(playerState)
            val transaction = player.playTurn(
                opponentsStates,
                playerState,
                boardState
            )
            gameCallback.onPlayerSubmittedTransaction(playerState, opponentsStates, boardState, transaction)

            val newPlayerState = board.commit(playerState, transaction)
            players[player] = newPlayerState
            drawMissingCards()
        }
    }

    private fun logState(
        boardState: BoardState,
        playerState: PlayerState
    ) {
        logger.v(LOG_TAG, "board first category cards: ${boardState.cards[CardCategory.FIRST]}")
        logger.v(LOG_TAG, "board tokens: ${boardState.tokens}")
        logger.v(LOG_TAG, "player state: $playerState")
        logger.v(LOG_TAG, "player points: ${playerState.points}")
        logger.v(LOG_TAG, "player cards: ${playerState.cards.size}")
        logger.v(
            LOG_TAG,
            "board + piles cards: ${boardState.cards.values.plus(cardsPiles.values).flatten().size}"
        )
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
        private const val WINNING_POINTS_THRESHOLD = 15
    }
}
