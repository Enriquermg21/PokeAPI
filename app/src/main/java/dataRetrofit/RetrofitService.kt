package dataRetrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    @GET("https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc")
    suspend fun pokemonlist(
        @Query("") apiKey: String,
        @Query("") region: String

    )
}