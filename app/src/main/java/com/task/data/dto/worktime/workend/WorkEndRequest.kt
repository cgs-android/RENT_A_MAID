package com.task.data.dto.worktime.workend

data class WorkEndRequest(
    val token: String = "",
    val worklog_start_id: String = "",
    val ended_at: String = "",
    val comment: String = "",
    val status: String = ""
)