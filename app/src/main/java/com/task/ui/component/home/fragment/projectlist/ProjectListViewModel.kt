package com.task.ui.component.home.fragment.projectlist

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.task.data.dto.drawer.DrawerResponse
import com.task.data.dto.projectlist.ProjectListResponse
import com.task.ui.base.BaseViewModel
import com.task.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProjectListViewModel @Inject constructor() : BaseViewModel() {


    private var projectListResponse: List<ProjectListResponse>? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openProjectDetailsPrivate = MutableLiveData<Int>()
    val openProjectDetails: LiveData<Int> get() = openProjectDetailsPrivate


    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun loadStaticProjectListData(): List<ProjectListResponse> {
        projectListResponse = listOf(
            ProjectListResponse(
                "Project 456",
                "Albert Ritschard [TL], \n" +
                        "Susanne Iten, Franco Riccio, Martianne Aeby, Marta Amman",
                "Today",
                true,
                true
            ),
            ProjectListResponse(
                "Project 457", "Martianne Aeby [TL], Susanne Iten, \n" +
                        "Franco Riccio, Marta Amman, \n" +
                        "Giovanni Ceriello", "Tomorrow", true, true
            ),
            ProjectListResponse(
                "Project 472",
                "Susanne Iten, Franco Riccio, Martianne Aeby, Marta Amman",
                "11.04.2021",
                false, false
            ),
            ProjectListResponse(
                "Project 485",
                "Martianne Aeby [TL], Susanne Iten, ",
                "16.04.2021",
                false, false
            )
        )
        return projectListResponse as List<ProjectListResponse>
    }

    fun onProjectListItemOnTap(position: Int) {
        openProjectDetailsPrivate.value = position
    }
}