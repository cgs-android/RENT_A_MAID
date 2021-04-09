package com.task.data.remote.service

import com.task.data.dto.projecttraveldetails.ProjectTravelDetailsRequest
import com.task.data.dto.projecttraveldetails.ProjectTravelDetailsResponse
import com.task.data.dto.projectlist.ProjectListRequest
import com.task.data.dto.projectlist.ProjectListsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ProjectService {
    @POST("apis/projects/index.json")
    suspend fun fetchProjectList(@Body projectListRequest: ProjectListRequest): Response<ProjectListsResponse>

    @POST("apis/projects/single.json")
    suspend fun fetchProjectDetails(@Body projectTravelDetailsRequest: ProjectTravelDetailsRequest): Response<ProjectTravelDetailsResponse>
}