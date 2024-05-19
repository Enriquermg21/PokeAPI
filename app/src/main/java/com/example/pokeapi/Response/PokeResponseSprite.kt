package com.example.pokeapi.Response

import com.google.gson.annotations.SerializedName

data class PokeResponseSprite(
    @SerializedName("sprites") val sprites: Sprites
)

data class Sprites(
    @SerializedName("front_default") val frontDefault: String
)

data class Types(
    @SerializedName("name") val name: String
)