package com.task.data.dto.project.travelend


data class TravelEndResponse(
    val status: Boolean = false,
    val message: String = "",
    val data: TravelEndDataResponse
)


data class TravelEndDataResponse(
    val id: Int = 0,
    val project_id: Int = 0,
    val user_id: Int = 0
)