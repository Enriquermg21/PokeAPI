package com.example.pokeapi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PokeAdapter(private val pokeList: List<String>) : RecyclerView.Adapter<PokeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokeViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_poke, parent, false)
        return PokeViewHolder(view)
    }

    override fun getItemCount(): Int = pokeList.size

    override fun onBindViewHolder(holder: PokeViewHolder, position: Int) {
        val item: String = pokeList[position]
        holder.bind(item)
    }
}