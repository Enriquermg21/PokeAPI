package com.example.pokeapi

import com.google.gson.annotations.SerializedName

data class PokeResponseSprite(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("sprites") val sprites: Sprites
)

data class Sprites(
    @SerializedName("front_default") val frontDefault: String
)