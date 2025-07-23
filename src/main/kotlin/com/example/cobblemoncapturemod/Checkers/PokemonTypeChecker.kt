package com.example.cobblemoncapturemod.Checkers

import com.cobblemon.mod.common.pokemon.Pokemon

object PokemonTypeChecker {
    fun checkPokemonType(pokemon: Pokemon): Boolean {
        val labels = pokemon.species.labels
        return labels.contains("legendary") || labels.contains("mythical")
    }
}
