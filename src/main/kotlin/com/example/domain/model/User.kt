package com.example.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    val id: String,
    @BsonId
    val userId: String? = ObjectId.get().toString(),
    val name: String,
    val emailAddress: String,
    val profilePhoto: String,
    val list: List<User> = emptyList(),
    val online: Boolean = false,
    val lastLogin: Instant? = null
)
