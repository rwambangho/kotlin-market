package com.example.carrotmarket

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var chatReference: DatabaseReference
    private lateinit var sellerUid: String // 이전 활동에서 판매자 UID를 가져오세요.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        chatReference = database.reference.child("chats")

        val currentUserUid = auth.currentUser?.uid
        val chatId = if (currentUserUid!!.compareTo(sellerUid) > 0) {
            "$currentUserUid-$sellerUid"
        } else {
            "$sellerUid-$currentUserUid"
        }

        val adapter = ChatAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)
        recyclerView.adapter = adapter

        val chatQuery = chatReference.child(chatId).orderByChild("timestamp")

//        chatQuery.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                val message = snapshot.getValue(ChatMessage::class.java)
//                adapter.addMessage(message)
//            }
//
//            // onChildChanged, onChildRemoved, onChildMoved, onCancelled 등 필요한 다른 메서드를 구현하세요.
//        })

        val sendButton = findViewById<Button>(R.id.sendButton)
        val messageEditText = findViewById<EditText>(R.id.messageEditText)

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(currentUserUid, messageText, chatId)
                messageEditText.text.clear()
            }
        }
    }

    private fun sendMessage(senderUid: String, messageText: String, chatId: String) {
        val timestamp = System.currentTimeMillis()
        val message = ChatMessage(senderUid, messageText, timestamp)

        chatReference.child(chatId).push().setValue(message)
    }
}