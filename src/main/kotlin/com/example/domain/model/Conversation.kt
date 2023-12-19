package com.example.domain.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Conversation(
    @BsonId
    val conversationId: String? = ObjectId.get().toString(),
    val member: List<User> = emptyList(),
    val messages: List<Message> = emptyList()
)
