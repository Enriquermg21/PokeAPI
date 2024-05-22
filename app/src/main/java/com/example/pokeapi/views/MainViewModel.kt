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

    val _isLoading2 = MutableStateFlow(false)
    val isLoading2: StateFlow<Boolean> get() = _isLoading2

    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList: StateFlow<List<Pokemon>> get() = _pokemonList

    private var offset = 0
    private val limit = 20
    private val maxRetries = 50
    private val retryDelay = 1000L
    private var isLoadingSet = false

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
        if (!isLoadingSet) {
            _isLoading.value = true
            isLoadingSet = true // Marca isLoading como true por primera vez
        } else {
            _isLoading2.value = true
        }
        viewModelScope.launch {
            try {
                val pokemonNames = fetchPokemonNames(limit, offset)
                if (pokemonNames != null) {
                    val pokemons = fetchPokemonSpritesAndTypes(pokemonNames)
                    _pokemonList.value += pokemons
                    offset += limit
                } else {
                    _pokemonList.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error al cargar los datos de los Pokémon: ${e.message}")
            } finally {
                _isLoading.value = false
                _isLoading2.value = false
            }
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
            if (call.isSuccessful) {
                val pokemonResponse: PokeResponse? = call.body()
                pokemonResponse?.results?.map { it.name }
            } else {
                null
            }
        }
    }

    private suspend fun fetchPokemonSpritesAndTypes(names: List<String>): List<Pokemon> {
        val pokemons = mutableListOf<Pokemon>()
        for (name in names) {
            try {
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
                Log.d("MainViewModel", "Fetched sprite for $name")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching details for $name: ${e.message}")
            }
        }
        return pokemons
    }

    fun loadMorePokemon() {
        fetchPokemonData()
    }
}