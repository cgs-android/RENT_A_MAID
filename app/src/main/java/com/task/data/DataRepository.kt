package com.task.data

import com.task.data.dto.login.LoginRequest
import com.task.data.dto.login.LoginResponse
import com.task.data.dto.projectdetails.ProjectDetailsRequest
import com.task.data.dto.projectdetails.ProjectDetailsResponse
import com.task.data.dto.projectlist.ProjectListRequest
import com.task.data.dto.projectlist.ProjectListsResponse
import com.task.data.dto.recipes.Recipes
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

    override suspend fun requestRecipes(): Flow<Resource<Recipes>> {
        return flow {
            emit(remoteRepository.requestRecipes())
        }.flowOn(ioDispatcher)
    }

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

    override suspend fun requestProjectDeatils(projectDetailsRequest: ProjectDetailsRequest): Flow<Resource<ProjectDetailsResponse>> {
        return flow {
            emit(remoteRepository.requestProjectDeatils(projectDetailsRequest))
        }.flowOn(ioDispatcher)
    }


    override suspend fun addToFavourite(id: String): Flow<Resource<Boolean>> {
        return flow {
            localRepository.getCachedFavourites().let {
                it.data?.toMutableSet()?.let { set ->
                    val isAdded = set.add(id)
                    if (isAdded) {
                        emit(localRepository.cacheFavourites(set))
                    } else {
                        emit(Resource.Success(false))
                    }
                }
                it.errorCode?.let { errorCode ->
                    emit(Resource.DataError<Boolean>(errorCode))
                }
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun removeFromFavourite(id: String): Flow<Resource<Boolean>> {
        return flow {
            emit(localRepository.removeFromFavourites(id))
        }.flowOn(ioDispatcher)
    }

    override suspend fun isFavourite(id: String): Flow<Resource<Boolean>> {
        return flow {
            emit(localRepository.isFavourite(id))
        }.flowOn(ioDispatcher)
    }
}
