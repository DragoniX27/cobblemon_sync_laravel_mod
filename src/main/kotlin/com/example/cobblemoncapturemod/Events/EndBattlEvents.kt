package com.example.cobblemoncapturemod.Events

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor
import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import redis.clients.jedis.Jedis
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import com.google.gson.Gson

object EndBattleEvents {
    private val gson = Gson()

    fun load() {
        CobblemonEvents.BATTLE_VICTORY.subscribe { event ->

            if (event.battle.isPvW) return@subscribe

            val winnersJson = JsonArray()
            val losersJson = JsonArray()
            event.winners.forEach { actor ->
                val json = JsonObject()
                json.addProperty("uuid", actor.uuid.toString())
                json.addProperty("name", actor.getName().string) // 👈 aquí el cambio
                json.addProperty("type", if (actor.type == ActorType.PLAYER) "player" else "npc")
                winnersJson.add(json)
            }

            event.losers.forEach { actor ->
                val json = JsonObject()
                json.addProperty("uuid", actor.uuid.toString())
                json.addProperty("name", actor.getName().string) // 👈 aquí también
                json.addProperty("type", if (actor.type == ActorType.PLAYER) "player" else "npc")
                losersJson.add(json)
            }
            val result = JsonObject()
            result.add("winners", winnersJson)
            result.add("losers", losersJson)

            Jedis("redis", 6379).use { jedis ->
                jedis.publish("cobblemon.pokemon_battle", result.toString())
            }
        }
    }
}