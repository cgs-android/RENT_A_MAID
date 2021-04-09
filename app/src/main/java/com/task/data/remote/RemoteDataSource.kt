package com.task.data.remote

import com.task.data.Resource
import com.task.data.dto.credential.login.LoginRequest
import com.task.data.dto.credential.login.LoginResponse
import com.task.data.dto.project.projecttraveldetails.ProjectTravelDetailsRequest
import com.task.data.dto.project.projecttraveldetails.ProjectTravelDetailsResponse
import com.task.data.dto.project.projectlist.ProjectListRequest
import com.task.data.dto.project.projectlist.ProjectListsResponse
import com.task.data.dto.project.travelend.TravelEndRequest
import com.task.data.dto.project.travelend.TravelEndResponse
import com.task.data.dto.project.travelstart.TravelStartRequest
import com.task.data.dto.project.travelstart.TravelStartResponse
import com.task.utils.SingleEvent


internal interface RemoteDataSource {
    suspend fun requestLogin(loginRequest: LoginRequest): Resource<LoginResponse>
    suspend fun requestProjectList(projectListRequest: ProjectListRequest): Resource<ProjectListsResponse>
    suspend fun requestProjectDeatils(projectTravelDetailsRequest: ProjectTravelDetailsRequest): Resource<ProjectTravelDetailsResponse>
    suspend fun requestTravelStartTime(travelStartRequest: TravelStartRequest): SingleEvent<Resource<TravelStartResponse>>
    suspend fun requestTravelEndTime(travelEndRequest: TravelEndRequest): SingleEvent<Resource<TravelEndResponse>>
}
