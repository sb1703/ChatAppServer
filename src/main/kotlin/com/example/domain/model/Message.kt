package com.example.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Message(
    @BsonId
    val messageId: String? = ObjectId.get().toString(),
    val author: String? = null,
    var authorName: String? = null,
    val receiver: List<String> = emptyList(),
    val messageText: String? = null,
    val dateTime: Instant = Clock.System.now()
)
