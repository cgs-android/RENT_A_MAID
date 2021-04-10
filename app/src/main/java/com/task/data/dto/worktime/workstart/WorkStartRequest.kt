package com.task.data.dto.worktime.workstart

data class WorkStartRequest(
    val token: String = "",
    val project_id: String = "",
    val started_at: String = "",
)