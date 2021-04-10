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
import com.task.data.dto.worktime.workend.WorkEndRequest
import com.task.data.dto.worktime.workend.WorkEndResponse
import com.task.data.dto.worktime.workstart.WorkStartRequest
import com.task.data.dto.worktime.workstart.WorkStartResponse
import com.task.data.error.NETWORK_ERROR
import com.task.data.error.NO_INTERNET_CONNECTION
import com.task.data.local.LocalData
import com.task.data.remote.service.CredentialService
import com.task.data.remote.service.ProjectService
import com.task.utils.EnumIntUtils
import com.task.utils.NetworkConnectivity
import com.task.utils.SingleEvent
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject


class RemoteData @Inject
constructor(
    private val serviceGenerator: ServiceGenerator,
    private val localRepository: LocalData,
    private val networkConnectivity: NetworkConnectivity
) : RemoteDataSource {


    override suspend fun requestLogin(loginRequest: LoginRequest): Resource<LoginResponse> {
        val loginService = serviceGenerator.createService(CredentialService::class.java)
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorCode = NO_INTERNET_CONNECTION)
        }
        val loginData = loginService.fetchLogin(loginRequest)
        return when (val responseCode = loginData.code()) {
            EnumIntUtils.SUCCESS_CODE.code -> {
                if (loginData.isSuccessful) {
                    val responseBody = loginData.body()
                    if (responseBody!!.status) {
                        localRepository.putLoginResponseData(responseBody)
                        Resource.Success(data = responseBody)
                    } else {
                        Resource.Failure(failureData = responseBody)
                    }
                } else {
                    Resource.DataError(errorCode = responseCode)
                }
            }
            else -> {
                Resource.DataError(errorCode = responseCode)
            }
        }


    }

    override suspend fun requestProjectList(projectListRequest: ProjectListRequest): Resource<ProjectListsResponse> {
        val projectService = serviceGenerator.createService(ProjectService::class.java)
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorCode = NO_INTERNET_CONNECTION)
        }
        val projectListData = projectService.fetchProjectList(projectListRequest)
        return when (val responseCode = projectListData.code()) {
            EnumIntUtils.SUCCESS_CODE.code -> {
                if (projectListData.isSuccessful) {
                    val responseBody = projectListData.body()
                    if (responseBody!!.status) {
                        Resource.Success(data = responseBody)
                    } else {
                        Resource.Failure(failureData = responseBody)
                    }
                } else {
                    Resource.DataError(errorCode = responseCode)
                }
            }
            else -> {
                Resource.DataError(errorCode = responseCode)
            }
        }
    }


    override suspend fun requestProjectDeatils(projectTravelDetailsRequest: ProjectTravelDetailsRequest): Resource<ProjectTravelDetailsResponse> {
        val projectService = serviceGenerator.createService(ProjectService::class.java)
        if (!networkConnectivity.isConnected()) {
            return Resource.DataError(errorCode = NO_INTERNET_CONNECTION)
        }
        val projectDetailsData = projectService.fetchProjectDetails(projectTravelDetailsRequest)
        return when (val responseCode = projectDetailsData.code()) {
            EnumIntUtils.SUCCESS_CODE.code -> {
                if (projectDetailsData.isSuccessful) {
                    val responseBody = projectDetailsData.body()
                    if (responseBody!!.status) {
                        Resource.Success(data = responseBody)
                    } else {
                        Resource.Failure(failureData = responseBody)
                    }
                } else {
                    Resource.DataError(errorCode = responseCode)
                }
            }
            else -> {
                Resource.DataError(errorCode = responseCode)
            }
        }
    }

    override suspend fun requestTravelStartTime(travelStartRequest: TravelStartRequest): SingleEvent<Resource<TravelStartResponse>> {
        val projectService = serviceGenerator.createService(ProjectService::class.java)
        if (!networkConnectivity.isConnected()) {
            return SingleEvent(Resource.DataError(errorCode = NO_INTERNET_CONNECTION))
        }
        val travelStartTimeData = projectService.postTravelStartTime(travelStartRequest)
        return when (val responseCode = travelStartTimeData.code()) {
            EnumIntUtils.SUCCESS_CODE.code -> {
                if (travelStartTimeData.isSuccessful) {
                    val responseBody = travelStartTimeData.body()
                    if (responseBody!!.status) {
                        SingleEvent(Resource.Success(data = responseBody))
                    } else {
                        SingleEvent(Resource.Failure(failureData = responseBody))
                    }
                } else {
                    SingleEvent(Resource.DataError(errorCode = responseCode))
                }
            }
            else -> {
                SingleEvent(Resource.DataError(errorCode = responseCode))
            }
        }
    }

    override suspend fun requestTravelEndTime(travelEndRequest: TravelEndRequest): SingleEvent<Resource<TravelEndResponse>> {
        val projectService = serviceGenerator.createService(ProjectService::class.java)
        if (!networkConnectivity.isConnected()) {
            return SingleEvent(Resource.DataError(errorCode = NO_INTERNET_CONNECTION))
        }
        val travelEndTimeData = projectService.postTravelEndTime(travelEndRequest)
        return when (val responseCode = travelEndTimeData.code()) {
            EnumIntUtils.SUCCESS_CODE.code -> {
                if (travelEndTimeData.isSuccessful) {
                    val responseBody = travelEndTimeData.body()
                    if (responseBody!!.status) {
                        SingleEvent(Resource.Success(data = responseBody))
                    } else {
                        SingleEvent(Resource.Failure(failureData = responseBody))
                    }
                } else {
                    SingleEvent(Resource.DataError(errorCode = responseCode))
                }
            }
            else -> {
                SingleEvent(Resource.DataError(errorCode = responseCode))
            }
        }
    }

    override suspend fun requestWorkStartTime(workStartRequest: WorkStartRequest): SingleEvent<Resource<WorkStartResponse>> {
        val projectService = serviceGenerator.createService(ProjectService::class.java)
        if (!networkConnectivity.isConnected()) {
            return SingleEvent(Resource.DataError(errorCode = NO_INTERNET_CONNECTION))
        }
        val workStartTimeData = projectService.postWorkStartTime(workStartRequest)
        return when (val responseCode = workStartTimeData.code()) {
            EnumIntUtils.SUCCESS_CODE.code -> {
                if (workStartTimeData.isSuccessful) {
                    val responseBody = workStartTimeData.body()
                    if (responseBody!!.status) {
                        SingleEvent(Resource.Success(data = responseBody))
                    } else {
                        SingleEvent(Resource.Failure(failureData = responseBody))
                    }
                } else {
                    SingleEvent(Resource.DataError(errorCode = responseCode))
                }
            }
            else -> {
                SingleEvent(Resource.DataError(errorCode = responseCode))
            }
        }
    }

    override suspend fun requestWorkEndTime(
        workEndRequest: WorkEndRequest,
        event: Int
    ): SingleEvent<Resource<WorkEndResponse>> {
        val projectService = serviceGenerator.createService(ProjectService::class.java)
        if (!networkConnectivity.isConnected()) {
            return SingleEvent(Resource.DataError(errorCode = NO_INTERNET_CONNECTION))
        }
        val workEndTimeData = projectService.postWorkEndTime(workEndRequest)
        return when (val responseCode = workEndTimeData.code()) {
            EnumIntUtils.SUCCESS_CODE.code -> {
                if (workEndTimeData.isSuccessful) {
                    val responseBody = workEndTimeData.body()

                    if (responseBody!!.status) {
                        when (event) {
                            EnumIntUtils.ZERO.code -> {
                                SingleEvent(Resource.Success(data = responseBody))
                            }
                            EnumIntUtils.ONE.code -> {
                                SingleEvent(Resource.SuccessHandling(data = responseBody))
                            }
                            else -> {
                                SingleEvent(Resource.Success(data = responseBody))
                            }
                        }
                    } else {
                        SingleEvent(Resource.Failure(failureData = responseBody))
                    }

                } else {
                    SingleEvent(Resource.DataError(errorCode = responseCode))
                }
            }
            else -> {
                SingleEvent(Resource.DataError(errorCode = responseCode))
            }
        }
    }


    private suspend fun processCall(responseCall: suspend () -> Response<*>): Any? {
        if (!networkConnectivity.isConnected()) {
            return NO_INTERNET_CONNECTION
        }
        return try {
            val response = responseCall.invoke()
            val responseCode = response.code()
            if (response.isSuccessful) {
                response.body()
            } else {
                responseCode
            }
        } catch (e: IOException) {
            NETWORK_ERROR
        }
    }


}
