package com.github.achrafamil.splendor.api.players

import com.github.achrafamil.splendor.api.Player
import com.github.achrafamil.splendor.api.data.BoardState
import com.github.achrafamil.splendor.api.data.Color
import com.github.achrafamil.splendor.api.data.Noble
import com.github.achrafamil.splendor.api.data.PlayerState
import com.github.achrafamil.splendor.api.data.Transaction
import com.github.achrafamil.splendor.api.rules.TOKENS_LIMIT_BY_PLAYER
import com.github.achrafamil.splendor.api.rules.playerCanSubmitTransaction
import com.github.achrafamil.splendor.api.utils.PrintLogger
import com.github.achrafamil.splendor.api.utils.mergeWith
import kotlin.math.max
import kotlin.random.Random

/**
 * A basic player that will always respect the rules, and eventually win.
 *
 * Strategy is basic:
 *   "If there are cards I can afford, I buy the best one among them (best = the card with the highest points).
 *    Otherwise, I collect the 3 most-needed important tokens"
 *
 */
open class BasicPlayer(playerName: String) : Player(playerName) {
    private val logger = PrintLogger()

    override fun playTurn(
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ): Transaction {
        findTheBestAffordableCard(selfState, boardState)
            ?.let { cardId -> return Transaction.CardBuying(cardId) }

        return buildAnAccurateTokensExchangeTransaction(selfState, boardState)
    }

    protected open fun findTheBestAffordableCard(selfState: PlayerState, boardState: BoardState): Int? {
        return boardState
            .cards
            .values
            .flatten()
            .sortedByDescending { it.points }
            .firstOrNull { card ->
                boardState.playerCanSubmitTransaction(selfState, Transaction.CardBuying(card.id))
            }?.id
    }

    private fun buildAnAccurateTokensExchangeTransaction(
        selfState: PlayerState,
        boardState: BoardState
    ): Transaction.TokensExchange {
        val interestInColors = estimateInterestInColors(boardState, selfState)
        logger.v(name, "interest in colors: $interestInColors")

        val tokens = tokensFromInterestMap(interestInColors, boardState, selfState)

        return Transaction.TokensExchange(tokens)
    }

    protected open fun estimateInterestInColors(
        boardState: BoardState,
        selfState: PlayerState
    ): Map<Color, Double> {
        val interestInColors = mutableMapOf<Color, Double>().withDefault { Random.nextDouble(0.01, 0.1) }

        boardState.cards.values.flatten().forEach { card ->
            val costGap: Map<Color, Int> = card
                .cost
                .mapValues { (color, cost) ->
                    val tokenIHaveOfThisColor = selfState.tokens.getOrDefault(color, 0)
                    val cardIHaveOfThisColor = selfState.cards.filter { it.color == color }.size
                    max(0, cost - tokenIHaveOfThisColor - cardIHaveOfThisColor)
                }.toMap()
                .filterValues { it > 0 }

            costGap
                .mapValues { (_, gap) -> 1.0 / gap } // the smaller the gap the higher the interest is
                .forEach { (color, interest) ->
                    interestInColors[color] = interestInColors.getValue(color) + interest
                }
        }

        Color.values().forEach { color ->
            // add an insignificant amount of interest just to avoid having "zero-interest"
            interestInColors[color] = interestInColors.getOrDefault(color, 0.0) + Random.nextDouble(0.01, 0.1)
        }
        return interestInColors
    }

    protected fun tokensFromInterestMap(
        interestInColors: Map<Color, Double>,
        boardState: BoardState,
        selfState: PlayerState
    ): Map<Color, Int> {
        val interestConsideringBoardConstraint = interestInColors
            .filterKeys { color -> boardState.tokens[color] ?: 0 > 0 }

        val colorsToTake = interestConsideringBoardConstraint
            .toList()
            .sortedByDescending { (_, interest) -> interest }
            .take(3)
            .map { (color, _) -> color }

        val additionalColorsToReachThreeColor = boardState
            .tokens
            .filter { (_, count) -> count > 0 }
            .keys
            .take(3 - colorsToTake.size)

        val mapOfColorsToTake = colorsToTake
            .plus(additionalColorsToReachThreeColor)
            .map { color -> color to 1 }.toMap()
        logger.v(name, "colors to take: $mapOfColorsToTake")

        val colorsToRemove = interestInColors
            .toList()
            .sortedBy { (_, interest) -> interest }
            .filter { (color, _) -> selfState.tokens.getOrDefault(color, mapOfColorsToTake.getOrDefault(color, 0)) > 0 }
            .take(max(0, selfState.tokens.values.sum() + mapOfColorsToTake.size - TOKENS_LIMIT_BY_PLAYER))
            .map { it.first }
            .map { color -> color to -1 }.toMap()
        logger.v(name, "colors to remove $colorsToRemove")

        return mapOfColorsToTake.mergeWith(colorsToRemove)
    }

    override fun chooseNoble(
        affordableNobles: List<Noble>,
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ) = affordableNobles.first()
}
