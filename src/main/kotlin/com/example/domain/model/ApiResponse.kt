package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val success: Boolean,
    val user: User? = null,
    val chat: Message? = null,
    var message: String? = null,
    val prevPage: Int? = null,
    val nextPage: Int? = null,
    val listMessages: List<Message> = emptyList(),
    val listUsers: List<User> = emptyList()
)
