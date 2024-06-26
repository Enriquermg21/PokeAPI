package dataRetrofit

import com.example.pokeapi.response.PokeResponse
import com.example.pokeapi.response.PokeResponseSprite
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitService {
    @GET
    suspend fun getPokemon(@Url url: String): Response<PokeResponse>

    @GET
    suspend fun getPokemonDetails(@Url url: String): Response<PokeResponseSprite>
}
