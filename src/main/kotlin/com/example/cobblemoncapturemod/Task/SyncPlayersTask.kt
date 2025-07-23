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
            val trainerJson = JsonObject()
            trainerJson.addProperty("uuid", player.uuid.toString())
            val pokemonsJsonArray = JsonArray()
            val pc = Cobblemon.storage.getPC(player)
            val party = Cobblemon.storage.getParty(player)
            party.forEach { pokemon ->
                val types = pokemon.form?.types ?: pokemon.species.types

                val typesAsStrings = types.map { it.name.lowercase() }
                val json = JsonObject().apply {
                    addProperty("pokemon_uuid", pokemon.uuid.toString())
                    addProperty("pokemon_species", pokemon.species.name)
                    addProperty("pokemon_shiny", pokemon.shiny.toString())
                    addProperty("pokemon_nickname", pokemon.nickname.toString() ?: "")
                    addProperty("pokemon_gender", pokemon.gender.name)
                    addProperty("pokemon_form", pokemon.form?.name ?: "")
                    addProperty("pokemon_capturedBall", pokemon.caughtBall.toString() ?: "")
                    addProperty("pokemon_originalTrainer", pokemon.originalTrainer?.toString() ?: "")
                    addProperty("pokemon_is_legendary", PokemonTypeChecker.checkPokemonType(pokemon))
                    add("pokemon_types", gson.toJsonTree(typesAsStrings))
                    addProperty("pokemon_form", pokemon.form?.name?.lowercase() ?: "default")
                    addProperty("pokemon_level", pokemon.level)
                    addProperty("pokemon_team", true)
                }

                pokemonsJsonArray.add(json)
            }
            pc.forEach { pokemon ->
                val types = pokemon.form?.types ?: pokemon.species.types

                val typesAsStrings = types.map { it.name.lowercase() }

                val json = JsonObject().apply {
                    addProperty("pokemon_uuid", pokemon.uuid.toString())
                    addProperty("pokemon_species", pokemon.species.name)
                    addProperty("pokemon_shiny", pokemon.shiny.toString())
                    addProperty("pokemon_nickname", pokemon.nickname.toString() ?: "")
                    addProperty("pokemon_gender", pokemon.gender.name)
                    addProperty("pokemon_form", pokemon.form?.name ?: "")
                    addProperty("pokemon_capturedBall", pokemon.caughtBall.toString() ?: "")
                    addProperty("pokemon_originalTrainer", pokemon.originalTrainer?.toString() ?: "")
                    addProperty("pokemon_is_legendary", PokemonTypeChecker.checkPokemonType(pokemon))
                    add("pokemon_types", gson.toJsonTree(typesAsStrings))
                    addProperty("pokemon_form", pokemon.form?.name?.lowercase() ?: "default")
                    addProperty("pokemon_level", pokemon.level)
                    addProperty("pokemon_team", false)
                }

                pokemonsJsonArray.add(json)
            }
            trainerJson.add("pokemons", pokemonsJsonArray)

            Jedis("redis", 6379).use { jedis ->
                jedis.publish("cobblemon.syncPlayer", trainerJson.toString())
            }
        }

    }
}