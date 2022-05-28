package com.example.thepokemongo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thepokemongo.R
import com.example.thepokemongo.model.Pokemon
import com.example.thepokemongo.util.LoadImageTask


class PokemonsAdapter : RecyclerView.Adapter<PokemonView>() {

    private val listaDeLosPokes = ArrayList<Pokemon>()
    private lateinit var listenerPokeItem : OnPokemonItemClickListenerInterface
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonView {



        var inflater = LayoutInflater.from(parent.context)
        val fila = inflater.inflate(R.layout.pokemonrow, parent, false)
        val pokeView = PokemonView(fila, listenerPokeItem)
        return pokeView
    }

    override fun onBindViewHolder(skeleton: PokemonView, position: Int) {
        val poke = listaDeLosPokes[position]
        skeleton.pokemonNameRow.text = poke.name

        var imagen = LoadImageTask(skeleton.pokemonImageRow)
        imagen.execute(poke.image)

    }

    override fun getItemCount(): Int {
        return listaDeLosPokes.size
    }

    fun clearPokemonList(){
        listaDeLosPokes.clear()
    }

    fun getpokemon(position: Int) : Pokemon{
        return listaDeLosPokes.get(position)
    }

    fun addPokemon(newPokemon : Pokemon){
        listaDeLosPokes.add(newPokemon)

    }

    fun setOnPokemonItemClickListener(lstner : OnPokemonItemClickListenerInterface){
        listenerPokeItem = lstner
    }

    interface OnPokemonItemClickListenerInterface{
        fun onPokemonItemClick(position: Int)
    }
}