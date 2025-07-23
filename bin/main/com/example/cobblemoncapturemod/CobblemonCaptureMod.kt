package com.example.cobblemoncapturemod

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import com.example.cobblemoncapturemod.Config.ConfigBuilder
import com.example.cobblemoncapturemod.Config.MainConfig
import com.example.cobblemoncapturemod.Events.PokemonEvents
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.MinecraftServer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import com.example.cobblemoncapturemod.Task.SyncPlayersTask
import com.example.cobblemoncapturemod.Listener.CommandRedisListener

class CobblemonCaptureMod : ModInitializer {
    companion object {
        const val MOD_ID = "cobblemon_capture_mod"
        lateinit var config: MainConfig
        lateinit var server: MinecraftServer
    }

    override fun onInitialize() {
        config = ConfigBuilder.load(MainConfig::class.java, MOD_ID)
        PokemonEvents.load()

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            SyncPlayersTask.register(server)
            CommandRedisListener.start(server)
        }
    }
}