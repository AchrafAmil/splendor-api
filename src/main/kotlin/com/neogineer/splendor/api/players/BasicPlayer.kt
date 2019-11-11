package com.neogineer.splendor.api.players

import com.neogineer.splendor.api.Player
import com.neogineer.splendor.api.data.BoardState
import com.neogineer.splendor.api.data.Color
import com.neogineer.splendor.api.data.PlayerState
import com.neogineer.splendor.api.data.Transaction
import com.neogineer.splendor.api.rules.TOKENS_LIMIT_BY_PLAYER
import com.neogineer.splendor.api.rules.playerCanSubmitTransaction
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
class BasicPlayer(playerName: String) : Player("BasicPlayer $playerName") {

    override fun playTurn(
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ): Transaction {
        findTheBestAffordableCard(selfState, boardState)
            ?.let { cardId -> return Transaction.CardBuying(cardId) }

        return buildAnAccurateTokensExchangeTransaction(selfState, boardState)
    }

    private fun buildAnAccurateTokensExchangeTransaction(
        selfState: PlayerState,
        boardState: BoardState
    ): Transaction.TokensExchange {
        val interestInColors = mutableMapOf<Color, Double>()

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
                    interestInColors[color] = interestInColors.getOrDefault(color, 0.0) + interest
                }
        }

        Color.values().forEach { color ->
            // add an insignificant amount of interest just to avoid having "zero-interest"
            interestInColors[color] = interestInColors.getOrDefault(color, 0.0) + Random.nextDouble(0.01, 0.1)
        }

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

        val colorsToRemove = interestConsideringBoardConstraint
            .toList()
            .sortedBy { (_, interest) -> interest }
            .filter { (color, _) -> selfState.tokens.getOrDefault(color, 0) > 0 }
            .take(max(0, selfState.tokens.values.sum() + mapOfColorsToTake.size - TOKENS_LIMIT_BY_PLAYER))
            .map { it.first }
            .map { color -> color to -1 }.toMap()

        return Transaction.TokensExchange(mapOfColorsToTake + colorsToRemove)
    }

    private fun findTheBestAffordableCard(selfState: PlayerState, boardState: BoardState): Int? {
        return boardState
            .cards
            .values
            .flatten()
            .sortedByDescending { it.points }
            .firstOrNull { card ->
                boardState.playerCanSubmitTransaction(selfState, Transaction.CardBuying(card.id))
            }?.id
    }
}
