package com.example.pokeapi.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokeapi.R
import com.example.pokeapi.databinding.ItemPokemonBinding
import com.example.pokeapi.databinding.PokeinfoBinding
import com.example.pokeapi.views.Pokemon

class PokeViewHolder(
    private val binding: ItemPokemonBinding,
    private val bindingInfo: PokeinfoBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pokemon: Pokemon) {
        binding.pokemonName.text = pokemon.name
        bindingInfo.tvTypeinfo.text = pokemon.types
        if (pokemon.spriteUrl.isNotEmpty()) {
            Glide.with(itemView.context)
                .load(pokemon.spriteUrl)
                .centerInside()
                .into(binding.pokemonImage)
        } else {
            binding.pokemonImage.setImageResource(R.drawable.ic_launcher_background)
        }
    }
}

