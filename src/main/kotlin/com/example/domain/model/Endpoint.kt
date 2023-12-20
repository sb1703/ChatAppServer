package com.example.domain.model

sealed class Endpoint(val path: String) {
    data object Root: Endpoint(path = "/")
    data object TokenVerification: Endpoint(path = "/token_verification")
    data object GetUserInfo: Endpoint(path = "/get_user")
    data object UpdateUserInfo: Endpoint(path = "/update_user")
    data object FetchUsers: Endpoint(path = "/fetch_users")
    data object AddUsers: Endpoint(path = "/add_users")
    data object FetchChats: Endpoint(path = "/fetch_chats")
    data object FetchLastChat: Endpoint(path = "/fetch_last_chat")
    data object AddChats: Endpoint(path = "/add_chats")
    data object SearchUsers: Endpoint(path = "/search_users")
    data object DeleteUser: Endpoint(path = "/delete_user")
    data object SignOut: Endpoint(path = "/sign_out")
    data object Unauthorized: Endpoint(path = "/unauthorized")
    data object Authorized: Endpoint(path = "/authorized")
}