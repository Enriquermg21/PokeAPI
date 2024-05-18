package com.example.pokeapi

import com.google.gson.annotations.SerializedName

data class PokeResponseSprite(
    // @SerializedName("types") val types: Types,
    @SerializedName("sprites") val sprites: Sprites

)

data class Sprites(
    @SerializedName("front_default") val frontDefault: String
)

data class Types(
    @SerializedName("name") val name: String
)