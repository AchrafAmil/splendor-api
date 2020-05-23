package com.github.achrafamil.splendor.api.players

import com.github.achrafamil.splendor.api.data.BoardState
import com.github.achrafamil.splendor.api.data.Color
import com.github.achrafamil.splendor.api.data.PlayerState
import com.github.achrafamil.splendor.api.data.Transaction
import com.github.achrafamil.splendor.api.rules.playerCanSubmitTransaction
import com.github.achrafamil.splendor.api.utils.PrintLogger
import kotlin.math.max
import kotlin.random.Random

/**
 * Johanna is a basic player but with some enhancements:
 *
 * Her interest in cards is also influenced by nobles in the game.
 * She will favor cards that help reach a noble.
 *
 * On average, when playing against [BasicPlayer] Johanna has a chance of winning equal to 64%
 */
class Johanna(name: String = "") : BasicPlayer("Johanna $name") {
    private val logger = PrintLogger()

    override fun findTheBestAffordableCard(selfState: PlayerState, boardState: BoardState) =
        nobleDrivenBestAffordableCard(selfState, boardState)

    private fun nobleDrivenBestAffordableCard(selfState: PlayerState, boardState: BoardState): Int? {
        val interestInCardColors = computeNobleDrivenInterestInCardColors(selfState, boardState)

        return boardState
            .cards
            .values
            .flatten()
            .sortedByDescending { it.points + interestInCardColors.getOrDefault(it.color, .0) * INTEREST_COEFFICIENT }
            .firstOrNull { card ->
                boardState.playerCanSubmitTransaction(selfState, Transaction.CardBuying(card.id))
            }?.id
    }

    private fun computeNobleDrivenInterestInCardColors(
        selfState: PlayerState,
        boardState: BoardState
    ): Map<Color, Double> {
        logger.v(name, "setting up noble goal")

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
        logger.v(name, "interest in card colors: $interestInCardColors")
        return interestInCardColors
    }

    companion object {
        private const val INTEREST_COEFFICIENT = 1f
    }
}
