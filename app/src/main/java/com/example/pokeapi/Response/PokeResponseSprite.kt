package com.example.pokeapi.Response

import com.google.gson.annotations.SerializedName

data class PokeResponseSprite(
    @SerializedName("sprites") val sprites: Sprites,
    @SerializedName("types") val types: List<TypeSlot>
)

data class Sprites(
    @SerializedName("front_default") val frontDefault: String
)

data class TypeSlot(
    @SerializedName("slot") val slot: Int,
    @SerializedName("type") val typeDetail: TypeDetail
)

data class TypeDetail(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)



