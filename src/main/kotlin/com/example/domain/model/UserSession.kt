package com.example.domain.model

import io.ktor.server.auth.*

data class UserSession(
    val id: String,
    val name: String,
    val mail: String
): Principal
// Need to implement Principal interface to be able to authenticate & authorize our users using UserSession
