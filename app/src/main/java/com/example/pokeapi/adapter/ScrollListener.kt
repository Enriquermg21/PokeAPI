package com.example.pokeapi.adapter

import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ScrollListener(private val context: Context, recyclerView: RecyclerView) :
    RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
        val visibleItemCount = layoutManager!!.childCount
        val totalItemCount = layoutManager.getItemCount()
        val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
            showToast("¡the end!")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

