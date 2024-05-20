import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeapi.Response.PokeResponse
import com.example.pokeapi.Response.PokeResponseSprite
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


    private val MAX_RETRIES = 200
    private val RETRY_DELAY_MS = 1000L
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
            val totalPokemons = 300
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
                if (retries >= MAX_RETRIES || e !is UnknownHostException) {
                    throw e // Si el número máximo de reintentos ha sido alcanzado o no es un error de conexión, lanzar la excepción
                }
                retries++
                delay(RETRY_DELAY_MS * retries) // Esperar antes de intentar de nuevo
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
            val batch = names.subList(i, minOf(i + batchSize, names.size))
            val batchPokemons = fetchPokemonSpritesAndTypes(batch)
            pokemons.addAll(batchPokemons)
            Log.d("fetchPokemonData", "Fetched ${pokemons.size} Pokemon so far")

            // Update the view with the currently loaded pokemons
            _pokemonList.value = pokemons.toList()

            // Stop showing loading screen after the first batch
            if (i == 0) {
                _isLoading.value = false
            }
        }
        return pokemons
    }

    private suspend fun fetchPokemonSpritesAndTypes(names: List<String>): List<Pokemon> {
        val pokemons = mutableListOf<Pokemon>()
        for ((index, name) in names.withIndex()) {
            try {
                val call: Response<PokeResponseSprite> =
                    getRetrofit().create(RetrofitService::class.java)
                        .getPokemonDetails("pokemon/$name")

                val pokeResponseSprite: PokeResponseSprite? = call.body()

                val sprite = pokeResponseSprite?.sprites?.frontDefault ?: ""

                if (sprite.isEmpty()) {
                    Log.w("fetchPokemonSprites", "No sprite found for $name")
                    continue
                }

                val types = pokeResponseSprite?.types?.joinToString(", ") {
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
                Log.d("fetchPokemonSprites", "Fetched sprite for $name ($index/${names.size})")
            } catch (e: Exception) {
                Log.e("fetchPokemonSprites", "Exception fetching $name: ${e.message}")
            }
        }
        Log.d("fetchPokemonSprites", "Total sprites fetched: ${pokemons.size}")
        return pokemons
    }
}
