package com.example.domain.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class Message(
    @BsonId
    val messageId: String? = ObjectId.get().toString(),
    val author: String? = null,
    val receiver: List<String> = emptyList(),
    val messageText: String? = null,
    val time: String = getCurrentTimeIn12HourFormat()
)

fun getCurrentTimeIn12HourFormat(): String {
    val currentTime = Calendar.getInstance().time
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(currentTime)
}
