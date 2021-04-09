package com.task.data.remote.service

import com.task.data.dto.credential.login.LoginRequest
import com.task.data.dto.credential.login.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface CredentialService {

    @POST("apis/users/login.json")
    suspend fun fetchLogin(@Body loginRequest: LoginRequest): Response<LoginResponse>
}
