package com.example.cobblemoncapturemod.Listener

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.minecraft.server.MinecraftServer
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPubSub

object CommandRedisListener {
    lateinit var server: MinecraftServer

    fun start(server: MinecraftServer) {
        this.server = server

        Thread {
            val jedis = Jedis("redis", 6379)
            jedis.subscribe(object : JedisPubSub() {
                override fun onMessage(channel: String, message: String) {
                    if (channel == "laravel:commands") {
                        try {
                            val json = Json.decodeFromString<CommandMessage>(message)
                            println("[Redis] Ejecutando múltiples comandos: ${json.commands}")

                            json.commands.forEach { command ->
                                runCommandAsConsole(command)
                            }
                        } catch (e: Exception) {
                            println("[Redis] Error al procesar comandos JSON: ${e.message}")
                        }
                    }
                }
            }, "laravel:commands")
        }.start()
    }

    private fun runCommandAsConsole(command: String) {
        server.submit {
            server.commands.performPrefixedCommand(server.createCommandSourceStack(), command)
        }
    }
}

@Serializable
data class CommandMessage(
    val commands: List<String>
)
