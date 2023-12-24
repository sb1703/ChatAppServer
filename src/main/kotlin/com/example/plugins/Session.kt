package com.example.plugins

import com.example.domain.model.ChatSession
import com.example.domain.model.UserSession
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.sessions.*
import io.ktor.util.*
import java.io.File

fun Application.configureSession() {
    install(Sessions) {

        val secretEncryptKey = hex("00112233445566778899aabbccddeeff")
        val secretAuthKey = hex("02030405060708090a0b0c")

        cookie<UserSession>(
            name = "USER_SESSION",
            storage = directorySessionStorage(File(".sessions"))
        ) {
//            cookie.maxAge = 30.minutes

//            ENC not needed in this case as we are storing cookie server side and only sending ID to android applications
            transform(SessionTransportTransformerEncrypt(secretEncryptKey,secretAuthKey))

//            can send cookie only through secure connection
//            cookie.secure = true
        }

        cookie<ChatSession>("CHAT_SESSION")
    }

    intercept(Plugins) {
        if(call.sessions.get<ChatSession>() == null){
            val userId = call.parameters["userId"] ?: ""
            val receiver = call.parameters["receiver"] ?: ""
            call.sessions.set(ChatSession(userId,receiver, generateNonce()))
        }
    }
}