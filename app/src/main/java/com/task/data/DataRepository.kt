package com.task.data

import com.task.data.dto.login.LoginRequest
import com.task.data.dto.login.LoginResponse
import com.task.data.dto.projecttraveldetails.ProjectTravelDetailsRequest
import com.task.data.dto.projecttraveldetails.ProjectTravelDetailsResponse
import com.task.data.dto.projectlist.ProjectListRequest
import com.task.data.dto.projectlist.ProjectListsResponse
import com.task.data.local.LocalData
import com.task.data.remote.RemoteData
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


}
