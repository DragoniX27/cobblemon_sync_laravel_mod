package com.example.cobblemoncapturemod.Task

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.MinecraftServer

import com.cobblemon.mod.common.pokemon.Pokemon
import com.example.cobblemoncapturemod.Checkers.PokemonTypeChecker
import com.cobblemon.mod.common.Cobblemon

import redis.clients.jedis.Jedis
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import com.google.gson.Gson

object SyncPlayersTask {
    private var tickCounter = 0L
    private const val TICKS_INTERVAL = 20L * 300L
    private val gson = Gson()


    fun register(server: MinecraftServer) {
        ServerTickEvents.END_SERVER_TICK.register { srv ->
            tickCounter++
            if (tickCounter >= TICKS_INTERVAL) {
                tickCounter = 0
                SyncPlayers(srv)
            }
        }
    }

    private fun SyncPlayers(server: MinecraftServer) {
        val players = server.playerList.players
        players.forEach { player ->
            Jedis("redis", 6379).use { jedis ->
                jedis.publish("cobblemon.syncPlayer", player.uuid.toString())
            }
        }

    }
}