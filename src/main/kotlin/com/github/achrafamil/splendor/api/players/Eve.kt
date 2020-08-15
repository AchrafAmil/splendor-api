package com.github.achrafamil.splendor.api.players

import com.github.achrafamil.splendor.api.data.BoardState
import com.github.achrafamil.splendor.api.data.Color
import com.github.achrafamil.splendor.api.data.PlayerState
import com.github.achrafamil.splendor.api.data.Transaction
import com.github.achrafamil.splendor.api.rules.playerCanSubmitTransaction
import kotlin.math.max
import kotlin.random.Random

/**
 * Eve is a basic player but with a bit of rivalry:
 * Eve will always try to prevent their opponent from getting their most interesting cards
 *
 * On average, when playing against [BasicPlayer] Eve has a chance of winning equal to 45%
 */

class Eve(name: String = "") : BasicPlayer("Eve $name") {

    private lateinit var opponent: PlayerState

    override fun playTurn(
        opponentsStates: List<PlayerState>,
        selfState: PlayerState,
        boardState: BoardState
    ): Transaction {
        opponent = opponentsStates.first()
        nobleDrivenBestAffordableCard(selfState, boardState)
            ?.let { cardId -> return Transaction.CardBuying(cardId) }

        return super.playTurn(opponentsStates, selfState, boardState)
    }

    private fun nobleDrivenBestAffordableCard(selfState: PlayerState, boardState: BoardState): Int? {
        val interestInCardColors = computeNobleDrivenInterestInCardColors(opponent, boardState)

        return boardState
            .cards
            .values
            .flatten()
            .sortedByDescending { it.points + interestInCardColors.getOrDefault(it.color, .0) }
            .firstOrNull { card ->
                boardState.playerCanSubmitTransaction(selfState, Transaction.CardBuying(card.id))
            }?.id
    }

    protected fun computeNobleDrivenInterestInCardColors(
        selfState: PlayerState,
        boardState: BoardState
    ): Map<Color, Double> {
        val interestInCardColors = mutableMapOf<Color, Double>().withDefault { Random.nextDouble(0.01, 0.1) }

        // go throw each noble and increment interest in its needed cards colors
        boardState.nobles.forEach { noble ->
            val costGap: Map<Color, Int> = noble
                .cost
                .mapValues { (color, cost) ->
                    val cardsIHaveOfThisColor = selfState.cards.filter { it.color == color }.size
                    max(0, cost - cardsIHaveOfThisColor)
                }.toMap()
                .filterValues { it > 0 }

            costGap
                .mapValues { (_, gap) -> 1.0 / gap } // the smaller the gap the higher the interest is
                .forEach { (color, interest) ->
                    interestInCardColors[color] = interestInCardColors.getValue(color) + interest
                }
        }
        return interestInCardColors
    }

    override fun estimateInterestInColors(boardState: BoardState, selfState: PlayerState): Map<Color, Double> {
        return super.estimateInterestInColors(boardState, opponent)
    }
}
