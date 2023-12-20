package com.example.plugins

import com.example.domain.repository.ConversationDataSource
import com.example.domain.repository.UserDataSource
import com.example.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject

fun Application.configureRouting() {
    routing {
        val userDataSource: UserDataSource by inject(UserDataSource::class.java)
        val conversationDataSource: ConversationDataSource by inject(ConversationDataSource::class.java)
        rootRoute()
        tokenVerificationRoute(application,userDataSource)
        getUserInfoRoute(application,userDataSource)
        updateUserRoute(application,userDataSource)
        deleteUserRoute(application,userDataSource)
        addChats(application,userDataSource,conversationDataSource)
        addUsers(application,userDataSource)
        fetchChats(application,userDataSource,conversationDataSource)
        fetchLastChat(application,userDataSource,conversationDataSource)
        fetchUsers(application,userDataSource)
        searchUsers(application,userDataSource)
        signOutRoute()
        authorizedRoute()
        unauthorizedRoute()
    }
}
