package com.example.pokeapi.Adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokeapi.R
import com.example.pokeapi.databinding.ItemPokemonBinding


class PokeViewHolder(private val binding: ItemPokemonBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(name: String, spriteUrl: String) {
        binding.pokemonName.text = name
        if (spriteUrl.isNotEmpty()) {
            Glide.with(itemView.context)
                .load(spriteUrl)
                .centerInside()
                .into(binding.pokemonImage)
        } else {
            binding.pokemonImage.setImageResource(R.drawable.ic_launcher_background) // Usa una imagen de marcador de posición
        }
    }
}