package com.example.routes

import com.example.domain.model.ApiRequest
import com.example.domain.model.ApiResponse
import com.example.domain.model.Endpoint
import com.example.domain.model.UserSession
import com.example.domain.repository.UserDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getUserInfoByIdRoute(
    app: Application,
    userDataSource: UserDataSource
) {
    authenticate("auth-session") {
        post(Endpoint.GetUserInfoById.path) {
            val userSession = call.principal<UserSession>()
            val userId = call.receive<ApiRequest>().userId
            if (userSession == null) {
                app.log.info("INVALID SESSION")
                call.respondRedirect(Endpoint.Unauthorized.path)
            } else {
                try {
                    val user = userId?.let { it1 -> userDataSource.getUserInfoById(userId = it1) }
                    call.respond(
                        message = ApiResponse(
                            success = true,
                            user = user
                        ),
                        status = HttpStatusCode.OK
                    )
                } catch (e: Exception) {
                    app.log.info("GETTING USER INFO ERROR: ${e.message}")
                    call.respondRedirect(Endpoint.Unauthorized.path)
                }
            }
        }
    }
}