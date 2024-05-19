package com.example.pokeapi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeapi.databinding.ItemPokemonBinding

class PokeAdapter(
    private val pokemonList: List<String>,
    private val spriteList: List<String>,
    private val onItemClick: (String, String) -> Unit,

) :
    RecyclerView.Adapter<PokeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokeViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokeViewHolder, position: Int) {
        if (position < pokemonList.size && position < spriteList.size) {
            val pokemonName = pokemonList[position]
            val spriteUrl = spriteList[position]
            holder.bind(pokemonName, spriteUrl)
            holder.itemView.setOnClickListener {
                onItemClick(pokemonName, spriteUrl)
            }
        }
    }

    override fun getItemCount(): Int = pokemonList.size
}