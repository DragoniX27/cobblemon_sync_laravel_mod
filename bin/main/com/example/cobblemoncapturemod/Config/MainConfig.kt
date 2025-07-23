package com.example.cobblemoncapturemod.Config

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon

class MainConfig {
    var debug: Boolean = false
    var useCobblemonDropsIfOverrideNotPresent = true
    var granularDropPeriods: Map<String, String> = mapOf()

    fun getGranularDropPeriodFor(pokemon: Pokemon): IntRange? =
        granularDropPeriods.entries.find { (k) ->
            PokemonProperties.parse(k).matches(pokemon)
        }?.value?.let { period ->
            period?.split("-")?.map { it.toInt() }?.let { (start, end) ->
                start..end
            }
        }
}