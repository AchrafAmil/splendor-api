package com.neogineer.splendor.api

import com.neogineer.splendor.api.data.NameAlreadyTakenException
import com.neogineer.splendor.api.utils.Logger
import com.neogineer.splendor.api.utils.PrintLogger
import java.lang.IllegalStateException

class GameMaster {

    private val logger: Logger = PrintLogger()

    private val players = mutableListOf<Player>()

    fun registerPlayer(player: Player) {
        logger.i(LOG_TAG, "registering player: ${player.name}")
        when {
            players.size >= 4 ->
                throw IllegalStateException("can not add more than 4 players")
            players.any { it.name == player.name } ->
                throw NameAlreadyTakenException("there's already a player named ${player.name}")
            else -> players.add(player)
        }
    }
}