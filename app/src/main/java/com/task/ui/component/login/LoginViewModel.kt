package com.task.ui.component.login

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.task.data.DataRepository
import com.task.data.DataRepositorySource
import com.task.data.Resource
import com.task.data.dto.credential.login.LoginRequest
import com.task.data.dto.credential.login.LoginResponse
import com.task.data.error.CHECK_YOUR_FIELDS
import com.task.data.error.PASS_WORD_ERROR
import com.task.data.error.USER_NAME_ERROR
import com.task.ui.base.BaseViewModel
import com.task.utils.NetworkConnectivity
import com.task.utils.RegexUtils.isValidEmail
import com.task.utils.SingleEvent
import com.task.utils.wrapEspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val mDataRepository: DataRepository,
    private val mDataRepositoryRepository: DataRepositorySource,
    private val mNetworkConnectivity: NetworkConnectivity
) :
    BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val loginLiveDataPrivate = MutableLiveData<Resource<LoginResponse>>()
    val loginLiveData: LiveData<Resource<LoginResponse>> get() = loginLiveDataPrivate


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate


    fun doLogin(userEmail: String, passWord: String) {
        val isUserEmailValid = isValidEmail(userEmail)
        val isPassWordValid = passWord.trim().length > 4
        if (isUserEmailValid && !isPassWordValid) {
            loginLiveDataPrivate.value = Resource.DataError(PASS_WORD_ERROR)
        } else if (!isUserEmailValid && isPassWordValid) {
            loginLiveDataPrivate.value = Resource.DataError(USER_NAME_ERROR)
        } else if (!isUserEmailValid && !isPassWordValid) {
            loginLiveDataPrivate.value = Resource.DataError(CHECK_YOUR_FIELDS)
        } else {
            viewModelScope.launch {
                loginLiveDataPrivate.value = Resource.Loading()
                wrapEspressoIdlingResource {
                    mDataRepositoryRepository.doLogin(
                        loginRequest = LoginRequest(
                            userEmail,
                            passWord
                        )
                    ).collect {
                        loginLiveDataPrivate.value = it
                    }
                }
            }
        }
    }


    fun isNetworkAvailable(): Boolean {
        return mNetworkConnectivity.isConnected()
    }

    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun showFailureToastMessage(error: String) {
        showToastPrivate.value = SingleEvent(error)
    }
}
