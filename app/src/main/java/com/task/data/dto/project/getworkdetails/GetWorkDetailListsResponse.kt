package com.task.data.dto.project.getworkdetails


data class GetWorkDetailListsResponse(
    val status: Boolean = false,
    val message: String = "",
    val data: List<GetWorkDetailListData>
)

data class GetWorkDetailListData(
    val id: Int = 0,
    val project_id: Int = 0,
    val user_id: Int = 0,
    val started_at: String = "",
    val ended_at: String = ""
)