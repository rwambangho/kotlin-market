package com.example.carrotmarket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.carrotmarket.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatFragment: Fragment() {
    lateinit var binding: FragmentChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().reference.child("chats")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)
        chatAdapter = ChatAdapter()
        binding.chatRecyclerView.adapter = chatAdapter

        val senderUid = "some_other_user_uid" // Replace with the actual UID of the chat partner


//        database.child(uid).child(senderUid).addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                val message = snapshot.getValue(ChatMessage::class.java)
//                message?.let { chatAdapter.addMessage(it) }
//                binding.chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
//            }
//
//
//        })

        // Send a message example
        binding.sendButton.setOnClickListener {
            val messageContent = binding.messageEditText.text.toString().trim()

            if (messageContent.isNotEmpty()) {
                val timestamp = System.currentTimeMillis()
                val chatMessage = ChatMessage(uid, messageContent, timestamp)

                // Push the message to the database
                database.child(uid).child(senderUid).push().setValue(chatMessage)

                // Clear the input field
                binding.messageEditText.text.clear()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }
}