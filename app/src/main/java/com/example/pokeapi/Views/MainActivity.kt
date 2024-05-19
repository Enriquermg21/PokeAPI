package com.example.pokeapi.Views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokeapi.Adapter.PokeAdapter
import com.example.pokeapi.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PokeAdapter
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = PokeAdapter(emptyList(), emptyList()) { capitalizedPokemonName, pokemonSprite ->
            val intent = Intent(this, pokeinfoActivity::class.java).apply {
                putExtra("POKEMON_NAME", capitalizedPokemonName)
                putExtra("POKEMON_SPRITE", pokemonSprite)
            }
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            mainViewModel.isLoading.collectLatest { isLoading ->
                binding.layoutloading.loadingView.visibility =
                    if (isLoading) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            mainViewModel.pokemonList.collectLatest { pokemonList ->
                val names = pokemonList.map { it.name }
                val sprites = pokemonList.map { it.spriteUrl }
                adapter.updateData(names, sprites)
            }
        }
    }

    private fun showError() {
        Toast.makeText(this, "Error machote", Toast.LENGTH_LONG).show()
    }
}
