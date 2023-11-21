package com.example.carrotmarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.carrotmarket.databinding.ActivitySigninBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySigninBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (Firebase.auth.currentUser != null){
            Toast.makeText(this,"Current user is ${Firebase.auth.uid}",Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this,MainActivity::class.java)
            )
            finish()
        }

        println("Login activity activated.")
        binding.loginButton.setOnClickListener {
            doLogin(binding.idEditText.text.toString(),binding.passwordEditText.text.toString())
        }

        binding.createAccount.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
    }

    private fun doLogin(id: String, pass: String)
    {
        Firebase.auth.signInWithEmailAndPassword(id,pass)
            .addOnCompleteListener(this) {
                println("Login Task occur")

                if(it.isSuccessful){
                    Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(this,SignInActivity::class.java)
                    )
                    finish()
                }
                else{
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }

    }
}