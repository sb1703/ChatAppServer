package com.example.domain.repository

import com.example.domain.model.ApiResponse
import com.example.domain.model.Message
import com.example.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ConversationDataSource {
    suspend fun fetchChats(user1: User,user2: User,page: Int = 1,limit: Int = 4): ApiResponse
    suspend fun addChats(user1: User, user2: User, msg: Message): Boolean
}