package com.example.routes

import com.example.domain.model.ApiRequest
import com.example.domain.model.Endpoint
import com.example.domain.model.UserSession
import com.example.domain.repository.ConversationDataSource
import com.example.domain.repository.UserDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.fetchLastChat(
    app: Application,
    userDataSource: UserDataSource,
    conversationDataSource: ConversationDataSource
) {
    authenticate("auth-session") {
        get(Endpoint.FetchLastChat.path) {
            val userSession = call.principal<UserSession>()
            val userId = call.receive<ApiRequest>().userId
            if (userSession == null) {
                app.log.info("INVALID SESSION")
                call.respondRedirect(Endpoint.Unauthorized.path)
            } else {
                try {
                    if(userId != null) {
                        val user = userDataSource.getUserInfoByMail(mail = userSession.mail)
                        val user2 = userDataSource.getUserInfoById(userId = userId)
                        if (user != null && user2 != null) {
                            call.respond(
                                message = conversationDataSource.fetchLastChat(user,user2),
                                status = HttpStatusCode.OK
                            )
                        } else {
                            app.log.info("INVALID USER")
                            call.respondRedirect(Endpoint.Unauthorized.path)
                        }
                    } else {
                        app.log.info("INVALID USER_ID")
                        call.respondRedirect(Endpoint.Unauthorized.path)
                    }
                } catch (e: Exception) {
                    app.log.info("GETTING USER INFO ERROR: ${e.message}")
                    call.respondRedirect(Endpoint.Unauthorized.path)
                }
            }
        }
    }
}