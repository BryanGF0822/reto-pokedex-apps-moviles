package com.example.thepokemongo.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thepokemongo.R



class PokemonView(itemView: View, listener : PokemonsAdapter.OnPokemonItemClickListenerInterface) : RecyclerView.ViewHolder(itemView) {


    var pokemonNameRow : TextView = itemView.findViewById(R.id.nombreDelPokeRow)
    var pokemonImageRow : ImageView = itemView.findViewById(R.id.imagenDelPokeRow)

    init{
        itemView.setOnClickListener{
            listener.onPokemonItemClick(adapterPosition)
        }
    }
}