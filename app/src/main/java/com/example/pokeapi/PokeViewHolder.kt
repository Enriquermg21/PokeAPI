package com.example.pokeapi

import android.R
import android.view.View
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeapi.databinding.ItemPokeBinding

class PokeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemPokeBinding.bind(view)
    fun bind(item: String) {
        val listView = binding.lPoke
        val adapter = ArrayAdapter(itemView.context, R.layout.simple_list_item_1, listOf(item))
        listView.adapter = adapter
    }
}