package com.task.data.remote

import com.task.data.Resource
import com.task.data.dto.login.LoginRequest
import com.task.data.dto.login.LoginResponse
import com.task.data.dto.recipes.Recipes


internal interface RemoteDataSource {
    suspend fun requestRecipes(): Resource<Recipes>
    suspend fun requestLogin(loginRequest: LoginRequest): Resource<LoginResponse>
}
