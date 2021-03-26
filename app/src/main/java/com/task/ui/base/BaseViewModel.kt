package com.task.ui.base

import androidx.lifecycle.ViewModel
import com.task.data.local.LocalData
import com.task.usecase.errors.ErrorManager
import javax.inject.Inject


abstract class BaseViewModel : ViewModel() {

    @Inject
    lateinit var errorManager: ErrorManager

    @Inject
    lateinit var localRepository: LocalData

    fun getToken(): String {
        return localRepository.getLoginResponseData().data.token
    }

}
