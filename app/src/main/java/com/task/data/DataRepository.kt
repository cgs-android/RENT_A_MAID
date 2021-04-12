package com.task.data

import com.task.data.dto.credential.login.LoginRequest
import com.task.data.dto.credential.login.LoginResponse
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
import com.task.data.local.LocalData
import com.task.data.remote.RemoteData
import com.task.utils.SingleEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class DataRepository @Inject constructor(
    private val remoteRepository: RemoteData,
    private val localRepository: LocalData,
    private val ioDispatcher: CoroutineContext
) : DataRepositorySource {


    override suspend fun doLogin(loginRequest: LoginRequest): Flow<Resource<LoginResponse>> {
        return flow {
            emit(remoteRepository.requestLogin(loginRequest))
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestProjectList(projectListRequest: ProjectListRequest): Flow<Resource<ProjectListsResponse>> {
        return flow {
            emit(remoteRepository.requestProjectList(projectListRequest))
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestProjectDeatils(projectTravelDetailsRequest: ProjectTravelDetailsRequest): Flow<Resource<ProjectTravelDetailsResponse>> {
        return flow {
            emit(remoteRepository.requestProjectDeatils(projectTravelDetailsRequest))
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestTravelStartTime(travelStartRequest: TravelStartRequest): Flow<SingleEvent<Resource<TravelStartResponse>>> {
        return flow {
            emit(remoteRepository.requestTravelStartTime(travelStartRequest))
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestTravelEndTime(travelEndRequest: TravelEndRequest): Flow<SingleEvent<Resource<TravelEndResponse>>> {
        return flow {
            emit(remoteRepository.requestTravelEndTime(travelEndRequest))
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestWorkStartTime(workStartRequest: WorkStartRequest): Flow<SingleEvent<Resource<WorkStartResponse>>> {
        return flow {
            emit(remoteRepository.requestWorkStartTime(workStartRequest))
        }.flowOn(ioDispatcher)
    }

    override suspend fun requestWorkEndTime(
        workEndRequest: WorkEndRequest,
        event: Int
    ): Flow<SingleEvent<Resource<WorkEndResponse>>> {
        return flow {
            emit(remoteRepository.requestWorkEndTime(workEndRequest, event))
        }.flowOn(ioDispatcher)
    }

    override suspend fun getTravelDetails(getTravelRequest: GetTravelRequest): Flow<Resource<GetTravelResponse>> {
        return flow {
            emit(remoteRepository.getTravelDetails(getTravelRequest))
        }.flowOn(ioDispatcher)
    }


}
