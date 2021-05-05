package com.task.ui.component.home.fragment.projecttraveldetail

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.task.data.DataRepositorySource
import com.task.data.Resource
import com.task.data.dto.project.gettraveldetails.GetTravelRequest
import com.task.data.dto.project.gettraveldetails.GetTravelResponse
import com.task.data.dto.project.projecttraveldetails.ProjectTravelDetailsRequest
import com.task.data.dto.project.projecttraveldetails.ProjectTravelDetailsResponse
import com.task.data.dto.project.travelend.TravelEndRequest
import com.task.data.dto.project.travelend.TravelEndResponse
import com.task.data.dto.project.travelstart.TravelStartRequest
import com.task.data.dto.project.travelstart.TravelStartResponse
import com.task.ui.base.BaseViewModel
import com.task.utils.DateUtils
import com.task.utils.SingleEvent
import com.task.utils.wrapEspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectTravelDetailsViewModel @Inject constructor(
    private val mDataRepositoryRepository: DataRepositorySource
) :
    BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val projectDetailsPrivate = MutableLiveData<Resource<ProjectTravelDetailsResponse>>()
    val projectTravelDetails: LiveData<Resource<ProjectTravelDetailsResponse>> get() = projectDetailsPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val getTravelDetailsPrivate = MutableLiveData<Resource<GetTravelResponse>>()
    val getTravelDetails: LiveData<Resource<GetTravelResponse>> get() = getTravelDetailsPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val travelStartPrivate = MutableLiveData<SingleEvent<Resource<TravelStartResponse>>>()
    val travelStart: LiveData<SingleEvent<Resource<TravelStartResponse>>> get() = travelStartPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val travelEndPrivate = MutableLiveData<SingleEvent<Resource<TravelEndResponse>>>()
    val travelEnd: LiveData<SingleEvent<Resource<TravelEndResponse>>> get() = travelEndPrivate

    fun getProjectDetails() {
        viewModelScope.launch {
            projectDetailsPrivate.value = Resource.Loading()
            wrapEspressoIdlingResource {
                mDataRepositoryRepository.requestProjectDeatils(
                    projectTravelDetailsRequest = ProjectTravelDetailsRequest(
                        getToken(),
                        getProjectId()
                    )
                ).collect {
                    projectDetailsPrivate.value = it
                }
            }
        }
    }

    fun postTravelStartTime(startedAt: String, startGeoLocation: String) {
        viewModelScope.launch {
            travelStartPrivate.value = SingleEvent(Resource.Loading())
            wrapEspressoIdlingResource {
                mDataRepositoryRepository.requestTravelStartTime(
                    travelStartRequest = TravelStartRequest(
                        getToken(),
                        getProjectId(),
                        startedAt,
                        startGeoLocation
                    )
                ).collect {
                    travelStartPrivate.value = it
                }
            }
        }
    }

    fun postTravelEndTime(endAt: String, endGeoLocation: String) {
        viewModelScope.launch {
            travelEndPrivate.value = SingleEvent(Resource.Loading())
            wrapEspressoIdlingResource {
                mDataRepositoryRepository.requestTravelEndTime(
                    travelEndRequest = TravelEndRequest(
                        getToken(),
                        getTravelStartId(),
                        endAt,
                        endGeoLocation,
                        getTotalTravelDistance()
                    )
                ).collect {
                    travelEndPrivate.value = it
                }
            }
        }
    }

    fun getTravelDetails() {
        viewModelScope.launch {
            getTravelDetailsPrivate.value = Resource.Loading()
            wrapEspressoIdlingResource {
                mDataRepositoryRepository.getTravelDetails(
                    getTravelRequest = GetTravelRequest(
                        getToken(),
                        getProjectId(),
                        DateUtils.getCurrentDate()
                    )
                ).collect {
                    getTravelDetailsPrivate.value = it
                }
            }
        }
    }

    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun showFailureToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

    fun showSuccessToastMessage(success: String) {
        showToastPrivate.value = SingleEvent(success)
    }

    fun storeLocalTravelStartId(travelStartId: Int) {
        localRepository.putTravelStartId(travelStartId)
    }

    fun localPrefStoreProjectId(projectId: String) {
        localRepository.putProjectId(projectId)
    }

    fun localPrefStoreProjectIdForBackgroundService(projectId: String) {
        localRepository.putProjectIdForBackgroundService(projectId)
    }

    fun locaPrefUpdatePauseTravelStatus(pauseStatus: Boolean) {
        localRepository.putIsTravelPause(pauseStatus)
    }

}