package com.task.data.dto.project.getworkdetails

data class GetWorkDetailRequest(
    val token: String = "",
    val project_id: String = "",
    val entry_date: String = ""
)