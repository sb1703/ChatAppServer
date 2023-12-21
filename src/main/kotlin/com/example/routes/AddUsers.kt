package com.example.routes

import com.example.domain.model.ApiRequest
import com.example.domain.model.ApiResponse
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

fun Route.addUsers(
    app: Application,
    userDataSource: UserDataSource
) {
    authenticate("auth-session") {
        post(Endpoint.AddUsers.path) {
//            val userId = call.request.queryParameters["userId"]
            val userSession = call.principal<UserSession>()
            val userId = call.receive<ApiRequest>().userId
            if (userSession == null) {
                app.log.info("INVALID SESSION")
                call.respondRedirect(Endpoint.Unauthorized.path)
            } else {
                try {
                    val user = userDataSource.getUserInfoByMail(mail = userSession.mail)
                    if (user != null) {
                        val userId1 = user.userId
                        if(userId1 != null && userId != null) {
                            userDataSource.addUsers(userId1,userId)
                            call.respond(
                                message = ApiResponse(
                                    success = true,
                                    message = "Added User Successfully"
                                ),
                                status = HttpStatusCode.OK
                            )
                        } else {
                            app.log.info("INVALID USER_ID")
                            call.respondRedirect(Endpoint.Unauthorized.path)
                        }
                    } else {
                        app.log.info("INVALID USERS")
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