package com.example.carrotmarket

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.carrotmarket.databinding.DialogConfirmBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ConfirmDialog(
    confirmDialogInterface: ConfirmDialogInterface,
    uid: String, sellerid: String
) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogConfirmBinding? = null
    private lateinit var database: DatabaseReference
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore

    private lateinit var auth: FirebaseAuth
    private lateinit var buyerid: String

    private var confirmDialogInterface: ConfirmDialogInterface? = null

    private var uid: String? = null //게시물uid
    private var sellerid: String?=null //판매자 uid

    init {
        this.uid = uid
        this.sellerid = sellerid
        this.confirmDialogInterface = confirmDialogInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogConfirmBinding.inflate(inflater, container, false)
        val view = binding.root
        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()
        buyerid = auth.currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().reference.child("chats")
        //전송버튼
        binding.yesButton.setOnClickListener {

            val message = binding.sendtext.text
            val timestamp = System.currentTimeMillis()
            val chatMessage = ChatMessage(buyerid, message.toString(), timestamp)

            // Push the message to the database

            //판매자가 문서의 id가되고 그 아래 구매자id, 내용이 필드값으로 들어간다.
            db.collection("chats").document(sellerid.toString())
                .set(chatMessage).addOnSuccessListener {
                }
            dismiss()
        }
        //취소버튼
        binding.noButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface ConfirmDialogInterface {
    fun onYesButtonClick(id: Int)
}