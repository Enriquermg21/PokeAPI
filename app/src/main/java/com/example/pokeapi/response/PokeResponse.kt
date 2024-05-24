package com.example.pokeapi.response

import com.google.gson.annotations.SerializedName
data class PokeResponse(
    @SerializedName("results") var results: List<PokemonName>
)

data class PokemonName(
    @SerializedName("name") val name: String
)