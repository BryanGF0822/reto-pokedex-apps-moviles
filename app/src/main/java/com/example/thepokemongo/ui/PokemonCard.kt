package com.example.thepokemongo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.thepokemongo.databinding.ActivityPokemonCardBinding
import com.example.thepokemongo.model.Pokemon
import com.example.thepokemongo.util.LoadImageTask
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PokemonCard : AppCompatActivity() {

    private lateinit var binding: ActivityPokemonCardBinding
    private lateinit var elpoke : Pokemon
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPokemonCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        elpoke = (intent.extras?.get("pokemon") as Pokemon?)!!
        var imagen = LoadImageTask(binding.imgDelPokeImageView)
        imagen.execute(elpoke.image)

        binding.nombreDelpokeTextView.text = " ${elpoke.name} "
        binding.propietario.text = " ${binding.propietario.text}  ${elpoke.username} "
        binding.tipo.text = " ${binding.tipo.text}  ${elpoke.type} "
        binding.salud.text = " ${binding.salud.text}  ${elpoke.health} "
        binding.ataque.text = " ${binding.ataque.text}  ${elpoke.attack} "
        binding.defensa.text = " ${binding.defensa.text}  ${elpoke.defense} "
        binding.velocidad.text = " ${binding.velocidad.text}  ${elpoke.speed} "


        binding.liberarPokeButton.setOnClickListener {
            Firebase.firestore.collection("pokemon")
                .whereEqualTo("username", elpoke.username)
                .whereEqualTo("name", elpoke.name)
                .whereEqualTo("timeAdded", elpoke.timeAdded)
                .get().addOnCompleteListener { task->
                    for(document in task.result!!){
                        document.reference.delete()
                    }
                }
            this.finish()
        }
    }
}