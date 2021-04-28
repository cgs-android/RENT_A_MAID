package com.task.ui.component.home.fragment.projectlist

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.task.data.DataRepositorySource
import com.task.data.Resource
import com.task.data.dto.project.projectlist.ProjectListDataResponse
import com.task.data.dto.project.projectlist.ProjectListRequest
import com.task.data.dto.project.projectlist.ProjectListsResponse
import com.task.ui.base.BaseViewModel
import com.task.utils.SingleEvent
import com.task.utils.wrapEspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectListViewModel @Inject constructor(
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
    private val projectDetailsPrivate = MutableLiveData<Resource<ProjectListsResponse>>()
    val projectDetails: LiveData<Resource<ProjectListsResponse>> get() = projectDetailsPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openProjectDetailsPrivate = MutableLiveData<SingleEvent<ProjectListDataResponse>>()
    val openProjectDetails: LiveData<SingleEvent<ProjectListDataResponse>> get() = openProjectDetailsPrivate


    fun getAllProjectList() {
        viewModelScope.launch {
            projectDetailsPrivate.value = Resource.Loading()
            wrapEspressoIdlingResource {
                mDataRepositoryRepository.requestProjectList(
                    projectListRequest = ProjectListRequest(
                        getToken(),
                    )
                ).collect {
                    projectDetailsPrivate.value = it
                }
            }
        }
    }

    fun onProjectListItemOnTap(
        projectListDataResponse: ProjectListDataResponse,
        position: Int
    ) {
        openProjectDetailsPrivate.value = SingleEvent(projectListDataResponse)
    }

    fun localPrefStoreProjectId(projectId: String) {
        localRepository.putProjectId(projectId)
    }

    fun putLocalUserRole(userStatus: Boolean) {
        localRepository.putUserRoleStatus(userStatus)
    }


    fun showFailureToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }

    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun getLoginUserId(): String {
        return getUserId().toString()
    }
}