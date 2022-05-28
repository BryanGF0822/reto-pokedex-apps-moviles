package com.example.thepokemongo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thepokemongo.databinding.ActivityPokedexBinding
import com.example.thepokemongo.model.Pokemon
import com.example.thepokemongo.model.User
import com.example.thepokemongo.util.Constants
import com.example.thepokemongo.util.HTTPSWebUtilDomi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PokedexActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPokedexBinding
    private lateinit var listaDePokesRecycle : RecyclerView
    private lateinit var layoutManejador: GridLayoutManager
    //private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adaptador : PokemonsAdapter

    private lateinit var usuario : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokedexBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuario = intent.extras?.get("user") as User

        binding.bienvenidaPokeTextView.text = "Hola ${usuario.username}! Vamos por un pokemon!"
        //elementos del recycler
        listaDePokesRecycle = binding.listaDePokeRecycleView
        layoutManejador = GridLayoutManager(this, 2)
        listaDePokesRecycle.layoutManager =  layoutManejador
        listaDePokesRecycle.setHasFixedSize(true)
        adaptador = PokemonsAdapter()
        listaDePokesRecycle.adapter = adaptador

        adaptador.setOnPokemonItemClickListener(object : PokemonsAdapter.OnPokemonItemClickListenerInterface{
            override fun onPokemonItemClick(position: Int){
                val intent = Intent(this@PokedexActivity, PokemonCard::class.java).apply{
                    putExtra("pokemon",adaptador.getpokemon(position))
                }
                startActivity(intent)
            }
        })


        binding.atraparPokeButton.setOnClickListener{
            var nombreDelPoke = binding.nombrePokeAtraparEditText.text.toString()
            if(nombreDelPoke!=""){
                nombreDelPoke = nombreDelPoke.trim()
                nombreDelPoke = nombreDelPoke.lowercase()


                lifecycleScope.launch(Dispatchers.IO){

                    try{
                        nombreDelPoke = nombreDelPoke.trim()
                        val requestGET = HTTPSWebUtilDomi().GETRequest("${Constants.BASE_URL}api/v2/pokemon/${nombreDelPoke}")
                        val jsonObject : JsonObject = Gson().fromJson(requestGET, JsonObject::class.java)

                        nombreDelPoke = nombreDelPoke.replaceFirstChar { it.uppercaseChar() }

                        val stats = jsonObject["stats"].toString()
                        val types = jsonObject["types"].toString()
                        val imgs = jsonObject["sprites"].toString()

                        var pokeCapturado = crearNewPokeAmigo(nombreDelPoke, imgs, types, stats)


                        Firebase.firestore.collection("pokemon").add(pokeCapturado)

                        withContext(Dispatchers.Main){
                            Toast.makeText(this@PokedexActivity, " Wow!! Has logrado capturar a ${nombreDelPoke}", Toast.LENGTH_LONG).show()
                            agregarPokemonesAlRecycleView()
                        }
                    }catch(e : Exception){

                        withContext(Dispatchers.Main){
                            Toast.makeText(this@PokedexActivity, "Este pokemon no exite, intenta con otro nombre.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }else{
                Toast.makeText(this, "Este campo no deberia estar vacio", Toast.LENGTH_LONG).show()
            }
        }

        binding.buscarPokeButton.setOnClickListener {
            var pokebuscado :String = binding.buscaElPokeEditText.text.toString()
            if(pokebuscado!=""){
                pokebuscado= pokebuscado.trim()
                pokebuscado= pokebuscado.replaceFirstChar { it.uppercaseChar() }

                Firebase.firestore.collection("pokemon")
                    .whereEqualTo("username", usuario.username)
                    .whereEqualTo("name", pokebuscado)
                    .get().addOnCompleteListener { task->
                        if(task.result.size() !=0){
                            adaptador.clearPokemonList()
                            for(doc in task.result!!){
                                var pokeencontrado = doc.toObject(Pokemon::class.java)
                                adaptador.addPokemon(pokeencontrado)
                                adaptador.notifyDataSetChanged()
                            }
                        }else{
                            Toast.makeText(this@PokedexActivity, "el pokemon que intentas buscar no está en tu bolsa!", Toast.LENGTH_LONG).show()
                        }

                    }
            }else{
                Toast.makeText(this, "Debes escribir el nombre de uno de tus pokemones.", Toast.LENGTH_LONG).show()
                agregarPokemonesAlRecycleView()
            }
        }

    }



    fun crearNewPokeAmigo(nombre : String, img :String, tipos: String, estadisticas:String) : Pokemon{

        var estadisticasArray = estadisticas.split("},")

        var i =0
        val stats : IntArray = intArrayOf(0,0,0,0,0,0)
        while(i<estadisticasArray.size){
            var baseStat : String = estadisticasArray[i]
            var inicio :Int = baseStat.indexOf(":")
            var final : Int = baseStat.indexOf(",")


            stats[i] = baseStat.subSequence(inicio+1, final).toString().toInt()
            i++
        }


        var tiposArray = tipos.split("},")


        var j = 0
        var nombreDelTipoDePoke :String =""
        while(j<tiposArray.size){
            var tipoDePoke :String= tiposArray[j]
            var inicio :Int = tipoDePoke.indexOf("name")
            var final :Int= tipoDePoke.indexOf("\",")

            nombreDelTipoDePoke = nombreDelTipoDePoke+tipoDePoke.subSequence(inicio+7, final).toString().replaceFirstChar { it.uppercaseChar() }
            nombreDelTipoDePoke = nombreDelTipoDePoke+"-"

            j++
        }
        nombreDelTipoDePoke = nombreDelTipoDePoke.subSequence(0, nombreDelTipoDePoke.length-1).toString()
        var inicioImg = img.indexOf("front_default")
        var finalImg = img.indexOf("front_female")


        var imgURL = img.subSequence(inicioImg, finalImg).toString()
        imgURL = imgURL.replace("front_default","")
        imgURL = imgURL.replace("\":\"","")
        imgURL = imgURL.replace("\",\"","")

        val pokeACrear : Pokemon = Pokemon(
            imgURL,
            nombre,
            nombreDelTipoDePoke,
            stats[0].toDouble(),
            stats[1].toDouble(),
            stats[2].toDouble(),
            stats[5].toDouble(),
            usuario.username,
            System.currentTimeMillis()
        )
        return pokeACrear
    }


    fun agregarPokemonesAlRecycleView(){
        adaptador.clearPokemonList()
        Firebase.firestore.collection("pokemon")
            .whereEqualTo("username", usuario.username)
            .orderBy("timeAdded", Query.Direction.ASCENDING)
            .get().addOnCompleteListener{ task->
                for(doc in task.result!!){
                    var pok = doc.toObject(Pokemon::class.java)
                    adaptador.addPokemon(pok)
                    adaptador.notifyDataSetChanged()
            }
            }
    }

    override fun onResume() {
        super.onResume()
        agregarPokemonesAlRecycleView()
    }
}