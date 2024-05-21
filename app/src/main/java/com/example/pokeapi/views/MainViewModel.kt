package com.example.pokeapi.views

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeapi.response.PokeResponse
import com.example.pokeapi.response.PokeResponseSprite
import dataRetrofit.RetrofitService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.UnknownHostException
import java.util.Locale

data class Pokemon(
    val name: String,
    val spriteUrl: String,
    val types: String
)

class MainViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList: StateFlow<List<Pokemon>> get() = _pokemonList

    private val maxRetries = 20
    private val retryDelay = 1000L

    init {
        fetchPokemonData()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun fetchPokemonData() {
        _isLoading.value = true
        viewModelScope.launch {
            val totalPokemons = 150
            val batchSize = 50

            val pokemonNames = fetchAllPokemonNames(totalPokemons, batchSize)
            if (pokemonNames.isNotEmpty()) {
                val pokemons = fetchPokemonSpritesAndTypesInBatches(pokemonNames, batchSize)
                _pokemonList.value = pokemons
            } else {
                _pokemonList.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    private suspend fun <T> retry(apiCall: suspend () -> T): T {
        var retries = 0
        while (true) {
            try {
                return apiCall()
            } catch (e: Exception) {
                if (retries >= maxRetries || e !is UnknownHostException) {
                    throw e
                }
                retries++
                delay(retryDelay * retries)
            }
        }
    }

    private suspend fun fetchPokemonNames(limit: Int, offset: Int): List<String>? {
        return retry {
            val call: Response<PokeResponse> =
                getRetrofit().create(RetrofitService::class.java)
                    .getPokemon("pokemon?limit=$limit&offset=$offset")
            val pokemonResponse: PokeResponse? = call.body()
            if (call.isSuccessful && pokemonResponse != null) {
                pokemonResponse.results.map { it.name }
            } else {
                null
            }
        }
    }

    private suspend fun fetchAllPokemonNames(total: Int, batchSize: Int): List<String> {
        val allNames = mutableListOf<String>()
        var offset = 0

        while (offset < total) {
            val names = fetchPokemonNames(batchSize, offset)
            if (names != null) {
                allNames.addAll(names)
                Log.d("fetchAllPokemonNames", "Total names fetched so far: ${allNames.size}")
            } else {
                Log.e("fetchAllPokemonNames", "Failed to fetch names at offset $offset")
                break
            }
            offset += batchSize
        }

        return allNames
    }

    private suspend fun fetchPokemonSpritesAndTypesInBatches(
        names: List<String>,
        batchSize: Int
    ): List<Pokemon> {
        val pokemons = mutableListOf<Pokemon>()
        for (i in names.indices step batchSize) {
            try {
                val batch = names.subList(i, minOf(i + batchSize, names.size))
                for ((index, name) in batch.withIndex()) {
                    val call: Response<PokeResponseSprite> =
                        getRetrofit().create(RetrofitService::class.java)
                            .getPokemonDetails("pokemon/$name")

                    val pokeResponseSprite: PokeResponseSprite? = call.body()

                    val sprite = pokeResponseSprite?.sprites?.frontDefault ?: ""

                    if (sprite.isEmpty()) {
                        continue
                    }

                    val types = pokeResponseSprite?.types?.joinToString(", ") { it ->
                        it.typeDetail.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        }
                    } ?: ""
                    pokemons.add(
                        Pokemon(
                            name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                            sprite,
                            types
                        )
                    )
                    Log.d("fetchPokemonSprites", "Fetched sprite for $name ($index/${batch.size})")
                }
            } catch (e: Exception) {
                Log.e(
                    "fetchPokemonSprites",
                    "Exception fetching com.example.pokeapi.Views.Pokemon batch: ${e.message}"
                )
            }

            _pokemonList.value = pokemons.toList()
            if (i == 0) {
                _isLoading.value = false
            }
        }
        return pokemons
    }
}