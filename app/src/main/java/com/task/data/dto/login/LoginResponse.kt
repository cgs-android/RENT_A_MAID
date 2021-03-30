package com.task.data.dto.login

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val data: LoginDataResponse
)


