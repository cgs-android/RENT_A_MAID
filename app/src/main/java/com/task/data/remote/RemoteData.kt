package com.task.data.remote

import com.task.data.Resource
import com.task.data.dto.login.LoginRequest
import com.task.data.dto.login.LoginResponse
import com.task.data.dto.recipes.Recipes
import com.task.data.dto.recipes.RecipesItem
import com.task.data.error.NETWORK_ERROR
import com.task.data.error.NO_INTERNET_CONNECTION
import com.task.data.remote.service.RecipesService
import com.task.utils.EnumIntUtils
import com.task.utils.NetworkConnectivity
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject


class RemoteData @Inject
constructor(
    private val serviceGenerator: ServiceGenerator,
    private val networkConnectivity: NetworkConnectivity
) : RemoteDataSource {

    override suspend fun requestRecipes(): Resource<Recipes> {
        val recipesService = serviceGenerator.createService(RecipesService::class.java)
        return when (val response = processCall(recipesService::fetchRecipes)) {
            is List<*> -> {
                Resource.Success(data = Recipes(response as ArrayList<RecipesItem>))
            }
            else -> {
                Resource.DataError(errorCode = response as Int)
            }
        }
    }

    override suspend fun requestLogin(loginRequest: LoginRequest): Resource<LoginResponse> {
        val loginService = serviceGenerator.createService(RecipesService::class.java)
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorCode = NO_INTERNET_CONNECTION)
        }
        val loginData = loginService.fetchLogin(loginRequest)
        val responseCode = loginData.code()
        return when (responseCode) {
            EnumIntUtils.SUCCESS_CODE.code -> {
                if (loginData.isSuccessful) {
                    val responseBody = loginData.body()
                    if (responseBody!!.status) {
                        Resource.Success(data = responseBody)
                    } else {
                        Resource.Failure(failureData = responseBody)
                    }
                } else {
                    Resource.DataError(errorCode = responseCode)
                }
            }
            else -> {
                Resource.DataError(errorCode = responseCode)
            }
        }


    }

    private suspend fun processCall(responseCall: suspend () -> Response<*>): Any? {
        if (!networkConnectivity.isConnected()) {
            return NO_INTERNET_CONNECTION
        }
        return try {
            val response = responseCall.invoke()
            val responseCode = response.code()
            if (response.isSuccessful) {
                response.body()
            } else {
                responseCode
            }
        } catch (e: IOException) {
            NETWORK_ERROR
        }
    }


    private fun processResponseCall(responseCall: Response<*>): Any? {
        if (!networkConnectivity.isConnected()) {
            return NO_INTERNET_CONNECTION
        }
        return try {
            val responseCode = responseCall.code()
            if (responseCall.isSuccessful) {
                responseCall.body()
            } else {
                responseCode
            }
        } catch (e: IOException) {
            NETWORK_ERROR
        }
    }
}
