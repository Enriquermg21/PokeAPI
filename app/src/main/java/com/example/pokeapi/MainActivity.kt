package com.example.pokeapi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokeapi.databinding.ActivityMainBinding
import com.example.pokeapi.databinding.LoadingViewBinding
import dataRetrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingLoading: LoadingViewBinding
    private lateinit var adapter: PokeAdapter
    private val pokeObj = mutableListOf<String>()
    private val pokeSprite = mutableListOf<String>()
    private var selectedPokemonName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        bindingLoading = LoadingViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoadingView()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PokeAdapter(pokeObj, pokeSprite) { capitalizedPokemonName, pokemonSprite ->
            selectedPokemonName = capitalizedPokemonName
            val intent = Intent(this, pokeinfoActivity::class.java).apply {
                putExtra("POKEMON_NAME", capitalizedPokemonName)
                putExtra("POKEMON_SPRITE", pokemonSprite)
            }
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter

        getPokemonList()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getPokemonList() {
        CoroutineScope(Dispatchers.IO).launch {
            val call: Response<PokeResponse> =
                getRetrofit().create(RetrofitService::class.java)
                    .getPokemon("pokemon?limit=10&offset=0")
            val pokemonResponse: PokeResponse? = call.body()
            runOnUiThread {
                if (call.isSuccessful && pokemonResponse != null) {
                    val capitalizedPokemonNames =
                        pokemonResponse.results.map { it.name.capitalize(Locale.ROOT) }
                    val pokemonNames = pokemonResponse.results.map { it.name }
                    pokeObj.clear()
                    pokeObj.addAll(capitalizedPokemonNames)
                    adapter.notifyDataSetChanged()
                    getPokemonSpritesFull(pokemonNames)
                } else {
                    showError()
                }
                hideKeyboard()
            }
        }
    }

    private fun getPokemonSpritesFull(pokemonNames: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            val sprites = mutableListOf<String>()
            for (name in pokemonNames) {
                try {
                    val call: Response<PokeResponseSprite> =
                        getRetrofit().create(RetrofitService::class.java)
                            .getPokemonDetails("pokemon/$name")
                    val pokeResponseSprite: PokeResponseSprite? = call.body()
                    if (call.isSuccessful && pokeResponseSprite != null) {
                        val sprite = pokeResponseSprite.sprites.frontDefault
                        sprites.add(sprite)

                    } else {
                        sprites.add("")
                    }
                } catch (e: Exception) {
                    sprites.add("")
                }
            }

            withContext(Dispatchers.Main) {
                pokeSprite.clear()
                pokeSprite.addAll(sprites)
                adapter.notifyDataSetChanged()
                hideLoadingView()
                Log.d("getPokemonSpritesFull", "Updated adapter with new sprites")
            }
        }
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.main.windowToken, 0)
    }

    private fun showError() {
        Toast.makeText(this, "Error machote", Toast.LENGTH_LONG).show()
    }

    private fun showLoadingView() {
        binding.layoutloading.loadingView.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        binding.layoutloading.loadingView.visibility = View.GONE
    }
}