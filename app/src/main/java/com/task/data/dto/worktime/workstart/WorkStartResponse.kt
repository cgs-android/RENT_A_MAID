package com.task.data.dto.worktime.workstart


data class WorkStartResponse(
    val status: Boolean = false,
    val message: String = "",
    val data: WorkStartDataResponse
)


data class WorkStartDataResponse(
    val id: Int = 0,
    val project_id: Int = 0,
    val user_id: Int = 0
)