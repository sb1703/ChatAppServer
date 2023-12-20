package com.example.data.repository

import com.example.domain.model.ApiResponse
import com.example.domain.model.Conversation
import com.example.domain.model.Message
import com.example.domain.model.User
import com.example.domain.repository.ConversationDataSource
import com.example.util.Constants.NEXT_PAGE_KEY
import com.example.util.Constants.PREVIOUS_PAGE_KEY
import org.litote.kmongo.and
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class ConversationDataSourceImpl(
    database: CoroutineDatabase
): ConversationDataSource {

    private val conversations = database.getCollection<Conversation>()

    override suspend fun fetchChats(user1: User, user2: User, page: Int, limit: Int): ApiResponse {
        val conversation = conversations.findOne(and(Conversation::member.contains(user1),Conversation::member.contains(user2)))
        return if(conversation != null) {
            ApiResponse(
                success = true,
                message = "ok",
                prevPage = calculatePage(
                    message = conversation.messages,
                    page = page,
                    limit = limit
                )[PREVIOUS_PAGE_KEY],
                nextPage = calculatePage(
                    message = conversation.messages,
                    page = page,
                    limit = limit
                )[NEXT_PAGE_KEY],
                listMessages = provideMessages(
                    message = conversation.messages,
                    page = page,
                    limit = limit
                )
            )
        } else {
            conversations.insertOne(
                Conversation(
                    member = listOf(user1,user2),
                    messages = emptyList()
                )
            )
            ApiResponse(
                success = true,
                message = "ok",
                prevPage = null,
                nextPage = null,
                listMessages = emptyList()
            )
        }
    }

    override suspend fun fetchLastChat(user1: User, user2: User): ApiResponse {
        val conversation = conversations.findOne(and(Conversation::member.contains(user1),Conversation::member.contains(user2)))
        return if(conversation != null) {
            ApiResponse(
                success = true,
                chat = conversation.messages.last()
            )
        } else {
            ApiResponse(
                success = false,
                message = "No Last Message Found"
            )
        }
    }

    override suspend fun addChats(user1: User, user2: User, msg: Message): Boolean {
        val conversation = conversations.findOne(and(Conversation::member.contains(user1), Conversation::member.contains(user2)))
        val list = conversation?.messages?.plus(msg)
        return if (conversation != null) {
            conversations.updateOne(
                filter = Conversation::conversationId eq conversation.conversationId,
                update = setValue(
                    property = Conversation::messages,
                    value = list
                )
            ).wasAcknowledged()
        } else {
            conversations.insertOne(
                Conversation(
                    member = listOf(user1,user2),
                    messages = listOf(msg)
                )
            ).wasAcknowledged()
        }
    }

    private fun calculatePage(
        message: List<Message>,
        page: Int,
        limit: Int
    ): Map<String,Int?> {

        val allMessages = message.windowed(
            size = limit,
            step = limit,
            partialWindows = true
        )
        require(page <= allMessages.size)
        val prevPage = if(page==1) null else page-1
        val nextPage = if(page == allMessages.size) null else page+1

        return mapOf(
            PREVIOUS_PAGE_KEY to prevPage,
            NEXT_PAGE_KEY to nextPage
        )
    }

    private fun provideMessages(
        message: List<Message>,
        page: Int,
        limit: Int
    ): List<Message> {
        val allMessages = message.windowed(
            size = limit,
            step = limit,
            partialWindows = true
        )
        require(page>0 && page<=allMessages.size)
        return allMessages[page-1]
    }

}