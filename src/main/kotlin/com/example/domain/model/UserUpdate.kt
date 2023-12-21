package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserUpdate(
    val name: String
)
