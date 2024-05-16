package dataRetrofit

import com.example.pokeapi.PokeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET("pokemon") // Ajusta tu endpoint aquí
    fun getPokemon(@Url url: String): Response<PokeResponse>
}