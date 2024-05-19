package com.example.pokeapi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pokeapi.databinding.PokeinfoBinding

class pokeinfoActivity : AppCompatActivity() {
    private lateinit var bindingInfo: PokeinfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pokeinfo)

        bindingInfo = PokeinfoBinding.inflate(layoutInflater)
        setContentView(bindingInfo.root)

        val pokemonName = intent.getStringExtra("POKEMON_NAME")
        val pokemonSprite = intent.getStringExtra("POKEMON_SPRITE")

        bindingInfo.tvNameinfo.text = pokemonName
        Glide.with(this)
            .load(pokemonSprite)
            .into(bindingInfo.ivPokeinfo)
    }
}