package com.example.pokeapi

import com.google.gson.annotations.SerializedName
import java.util.Objects

data class PokeResponse(
    @SerializedName("count") var counter: Int,
    @SerializedName("next") var next: String,
    @SerializedName("results") var results: List<Objects>
)