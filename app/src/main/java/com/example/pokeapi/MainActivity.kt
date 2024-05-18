package com.example.pokeapi

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokeapi.databinding.ActivityMainBinding
import dataRetrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
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
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PokeAdapter(pokeObj, pokeSprite) { capitalizedPokemonName ->
            selectedPokemonName = capitalizedPokemonName
            val intent = Intent(this, pokeinfoActivity::class.java).apply {}
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter
        val intent = Intent(this, pokeinfoActivity::class.java).apply {}
        startActivity(intent)
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
                        pokemonResponse.results.map { it.name.capitalize() }
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
                        sprites.add("") // Manejar la ausencia de sprite
                    }
                } catch (e: Exception) {
                    sprites.add("") // Manejar la ausencia de sprite en caso de error
                }
            }
            withContext(Dispatchers.Main) {
                pokeSprite.clear()
                pokeSprite.addAll(sprites)
                adapter.notifyDataSetChanged()
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
}