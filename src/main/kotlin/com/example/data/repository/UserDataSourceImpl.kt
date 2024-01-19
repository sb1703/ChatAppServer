package com.example.data.repository

import com.example.domain.model.ApiResponse
import com.example.domain.model.User
import com.example.domain.repository.UserDataSource
import com.example.util.Constants
import com.example.util.Constants.NEXT_PAGE_KEY
import com.example.util.Constants.PREVIOUS_PAGE_KEY
import io.ktor.server.application.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.BsonDocument
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.eq
import org.litote.kmongo.regex
import org.litote.kmongo.setValue
import org.slf4j.LoggerFactory

class UserDataSourceImpl(
    database: CoroutineDatabase
): UserDataSource {

    private val users = database.getCollection<User>()

    override suspend fun getUserInfoById(userId: String): User? {
        return users.findOne(filter = User::userId eq userId)
    }

    override suspend fun getUserInfoByMail(mail: String): User? {
        return users.findOne(filter = User::emailAddress eq mail)
    }

    override suspend fun getUserInfoByUserName(name: String): User? {
        return users.findOne(filter = User::name eq name)
    }

    override suspend fun saveUserInfo(user: User): Boolean {
        val logger: org.slf4j.Logger? = LoggerFactory.getLogger("MyLogger")
        if (logger != null) {
            logger.info("USERNAME-SUI: ${user.name}")
        }
//        val existingUser = users.findOne(filter = User::userId eq user.userId)
        val existingUser = users.findOne(filter = User::emailAddress eq user.emailAddress)
        if (logger != null) {
            if (existingUser != null) {
                logger.info("USERNAME-SUI-EXISTING-USER: ${existingUser.name}")
            }
        }
        return if (existingUser == null) {
            val userDocument = BsonDocument.parse(Json.encodeToString(user))
            val deserializedUser = Json.decodeFromString<User>(userDocument.toJson())
            users.insertOne(deserializedUser).wasAcknowledged()
//            users.insertOne(document = user).wasAcknowledged()
        } else {
            true
        }
    }

    override suspend fun deleteUser(userId: String): Boolean {
        return users.deleteOne(filter = User::userId eq userId).wasAcknowledged()
    }

    override suspend fun updateUserName(
        userId: String,
        name: String
    ): Boolean {
        return users.updateOne(
            filter = User::userId eq userId,
            update = setValue(
                property = User::name,
                value = name
            )
        ).wasAcknowledged()
    }

    override suspend fun updateUserOnline(
        userId: String,
        online: Boolean
    ): Boolean {
        return users.updateOne(
            filter = User::userId eq userId,
            update = setValue(
                property = User::online,
                value = online
            )
        ).wasAcknowledged()
    }

    override suspend fun updateUserLastLogin(
        userId: String,
        lastLogin: String
    ): Boolean {
        return users.updateOne(
            filter = User::userId eq userId,
            update = setValue(
                property = User::lastLogin,
                value = lastLogin
            )
        ).wasAcknowledged()
    }

    override suspend fun fetchUsers(userId: String, page: Int, limit: Int): ApiResponse {
        val user = users.findOne(filter = User::userId eq userId)

        return if(user != null) {
            ApiResponse(
                success = true,
                message = "ok",
                listUsers = user.list
//                prevPage = calculatePage(
//                    user = user.list,
//                    page = page,
//                    limit = limit
//                )[PREVIOUS_PAGE_KEY],
//                nextPage = calculatePage(
//                    user = user.list,
//                    page = page,
//                    limit = limit
//                )[NEXT_PAGE_KEY],
//                listUsers = provideUsers(
//                    user = user.list,
//                    page = page,
//                    limit = limit
//                )
            )
        } else {
            ApiResponse(
                success = true,
                message = "ok",
                prevPage = null,
                nextPage = null,
                listUsers = emptyList()
            )
        }

    }

    override suspend fun addUsers(userId: String, userId2: String): Boolean {
        val existingUser = users.findOne(filter = User::userId eq userId)
        val existingUser2 = users.findOne(filter = User::userId eq userId2)
        return if (existingUser == null || existingUser2 == null) {
//            users.insertOne(document = users).wasAcknowledged()
//            error case
            false
        } else {
            val list1 = existingUser.list.plus(existingUser2)
            val list2 = existingUser2.list.plus(existingUser)
            users.updateOne(
                filter = User::userId eq userId,
                update = setValue(
                    property = User::list,
                    value = list1
                )
            ).wasAcknowledged()
                    &&
            users.updateOne(
                filter = User::userId eq userId2,
                update = setValue(
                    property = User::list,
                    value = list2
                )
            ).wasAcknowledged()
        }
    }

    override suspend fun searchUsersByName(name: String, page: Int, limit: Int): ApiResponse {
        val user = users.find(filter = User::name.regex(name,"i"))

        return if ( user.first() != null) {
            ApiResponse(
                success = true,
                message = "ok",
                prevPage = calculatePage(
                    user = user.toList(),
                    page = page,
                    limit = limit
                )[PREVIOUS_PAGE_KEY],
                nextPage = calculatePage(
                    user = user.toList(),
                    page = page,
                    limit = limit
                )[NEXT_PAGE_KEY],
                listUsers = provideUsers(
                    user = user.toList(),
                    page = page,
                    limit = limit
                )
            )
        } else {
            ApiResponse(
                success = true,
                message = "ok",
                prevPage = null,
                nextPage = null,
                listUsers = emptyList()
            )
        }
    }

    private fun calculatePage(
        user: List<User>,
        page: Int,
        limit: Int
    ): Map<String,Int?> {

        val allUsers = user.windowed(
            size = limit,
            step = limit,
            partialWindows = true
        )
        require(page <= allUsers.size)
        val prevPage = if(page==1) null else page-1
        val nextPage = if(page == allUsers.size) null else page+1

        return mapOf(
            Constants.PREVIOUS_PAGE_KEY to prevPage,
            Constants.NEXT_PAGE_KEY to nextPage
        )
    }

    private fun provideUsers(
        user: List<User>,
        page: Int,
        limit: Int
    ): List<User> {
        val allUsers = user.windowed(
            size = limit,
            step = limit,
            partialWindows = true
        )
        require(page>0 && page<=allUsers.size)
        return allUsers[page-1]
    }
}