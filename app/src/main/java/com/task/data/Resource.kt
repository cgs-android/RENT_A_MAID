package com.task.data


sealed class Resource<T>(
    val data: T? = null,
    val errorCode: Int? = null,
    val failureData: T? = null,
) {
    class Success<T>(data: T) : Resource<T>(data)
    class SuccessHandling<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class DataError<T>(errorCode: Int) : Resource<T>(null, errorCode)
    class Failure<T>(failureData: T) : Resource<T>(failureData)

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is SuccessHandling<*> -> "Success[data=$data]"
            is Failure<*> -> "Success[data=$failureData]"
            is DataError -> "Error[exception=$errorCode]"
            is Loading<T> -> "Loading"
        }
    }
}
