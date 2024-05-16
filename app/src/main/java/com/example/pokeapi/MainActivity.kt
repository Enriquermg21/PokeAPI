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
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PokeAdapter
    private val pokeObj = mutableListOf<String>()

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
        adapter = PokeAdapter(pokeObj) { pokemonName ->
            val intent = Intent(this, pokeinfoActivity::class.java).apply {
                putExtra("POKEMON_NAME", pokemonName)
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
                    .getPokemon("pokemon?limit=100000&offset=0")
            val pokemonResponse: PokeResponse? = call.body()
            runOnUiThread {
                if (call.isSuccessful && pokemonResponse != null) {
                    val pokemonNames = pokemonResponse.results.map { it.name }
                    pokeObj.clear()
                    pokeObj.addAll(pokemonNames)
                    adapter.notifyDataSetChanged()
                } else {
                    showError()
                }
                showToast()
                hideKeyboard()
            }
        }
    }
    private fun showToast() {
        Toast.makeText(this, "Hasta aqui llega", Toast.LENGTH_LONG).show()
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.main.windowToken, 0)
    }

    private fun showError() {
        Toast.makeText(this, "Error machote", Toast.LENGTH_LONG).show()
    }
}