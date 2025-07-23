package com.example.cobblemoncapturemod.Events

import com.cobblemon.mod.common.api.events.CobblemonEvents
import redis.clients.jedis.Jedis
import com.google.gson.JsonObject
import com.google.gson.Gson
import com.example.cobblemoncapturemod.Checkers.PokemonTypeChecker


object CaptureEvents {
    private val gson = Gson()

    fun load() {
        CobblemonEvents.POKEMON_CAPTURED.subscribe { event ->
            // Handle the event when a Pokémon is captured
            val player = event.player
            val pokemon = event.pokemon

            // Example: Log the capture event
            //println("${player.name} captured a ${pokemon.species.name}!")

            val types = pokemon.form?.types ?: pokemon.species.types

            val typesAsStrings = types.map { it.name.lowercase() }

            val json = JsonObject().apply {
                addProperty("pokemon_uuid", pokemon.uuid.toString())
                addProperty("pokemon_species", pokemon.species.name)
                addProperty("pokemon_species_id", pokemon.species.nationalPokedexNumber)
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
            }

            Jedis("redis", 6379).use { jedis ->
                jedis.publish("cobblemon.pokemon_captured", json.toString())
            }
        }
    }
}