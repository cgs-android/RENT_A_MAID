package com.task.ui.component.home.fragment.projectworkdetail

import android.app.Activity
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.task.data.DataRepositorySource
import com.task.data.Resource
import com.task.data.dto.project.projecttraveldetails.ProjectTravelDetailsRequest
import com.task.data.dto.project.projecttraveldetails.ProjectTravelDetailsResponse
import com.task.data.dto.worktime.WorkLogResponse
import com.task.ui.base.BaseViewModel
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
}