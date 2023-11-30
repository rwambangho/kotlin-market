package com.example.carrotmarket

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carrotmarket.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignupBinding.inflate(layoutInflater) }
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        binding.btnSignIn.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            val name = binding.edtName.text.toString().trim()
            val birthday = binding.edtBirthday.text.toString().trim()

            createUser(email, password, birthday, name)
        }
    }

    private fun createUser (email: String, password: String, birthday: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "회원가입에 성공했습니다", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    updateUI(user)

                    val enrollinf = hashMapOf(
                        "email" to email,
                        "password" to password,
                        "name" to name,
                        "birthday" to birthday,
                    )
                    db.collection("Users").add(enrollinf).
                    addOnSuccessListener {
                    }
                } else {
                    Log.w("kms", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "회원가입에 실패했습니다", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "회원가입에 실패했습니다", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        user?.let {
            binding.txtResult.text = "Email: ${user.email}\nUid: ${user.uid}"
        }
    }
}