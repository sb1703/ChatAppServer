package com.example.domain.model

data class ChatSession(
    val userId: String,
    val receiver: String,
    val sessionId: String
)
