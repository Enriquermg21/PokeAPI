package com.example.pokeapi.response

import com.google.gson.annotations.SerializedName
data class PokeResponse(
    @SerializedName("count") var counter: Int,
    @SerializedName("next") var next: String?,
    @SerializedName("results") var results: List<PokemonName>
)

data class PokemonName(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)