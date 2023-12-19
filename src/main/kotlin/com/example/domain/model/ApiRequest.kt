package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiRequest(
    val tokenId: String? = null,
    val user: User? = null,
    val message: Message? = null,
    val userId: String? = null,
    val name: String? = null
)