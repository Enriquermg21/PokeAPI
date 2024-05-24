package com.example.pokeapi.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeapi.adapter.PokeAdapter
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
        setupRecyclerView()
        observeViewModel()

    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = PokeAdapter(emptyList()) { name, sprite, type ->
            val intent = Intent(this, PokeinfoActivity::class.java).apply {
                putExtra("POKEMON_NAME", name)
                putExtra("POKEMON_SPRITE", sprite)
                putExtra("POKEMON_TYPE", type)
            }
            startActivity(intent)
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    mainViewModel.loadMorePokemon()
                }
            }
        })
        binding.recyclerView.adapter = adapter
    }
    private fun observeViewModel() {
        lifecycleScope.launch {
            mainViewModel.isLoading.collectLatest { isLoading ->
                binding.layoutloading.loadingView.visibility =
                    if (isLoading) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            mainViewModel.isLoading2.collectLatest { isLoading2 ->
                binding.layoutloadingwithoutback.loadingViewwithoutback.visibility =
                    if (isLoading2) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            mainViewModel.pokemonList.collect { pokemons ->
                adapter.updateList(pokemons)
            }
        }
    }
}