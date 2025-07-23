package com.example.cobblemoncapturemod.Events

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.example.cobblemoncapturemod.Checkers.PokemonTypeChecker
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.events.entity.SpawnEvent
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.core.BlockPos
import redis.clients.jedis.Jedis
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer

object SpawnEvents {
    fun load(server: MinecraftServer) {
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe { event: SpawnEvent<PokemonEntity> ->
            // Handle the event when a Pokémon is captured
            val pokemon = event.entity.pokemon
            println("Spwaneo Un Pokemon ${pokemon.species.name}")

            if (!PokemonTypeChecker.checkPokemonType(pokemon)) return@subscribe
            println("Es legendario")

            val jedis = Jedis("localhost", 6379)
            val key = "legendary_captures:${pokemon.species.name}"

            if (jedis.exists(key)) {
                println("Ya existe un legendario con este nombre")
                val originalEntity: PokemonEntity = event.entity
                val pos: BlockPos = originalEntity.blockPosition()

                val newSpecies: String = generateSequence {
                    PokemonSpecies.random().name
                }.first { !jedis.exists("legendary_captures:$it") }


                originalEntity.discard()
                

                server.playerList.broadcastSystemMessage(
                    Component.literal("Ese legendario ya fue capturado, en su remplazo ha aparecido un nuevo shiny."),
                    false
                )
                server.commands.performPrefixedCommand(server.createCommandSourceStack(),"/spawnpokemonat ${pos.x} ${pos.y} ${pos.z} ${newSpecies} shiny lvl=45")
            }
            jedis.close()
        }
    }
}