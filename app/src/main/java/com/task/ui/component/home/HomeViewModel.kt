package com.task.ui.component.home

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.task.data.DataRepository
import com.task.data.Resource
import com.task.data.dto.drawer.DrawerResponse
import com.task.data.dto.recipes.RecipesItem
import com.task.ui.base.BaseViewModel
import com.task.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {

    private var drawerResponse: List<DrawerResponse>? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openDrawerDetailsPrivate = MutableLiveData<Int>()
    val openDrawerDetails: LiveData<Int> get() = openDrawerDetailsPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate


    fun loadDrawerData(): List<DrawerResponse> {
        drawerResponse = listOf(
            DrawerResponse("Home"),
            DrawerResponse("Project Details"),
            DrawerResponse("Account Details")
        )
        return drawerResponse as List<DrawerResponse>
    }

    fun onDrawerItemOnTap(position: Int) {
        openDrawerDetailsPrivate.value = position
    }


    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }
}