package com.example.pokeapi

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeapi.databinding.ItemPokemonBinding
import java.util.Locale

class PokemonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding: ItemPokemonBinding = ItemPokemonBinding.bind(view)
    fun bind(pokemonName: String, clickListener: (String) -> Unit) {
        val capitalizedPokemonName = pokemonName.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        }
        binding.pokemonName.text = capitalizedPokemonName
        binding.root.setOnClickListener {
            clickListener(pokemonName)
        }
    }
}