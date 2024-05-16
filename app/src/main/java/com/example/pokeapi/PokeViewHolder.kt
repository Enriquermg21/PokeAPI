package com.example.pokeapi

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeapi.databinding.ItemPokemonBinding

class PokemonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding: ItemPokemonBinding = ItemPokemonBinding.bind(view)
}