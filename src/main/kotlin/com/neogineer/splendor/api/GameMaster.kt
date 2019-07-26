package com.neogineer.splendor.api

import com.neogineer.splendor.api.data.NameAlreadyTakenException
import java.lang.IllegalStateException

class GameMaster {

    private val players = mutableListOf<Player>()

    fun registerPlayer(player: Player) {
        when {
            players.size >= 4 ->
                throw IllegalStateException("can not add more than 4 players")
            players.any { it.name == player.name } ->
                throw NameAlreadyTakenException("there's already a player named ${player.name}")
            else -> players.add(player)
        }
    }
}