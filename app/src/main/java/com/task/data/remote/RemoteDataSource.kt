package com.task.data.remote

import com.task.data.Resource
import com.task.data.dto.credential.login.LoginRequest
import com.task.data.dto.credential.login.LoginResponse
import com.task.data.dto.project.gettraveldetails.GetTravelRequest
import com.task.data.dto.project.gettraveldetails.GetTravelResponse
import com.task.data.dto.project.getworkdetails.GetWorkDetailListsResponse
import com.task.data.dto.project.getworkdetails.GetWorkDetailRequest
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
import com.task.utils.SingleEvent


internal interface RemoteDataSource {
    suspend fun requestLogin(loginRequest: LoginRequest): Resource<LoginResponse>
    suspend fun requestProjectList(projectListRequest: ProjectListRequest): Resource<ProjectListsResponse>
    suspend fun requestProjectDeatils(projectTravelDetailsRequest: ProjectTravelDetailsRequest): Resource<ProjectTravelDetailsResponse>
    suspend fun requestTravelStartTime(travelStartRequest: TravelStartRequest): SingleEvent<Resource<TravelStartResponse>>
    suspend fun requestTravelEndTime(travelEndRequest: TravelEndRequest): SingleEvent<Resource<TravelEndResponse>>
    suspend fun requestWorkStartTime(workStartRequest: WorkStartRequest): SingleEvent<Resource<WorkStartResponse>>
    suspend fun requestWorkEndTime(
        workEndRequest: WorkEndRequest,
        event: Int
    ): SingleEvent<Resource<WorkEndResponse>>

    suspend fun getTravelDetails(getTravelRequest: GetTravelRequest): Resource<GetTravelResponse>
    suspend fun getWorkDetails(getWorkDetailRequest: GetWorkDetailRequest): Resource<GetWorkDetailListsResponse>
}
