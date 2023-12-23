package com.example.routes

import com.example.domain.model.ApiRequest
import com.example.domain.model.UserAlreadyExistsException
import com.example.domain.model.UserSession
import com.example.domain.repository.UserDataSource
import com.example.room.RoomController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.chatSocket(
    roomController: RoomController,
    userDataSource: UserDataSource
) {
    webSocket("/chat-socket") {
//        val session = call.sessions.get<UserSession>()
        val session = call.principal<UserSession>()
        val message = call.receive<ApiRequest>().message
        if(session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
            return@webSocket
        }
        try {
            val user = userDataSource.getUserInfoByMail(session.mail)
            if (user != null) {
                user.userId?.let {
                    roomController.onJoin(
                        userId = it,
                        sessionId = session.id,
                        socket = this
                    )
                    incoming.consumeEach { frame ->
                        if(frame is Frame.Text) {
                            if (message != null) {
                                roomController.sendMessage(
                                    senderUserId = user.userId,
                                    message = frame.readText(),
                                    receiverUserIds = message.receiver
                                    //  IMP - RECEIVER_USER_ID
                                )
                            }
                        }
                    }
                }
            }
        } catch(e: UserAlreadyExistsException) {
            call.respond(HttpStatusCode.Conflict)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            val user = userDataSource.getUserInfoByMail(session.mail)
            if (user != null) {
                user.userId?.let { roomController.tryDisconnect(it) }
            }
        }
    }
}

//fun Route.fetchMessages(roomController: RoomController) {
//    get("/messages") {
//        call.respond(
//            HttpStatusCode.OK,
//            roomController.fetchMessages()
//        )
//    }
//}