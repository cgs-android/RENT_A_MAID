package com.task.ui.component.home.fragment.projectworkdetail

import android.app.Activity
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.task.data.DataRepositorySource
import com.task.data.Resource
import com.task.data.dto.project.gettraveldetails.GetTravelResponse
import com.task.data.dto.project.getworkdetails.GetWorkDetailListsResponse
import com.task.data.dto.project.getworkdetails.GetWorkDetailRequest
import com.task.data.dto.project.projecttraveldetails.ProjectTravelDetailsRequest
import com.task.data.dto.project.projecttraveldetails.ProjectTravelDetailsResponse
import com.task.data.dto.project.travelend.TravelEndRequest
import com.task.data.dto.project.travelend.TravelEndResponse
import com.task.data.dto.project.travelstart.TravelStartRequest
import com.task.data.dto.project.travelstart.TravelStartResponse
import com.task.data.dto.worktime.WorkLogResponse
import com.task.data.dto.worktime.workend.WorkEndRequest
import com.task.data.dto.worktime.workend.WorkEndResponse
import com.task.data.dto.worktime.workstart.WorkStartRequest
import com.task.data.dto.worktime.workstart.WorkStartResponse
import com.task.ui.base.BaseViewModel
import com.task.utils.DateUtils
import com.task.utils.SingleEvent
import com.task.utils.wrapEspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectWorkDetailsViewModel @Inject constructor(
    private val mDataRepositoryRepository: DataRepositorySource
) : BaseViewModel() {

    private var workLogResponseList: MutableList<WorkLogResponse> = mutableListOf()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val projectDetailsPrivate = MutableLiveData<Resource<ProjectTravelDetailsResponse>>()
    val projectWorkDetails: LiveData<Resource<ProjectTravelDetailsResponse>> get() = projectDetailsPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val getWorkDetailsPrivate = MutableLiveData<Resource<GetWorkDetailListsResponse>>()
    val getWorkDetails: LiveData<Resource<GetWorkDetailListsResponse>> get() = getWorkDetailsPrivate


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val workStartPrivate = MutableLiveData<SingleEvent<Resource<WorkStartResponse>>>()
    val workStart: LiveData<SingleEvent<Resource<WorkStartResponse>>> get() = workStartPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val workEndPrivate = MutableLiveData<SingleEvent<Resource<WorkEndResponse>>>()
    val workEnd: LiveData<SingleEvent<Resource<WorkEndResponse>>> get() = workEndPrivate


    fun loadWorkLogData(
        activity: Activity, workLogResponse: WorkLogResponse
    ): MutableList<WorkLogResponse> {
        this.workLogResponseList.add(workLogResponse)
        return workLogResponseList
    }

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

    fun getWorkDetails() {
        viewModelScope.launch {
            getWorkDetailsPrivate.value = Resource.Loading()
            wrapEspressoIdlingResource {
                mDataRepositoryRepository.getWorkDetails(
                    getWorkDetailRequest = GetWorkDetailRequest(
                        getToken(),
                        getProjectId(),
                        DateUtils.getCurrentDate()
                    )
                ).collect {
                    getWorkDetailsPrivate.value = it
                }
            }
        }
    }

    fun postWorkStartTime(startedAt: String) {
        viewModelScope.launch {
            workStartPrivate.value = SingleEvent(Resource.Loading())
            wrapEspressoIdlingResource {
                mDataRepositoryRepository.requestWorkStartTime(
                    workStartRequest = WorkStartRequest(
                        getToken(),
                        getProjectId(),
                        startedAt,
                    )
                ).collect {
                    workStartPrivate.value = it
                }
            }
        }
    }

    fun postWorkEndTime(endAt: String, comment: String, status: String, events: Int) {
        viewModelScope.launch {
            workEndPrivate.value = SingleEvent(Resource.Loading())
            wrapEspressoIdlingResource {
                mDataRepositoryRepository.requestWorkEndTime(
                    workEndRequest = WorkEndRequest(
                        getToken(),
                        getTravelStartId(),
                        endAt,
                        comment,
                        status
                    ), event = events
                ).collect {
                    workEndPrivate.value = it
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

    fun clearList() {
        workLogResponseList = mutableListOf()
    }



    fun storeLocalTravelStartId(travelStartId: Int) {
        localRepository.putTravelStartId(travelStartId)
    }
}