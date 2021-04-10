package com.task.data.dto.worktime.workend


data class WorkEndResponse(
    val status: Boolean = false,
    val message: String = "",
    val data: WorkEndDataResponse
)


data class WorkEndDataResponse(
    val id: Int = 0,
    val project_id: Int = 0,
    val user_id: Int = 0
)