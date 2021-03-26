package com.task.data.remote

import com.task.data.Resource
import com.task.data.dto.login.LoginRequest
import com.task.data.dto.login.LoginResponse
import com.task.data.dto.projectdetails.ProjectDetailsRequest
import com.task.data.dto.projectdetails.ProjectDetailsResponse
import com.task.data.dto.projectlist.ProjectListRequest
import com.task.data.dto.projectlist.ProjectListsResponse
import com.task.data.dto.recipes.Recipes


internal interface RemoteDataSource {
    suspend fun requestRecipes(): Resource<Recipes>
    suspend fun requestLogin(loginRequest: LoginRequest): Resource<LoginResponse>
    suspend fun requestProjectList(projectListRequest: ProjectListRequest): Resource<ProjectListsResponse>
    suspend fun requestProjectDeatils(projectDetailsRequest: ProjectDetailsRequest): Resource<ProjectDetailsResponse>
}
