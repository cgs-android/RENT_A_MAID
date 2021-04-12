package com.task.data.remote.service

import com.task.data.dto.project.gettraveldetails.GetTravelRequest
import com.task.data.dto.project.gettraveldetails.GetTravelResponse
import com.task.data.dto.project.projecttraveldetails.ProjectTravelDetailsRequest
import com.task.data.dto.project.projecttraveldetails.ProjectTravelDetailsResponse
import com.task.data.dto.project.projectlist.ProjectListRequest
import com.task.data.dto.project.projectlist.ProjectListsResponse
import com.task.data.dto.project.travelend.TravelEndRequest
import com.task.data.dto.project.travelend.TravelEndResponse
import com.task.data.dto.project.travelstart.TravelStartRequest
import com.task.data.dto.project.travelstart.TravelStartResponse
import com.task.data.dto.worktime.workend.WorkEndRequest
import com.task.data.dto.worktime.workend.WorkEndResponse
import com.task.data.dto.worktime.workstart.WorkStartRequest
import com.task.data.dto.worktime.workstart.WorkStartResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ProjectService {
    @POST("apis/projects/index.json")
    suspend fun fetchProjectList(@Body projectListRequest: ProjectListRequest): Response<ProjectListsResponse>

    @POST("apis/projects/single.json")
    suspend fun fetchProjectDetails(@Body projectTravelDetailsRequest: ProjectTravelDetailsRequest): Response<ProjectTravelDetailsResponse>

    @POST("apis/project-travels/start.json")
    suspend fun postTravelStartTime(@Body travelStartRequest: TravelStartRequest): Response<TravelStartResponse>

    @POST("apis/project-travels/end.json")
    suspend fun postTravelEndTime(@Body travelEndRequest: TravelEndRequest): Response<TravelEndResponse>

    @POST("apis/work-logs/start.json")
    suspend fun postWorkStartTime(@Body workStartRequest: WorkStartRequest): Response<WorkStartResponse>

    @POST("apis/work-logs/end.json")
    suspend fun postWorkEndTime(@Body workEndRequest: WorkEndRequest): Response<WorkEndResponse>

    @POST("apis/project-travels/get-today-travel-time.json")
    suspend fun getTravelDetails(@Body getTravelRequest: GetTravelRequest): Response<GetTravelResponse>

}