package com.example.pokeapi.Views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeapi.Response.PokeResponse
import com.example.pokeapi.Response.PokeResponseSprite
import dataRetrofit.RetrofitService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class Pokemon(val name: String, val spriteUrl: String)

class MainViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList: StateFlow<List<Pokemon>> get() = _pokemonList

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
            val pokemonNames = fetchPokemonNames()
            if (pokemonNames != null) {
                val pokemons = fetchPokemonSprites(pokemonNames)
                _pokemonList.value = pokemons
            } else {
                _pokemonList.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    private suspend fun fetchPokemonNames(): List<String>? {
        return try {
            val call: Response<PokeResponse> =
                getRetrofit().create(RetrofitService::class.java)
                    .getPokemon("pokemon?limit=10&offset=0")
            val pokemonResponse: PokeResponse? = call.body()
            if (call.isSuccessful && pokemonResponse != null) {
                pokemonResponse.results.map { it.name }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchPokemonSprites(names: List<String>): List<Pokemon> {
        val pokemons = mutableListOf<Pokemon>()
        for (name in names) {
            try {
                val call: Response<PokeResponseSprite> =
                    getRetrofit().create(RetrofitService::class.java)
                        .getPokemonDetails("pokemon/$name")
                val pokeResponseSprite: PokeResponseSprite? = call.body()
                if (call.isSuccessful && pokeResponseSprite != null) {
                    val sprite = pokeResponseSprite.sprites.frontDefault
                    pokemons.add(Pokemon(name.capitalize(), sprite))
                } else {
                    pokemons.add(Pokemon(name.capitalize(), ""))
                }
            } catch (e: Exception) {
                pokemons.add(Pokemon(name.capitalize(), ""))
            }
        }
        return pokemons
    }
}


