package com.example.domain.repository

import com.example.domain.model.ApiResponse
import com.example.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    suspend fun getUserInfoById(userId: String): User?
    suspend fun getUserInfoByMail(mail: String): User?
    suspend fun saveUserInfo(user: User): Boolean
    suspend fun deleteUser(userId: String): Boolean
    suspend fun updateUserInfo(
        userId: String,
        name: String
    ): Boolean
    suspend fun fetchUsers(userId: String, page: Int = 1,limit: Int = 4): ApiResponse
    suspend fun addUsers(userId: String, userId2: String): Boolean
    suspend fun searchUsersByName(name: String, page: Int = 1,limit: Int = 4): ApiResponse
}