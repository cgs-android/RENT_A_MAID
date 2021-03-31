package com.task.data.remote

import com.task.data.Resource
import com.task.data.dto.login.LoginRequest
import com.task.data.dto.login.LoginResponse
import com.task.data.dto.projecttraveldetails.ProjectTravelDetailsRequest
import com.task.data.dto.projecttraveldetails.ProjectTravelDetailsResponse
import com.task.data.dto.projectlist.ProjectListRequest
import com.task.data.dto.projectlist.ProjectListsResponse


internal interface RemoteDataSource {
    suspend fun requestLogin(loginRequest: LoginRequest): Resource<LoginResponse>
    suspend fun requestProjectList(projectListRequest: ProjectListRequest): Resource<ProjectListsResponse>
    suspend fun requestProjectDeatils(projectTravelDetailsRequest: ProjectTravelDetailsRequest): Resource<ProjectTravelDetailsResponse>
}
