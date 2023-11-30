package com.example.carrotmarket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.carrotmarket.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditArticleActivity : AppCompatActivity() {
    private lateinit var editTitleEditText: EditText
    private lateinit var editPriceEditText: EditText
    private lateinit var editDescriptionEditText: EditText
    private lateinit var editSubmitButton: Button
    private lateinit var editProgressBar: ProgressBar
    private lateinit var editSwitch: Switch


    private lateinit var chatKey: String
    private lateinit var title: String
    private lateinit var price: String
    private lateinit var uid: String
    private lateinit var imageUrl: String
    private lateinit var status: String

    private val articleDB: DatabaseReference by lazy {
        com.google.firebase.ktx.Firebase.database.reference.child("article")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("switchState", editSwitch.isChecked)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val switchState = savedInstanceState.getBoolean("switchState", false)
        editSwitch.isChecked = switchState
    }
    private var db: FirebaseFirestore = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_article)
        db = Firebase.firestore
        editTitleEditText = findViewById(R.id.editTitleEditText)
        editPriceEditText = findViewById(R.id.editPriceEditText)
        editDescriptionEditText = findViewById(R.id.editDescriptionEditText)
        editSubmitButton = findViewById(R.id.editSubmitButton)
        editProgressBar = findViewById(R.id.editProgressBar)
        editSwitch = findViewById(R.id.switch1)

        chatKey = intent.getStringExtra("chatKey") ?: ""
        title = intent.getStringExtra("title") ?: ""
        price = intent.getStringExtra("price") ?: ""
        uid = intent.getStringExtra("uid") ?: ""
        imageUrl = intent.getStringExtra("imageUrl") ?: ""
        status = intent.getStringExtra("sell") ?: ""

        val priceWithoutWon = price.replace("원", "")

        editTitleEditText.setText(title)
        editPriceEditText.setText(priceWithoutWon)
        if(status == "0"){
            editSwitch.isChecked = true
        }
        editSubmitButton.setOnClickListener {
            Log.d("EditArticleActivity", "editSubmitButton clicked")
            val updatedTitle = editTitleEditText.text.toString()
            val updatedPrice = editPriceEditText.text.toString()
            val updatedStatus = if (editSwitch.isChecked) "1" else "0"

            val resultIntent = Intent().apply {
                putExtra("title", updatedTitle)
                putExtra("price", updatedPrice)
                putExtra("status", updatedStatus)
            }

            setResult(Activity.RESULT_OK, resultIntent)
            updateArticle()
            finish()
        }
    }

    private fun updateArticle() {
        val updatedPrice = editPriceEditText.text.toString().trim()
        val updatedStatus = if (editSwitch.isChecked) "1" else "0"

        db.collection("article").document(uid).update("price", updatedPrice)
        db.collection("article").document(uid).update("sellvalue", updatedStatus)
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("price", updatedPrice)
        intent.putExtra("value", updatedStatus)
        intent.putExtra("uid",uid)
        startActivity(intent)

    }

}