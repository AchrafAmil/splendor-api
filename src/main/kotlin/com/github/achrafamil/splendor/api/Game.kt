package com.github.achrafamil.splendor.api

import com.github.achrafamil.splendor.api.data.Board
import com.github.achrafamil.splendor.api.data.BoardState
import com.github.achrafamil.splendor.api.data.Card
import com.github.achrafamil.splendor.api.data.CardCategory
import com.github.achrafamil.splendor.api.data.NameAlreadyTakenException
import com.github.achrafamil.splendor.api.data.Noble
import com.github.achrafamil.splendor.api.data.PlayerState
import com.github.achrafamil.splendor.api.data.ResourceLoader
import com.github.achrafamil.splendor.api.data.TooManyTurnsException
import com.github.achrafamil.splendor.api.data.mapToAllColors
import com.github.achrafamil.splendor.api.data.mapToColorMap
import com.github.achrafamil.splendor.api.rules.canAffordNoble
import com.github.achrafamil.splendor.api.rules.commit
import com.github.achrafamil.splendor.api.utils.Logger
import com.github.achrafamil.splendor.api.utils.PrintLogger
import com.github.achrafamil.splendor.api.utils.draw
import kotlin.math.min

/**
 * Main class to play a game.
 * 1 - create an instance of Game;
 * 2 - register your own implementation (or one of the ready-to-use implementations at .api.players.*);
 * 3 - call start method with a callback. Its methods will be triggered as game progresses.
 */
class Game(
    private val logger: Logger = PrintLogger()
) {
    private val players = mutableMapOf<Player, PlayerState>()

    private lateinit var cardsPiles: Map<CardCategory, MutableSet<Card>>
    private lateinit var board: Board

    private val winner: PlayerState?
        get() = players.values
            .filter { it.points >= WINNING_POINTS_THRESHOLD }
            .maxBy { it.points }

    /**
     * register your own implementation (or one of the ready-to-use implementations at .api.players.*)
     */
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

    /**
     * start the game.
     * Keep in mind this call is sync and method will not return until game ends.
     * Callback (if any) methods will be triggered as game progresses.
     * Make sure your register at least 2 and up to 4 players before calling start.
     */
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

        logger.i(LOG_TAG, "*******  WINNER IS : ${winner!!}")
        gameCallback.onGameFinished(winner!!, players.values.toList(), board.state)
    }

    private fun onNewTurn(turnNumber: Int, gameCallback: GameCallback) {
        logger.v(LOG_TAG, "--- turn N°$turnNumber")
        gameCallback.onNewTurn(turnNumber)
        if (turnNumber > MAX_TURNS_COUNT) {
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

            val affordableNobles = board.nobles.filter { newPlayerState.canAffordNoble(it) }
            if (affordableNobles.isNotEmpty()) {
                val chosenNoble = player.chooseNoble(affordableNobles, opponentsStates, newPlayerState, board.state)

                assignNobleToPlayer(chosenNoble, player)
            }
        }
    }

    private fun assignNobleToPlayer(noble: Noble, player: Player) {
        logger.i(LOG_TAG, "assigning noble ($noble) to player ${player.name}")
        if (board.nobles.remove(noble)) {
            val playerState = players[player]!!
            val newPlayerState = playerState.copy(nobles = playerState.nobles.plus(noble))
            players[player] = newPlayerState
        } else {
            throw IllegalStateException("Noble $noble not found in board ${board.nobles}")
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
        logger.v(LOG_TAG, "board + piles cards: ${boardState.cards.values.plus(cardsPiles.values).flatten().size}")
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
            val missingCardsCount = MAX_VISIBLE_CARDS_PER_CATEGORY - categoryRevealedCards.size
            if (missingCardsCount > 0) {
                categoryRevealedCards.addAll(
                    categoryPile.draw(min(categoryPile.size, missingCardsCount))
                )
            }
        }
    }

    companion object {
        private val LOG_TAG = Game::class.java.simpleName
        private const val WINNING_POINTS_THRESHOLD = 15
        private const val MAX_VISIBLE_CARDS_PER_CATEGORY = 4
        private const val MAX_TURNS_COUNT = 1000
    }
}
