package com.task.ui.component.home.fragment.projectdetail

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.task.data.DataRepositorySource
import com.task.data.Resource
import com.task.data.dto.projectdetails.ProjectDetailsRequest
import com.task.data.dto.projectdetails.ProjectDetailsResponse
import com.task.data.dto.projectlist.ProjectListRequest
import com.task.data.dto.projectlist.ProjectListsResponse
import com.task.ui.base.BaseViewModel
import com.task.utils.DialogHelper
import com.task.utils.SingleEvent
import com.task.utils.wrapEspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectDetailsViewModel @Inject constructor(
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
    private val projectDetailsPrivate = MutableLiveData<Resource<ProjectDetailsResponse>>()
    val projectDetails: LiveData<Resource<ProjectDetailsResponse>> get() = projectDetailsPrivate

    fun getProjectDetails(projectId: String) {
        viewModelScope.launch {
            projectDetailsPrivate.value = Resource.Loading()
            wrapEspressoIdlingResource {
                mDataRepositoryRepository.requestProjectDeatils(
                    projectDetailsRequest = ProjectDetailsRequest(
                        getToken(),
                        projectId
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
}