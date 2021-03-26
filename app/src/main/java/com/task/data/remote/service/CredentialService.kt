package com.task.data.remote.service

import com.task.data.dto.login.LoginRequest
import com.task.data.dto.login.LoginResponse
import com.task.data.dto.recipes.RecipesItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface CredentialService {
    @GET("recipes.json")
    suspend fun fetchRecipes(): Response<List<RecipesItem>>

    @POST("apis/users/login.json")
    suspend fun fetchLogin(@Body loginRequest: LoginRequest): Response<LoginResponse>
}
