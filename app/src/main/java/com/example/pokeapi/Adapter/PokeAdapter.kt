package com.example.pokeapi.Adapter

import Pokemon
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeapi.databinding.ItemPokemonBinding
import com.example.pokeapi.databinding.PokeinfoBinding

class PokeAdapter(
    private var pokemonList: List<Pokemon>,
    private val onItemClick: (String, String, String) -> Unit
) : RecyclerView.Adapter<PokeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokeViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val bindinginfo =
            PokeinfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokeViewHolder(binding, bindinginfo)
    }

    override fun onBindViewHolder(holder: PokeViewHolder, position: Int) {
        val pokemon = pokemonList[position]
        holder.bind(pokemon)
        holder.itemView.setOnClickListener {
            onItemClick(pokemon.name, pokemon.spriteUrl, pokemon.types)
        }
    }

    override fun getItemCount(): Int = pokemonList.size

    fun updateList(newPokemonList: List<Pokemon>) {
        pokemonList = newPokemonList
        notifyDataSetChanged()
    }
}

