package com.task.data.dto.credential.login

data class LoginResponse(
    val status: Boolean=false,
    val message: String="",
    val data: LoginDataResponse
)


