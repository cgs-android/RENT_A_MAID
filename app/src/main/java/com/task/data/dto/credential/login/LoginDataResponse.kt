package com.task.data.dto.credential.login

data class LoginDataResponse(
    val id: Int = 0,
    val token: String = "",
    val first_name: String = "",
    val last_name: String = "",
    val employee_user_name: String = "",
    val email: String = "",
    val role_id: Int = 0,
    val is_active: Boolean = false
)