package com.example.thepokemongo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.thepokemongo.databinding.ActivityMainBinding
import com.example.thepokemongo.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)

        binding.loginButton.setOnClickListener(::login)
    }

    private fun login(view: View){

        var nombreUsuario = binding.nameEditText.text.toString().trim()

        if(nombreUsuario!=""){

            val usuario = User(UUID.randomUUID().toString(), nombreUsuario)

            val query = Firebase.firestore.collection("users").whereEqualTo("username",nombreUsuario)

            query.get().addOnCompleteListener { task->
                if(task.result?.size()==0){
                    Firebase.firestore.collection("users").document(usuario.id).set(usuario)
                    val intent = Intent(this, PokedexActivity::class.java).apply {
                        putExtra("user", usuario)
                    }
                    startActivity(intent)

                }else{
                    lateinit var usuarioExistente : User
                    for(document in task.result!!){
                        usuarioExistente = document.toObject(User::class.java)
                        break
                    }
                    val intent = Intent(this, PokedexActivity::class.java).apply {
                        putExtra("user", usuarioExistente)
                    }
                    startActivity(intent)
                }
            }
        }else{
            Toast.makeText(this, "Ingresa un nombre de usuario!", Toast.LENGTH_LONG).show()
        }


    }
}