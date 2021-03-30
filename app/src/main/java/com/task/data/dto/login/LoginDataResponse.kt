package com.task.data.dto.login

data class LoginDataResponse(
    val id: Int,
    val token: String,
    val first_name: String,
    val last_name: String,
    val employee_user_name: String,
    val email: String,
    val role_id: Int,
    val is_active: Boolean
)