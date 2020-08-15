package com.github.achrafamil.splendor.api.players

import com.github.achrafamil.splendor.api.data.BoardState
import com.github.achrafamil.splendor.api.data.Color
import com.github.achrafamil.splendor.api.data.PlayerState
import kotlin.random.Random

/**
 * Ryan is a basic player, but with less intelligence: Rayan will make random choices.
 *
 */

class Ryan(name: String = "") : BasicPlayer("Ryan $name") {
    override fun estimateInterestInColors(boardState: BoardState, selfState: PlayerState): Map<Color, Double> {
        return Color
            .values()
            .map { color -> color to Random.nextDouble(1.0) }
            .toMap()
    }
}
