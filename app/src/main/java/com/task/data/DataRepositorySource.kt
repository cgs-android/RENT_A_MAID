package com.task.data


import com.task.data.dto.login.LoginRequest
import com.task.data.dto.login.LoginResponse
import com.task.data.dto.projecttraveldetails.ProjectTravelDetailsRequest
import com.task.data.dto.projecttraveldetails.ProjectTravelDetailsResponse
import com.task.data.dto.projectlist.ProjectListRequest
import com.task.data.dto.projectlist.ProjectListsResponse
import kotlinx.coroutines.flow.Flow


interface DataRepositorySource {
    suspend fun doLogin(loginRequest: LoginRequest): Flow<Resource<LoginResponse>>
    suspend fun requestProjectList(projectListRequest: ProjectListRequest): Flow<Resource<ProjectListsResponse>>
    suspend fun requestProjectDeatils(projectTravelDetailsRequest: ProjectTravelDetailsRequest): Flow<Resource<ProjectTravelDetailsResponse>>
}
