package com.example.carrotmarket

data class ChatMessage
    (val buyerid: String,
     val content: String,
     val timestamp: Long,
     val sellerid: String,
     val articleid: String)
