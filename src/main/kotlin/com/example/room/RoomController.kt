package com.example.room

import com.example.domain.model.Message
import com.example.domain.model.User
import com.example.domain.model.UserAlreadyExistsException
import com.example.domain.repository.ConversationDataSource
import com.example.domain.repository.UserDataSource
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val conversationDataSource: ConversationDataSource,
//    private val messageDataSource: MessageDataSource,
    private val userDataSource: UserDataSource
) {

//    userId to User
    private val users = ConcurrentHashMap<String,User>()

    suspend fun onJoin(
        userId: String,
        sessionId: String,
        socket: WebSocketSession
    ) {
        if(users.containsKey(userId)){
            throw UserAlreadyExistsException()
        }

        val user = userDataSource.getUserInfoById(userId)

        if (user != null) {
            users[userId] = User(
                userId = user.id,
                name = user.name,
                id = sessionId,
                socket = socket,
                emailAddress = user.emailAddress,
                profilePhoto = user.profilePhoto,
                list = user.list,
                online = user.online,
                lastLogin = user.lastLogin
            )
        }
    }

    suspend fun sendMessage(
        senderUserId: String,
        message: String,
        receiverUserIds: List<String>
    ) {
        val user1 = userDataSource.getUserInfoById(senderUserId)
        val user2 = userDataSource.getUserInfoById(receiverUserIds[0])

        val messageEntity = Message(
            author = senderUserId,
            messageText = message,
            receiver = receiverUserIds
        )
        if (user1 != null && user2 != null) {
            conversationDataSource.addChats(
                msg = messageEntity,
                user1 = user1,
                user2 = user2
            )
        }
        users.forEach {
            if(it.key in receiverUserIds){
                val parsedMessage = Json.encodeToString(messageEntity)
                it.value.socket?.send(Frame.Text(parsedMessage))
            }
        }
//        users.values.forEach { user ->
//            val parsedMessage = Json.encodeToString(messageEntity)
//            user.socket?.send(Frame.Text(parsedMessage))
//        }
    }

//    suspend fun fetchMessages(): List<Message> {
//        return conversationDataSource.fetchChats().listMessages
//    }

    suspend fun tryDisconnect(
        userId: String
    ) {
        users[userId]?.socket?.close()
        if(users.containsKey(userId)){
            users.remove(userId)
        }
    }

}