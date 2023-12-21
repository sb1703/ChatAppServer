package com.example.routes

import com.example.domain.model.*
import com.example.domain.repository.ConversationDataSource
import com.example.domain.repository.UserDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException

fun Route.fetchChats(
    app: Application,
    userDataSource: UserDataSource,
    conversationDataSource: ConversationDataSource
) {
    authenticate("auth-session") {
        post(Endpoint.FetchChats.path) {
            try {
                val page = call.request.queryParameters["page"]?.toInt() ?: 1
                val limit = call.request.queryParameters["limit"]?.toInt() ?: 30

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
                                var apiResponse = conversationDataSource.fetchChats(user,user2,page,limit)
                                apiResponse.message = apiResponse.chat?.author?.let { it1 -> userDataSource.getUserInfoById(userId = it1)!!.name }
                                call.respond(
                                    message = apiResponse,
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
            } catch (e: NumberFormatException){
                call.respond(
                    message = ApiResponse(success = false, message = "Only Numbers Allowed"),
                    status = HttpStatusCode.BadRequest
                )
            } catch (e: IllegalArgumentException){
                call.respond(
                    message = ApiResponse(success = false, message = "Chats not Found"),
                    status = HttpStatusCode.NotFound
                )
            }
        }
    }
}