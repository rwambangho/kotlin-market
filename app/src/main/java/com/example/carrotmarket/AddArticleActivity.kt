package com.example.carrotmarket

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.carrotmarket.databinding.ActivityAddArticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AddArticleActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    private val binding by lazy { ActivityAddArticleBinding.inflate(layoutInflater) }
    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child("article")
    }
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private var selectedUri: Uri? = null

    private val storage: FirebaseStorage by lazy { Firebase.storage }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        db = Firebase.firestore

        initViews()
    }

    @SuppressLint("ShowToast")
    private fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }

        imageAddButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this@AddArticleActivity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startContentProvider()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1010
                    )
                }
            }
        }

        submitButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val price = priceEditText.text.toString()
            val sellerId = auth.currentUser?.uid.orEmpty()
            val content = contentEditText.text.toString()
            val uploadtime = System.currentTimeMillis()

            progressBar.isVisible = true

            if (selectedUri != null) {
                // 예외 처리
                val PhotoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(
                    PhotoUri,
                    successHandler = { uri ->
                        val enrollinf = hashMapOf(
                            "title" to title,
                            "price" to price,
                            "sellerId" to sellerId,
                            "uri" to uri,
                            "content" to content,
                            "uploadtime" to uploadtime.toString(),
                            "sellvalue" to 0.toString(),
                            "uid" to FirebaseAuth.getInstance().uid as String
                        )
                        uploadArticle(enrollinf, uri)
                    },
                    errorHandler = {
                    }
                )
            } else {
                val enrollinf = hashMapOf(
                    "title" to title,
                    "price" to price,
                    "sellerId" to sellerId,
                    "uid" to FirebaseAuth.getInstance().uid as String,
                    "uri" to "",
                    "content" to content,
                    "uploadtime" to uploadtime.toString(),
                    "sellvalue" to 0.toString(),
                )
                uploadArticle(enrollinf, "")
            }

//            db.collection("article").add(enrollinf).
//                    addOnSuccessListener {
//                    }
//                .addOnFailureListener {
//                }
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1010 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startContentProvider()
            } else {
                Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2020)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            2020 -> {
                val uri = data?.data
                if (uri != null) {
                    binding.photoImageView.setImageURI(uri)
                    selectedUri = uri
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 권한이 필요합니다.\n권한을 허용하시겠습니까?")
            .setPositiveButton("허용") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()
    }
    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"

        val imageref = Firebase.storage.reference.child("$fileName")

        imageref.putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storage.reference.child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    }

    private fun uploadArticle(enrollinf: HashMap<String, String>, imageUrl: String) {
                    db.collection("article").add(enrollinf).
                    addOnSuccessListener {
                        Log.e("kms",it.id)
                        db.collection("article").document(it.id).update("uid", it.id)
                    }
                .addOnFailureListener {
                }
//        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), "$price 원", imageUrl)
//        articleDB.push().setValue(model)
//        binding.progressBar.isVisible = false
        finish()
    }
}