package com.task.data


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
import com.task.data.dto.worktime.workend.WorkEndRequest
import com.task.data.dto.worktime.workend.WorkEndResponse
import com.task.data.dto.worktime.workstart.WorkStartRequest
import com.task.data.dto.worktime.workstart.WorkStartResponse
import com.task.utils.SingleEvent
import kotlinx.coroutines.flow.Flow


interface DataRepositorySource {
    suspend fun doLogin(loginRequest: LoginRequest): Flow<Resource<LoginResponse>>
    suspend fun requestProjectList(projectListRequest: ProjectListRequest): Flow<Resource<ProjectListsResponse>>
    suspend fun requestProjectDeatils(projectTravelDetailsRequest: ProjectTravelDetailsRequest): Flow<Resource<ProjectTravelDetailsResponse>>
    suspend fun requestTravelStartTime(travelStartRequest: TravelStartRequest): Flow<SingleEvent<Resource<TravelStartResponse>>>
    suspend fun requestTravelEndTime(travelEndRequest: TravelEndRequest): Flow<SingleEvent<Resource<TravelEndResponse>>>
    suspend fun requestWorkStartTime(workStartRequest: WorkStartRequest): Flow<SingleEvent<Resource<WorkStartResponse>>>
    suspend fun requestWorkEndTime(
        workEndRequest: WorkEndRequest,
        event: Int
    ): Flow<SingleEvent<Resource<WorkEndResponse>>>
}
