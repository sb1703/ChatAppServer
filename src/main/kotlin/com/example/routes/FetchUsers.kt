package com.example.routes

import com.example.domain.model.ApiResponse
import com.example.domain.model.Endpoint
import com.example.domain.model.UserSession
import com.example.domain.repository.ConversationDataSource
import com.example.domain.repository.UserDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException

fun Route.fetchUsers(
    app: Application,
    userDataSource: UserDataSource
) {
    authenticate("auth-session") {
        get(Endpoint.FetchUsers.path) {
            try {
                val page = call.request.queryParameters["page"]?.toInt() ?: 1
                val limit = call.request.queryParameters["limit"]?.toInt() ?: 4
                val userSession = call.principal<UserSession>()
                if (userSession == null) {
                    app.log.info("INVALID SESSION")
                    call.respondRedirect(Endpoint.Unauthorized.path)
                } else {
                    try {
                        val user = userDataSource.getUserInfoByMail(mail = userSession.mail)
                        if (user != null && user.userId != null) {
                            call.respond(
                                message = userDataSource.fetchUsers(userId = user.userId, page = page, limit = limit),
                                status = HttpStatusCode.OK
                            )
                        } else {
                            app.log.info("INVALID USER")
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
                    message = ApiResponse(success = false, message = "Users not Found"),
                    status = HttpStatusCode.NotFound
                )
            }
        }
    }
}