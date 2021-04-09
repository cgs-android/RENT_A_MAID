package com.task.data.dto.project.travelstart


data class TravelStartResponse(
    val status: Boolean = false,
    val message: String = "",
    val data: TravelStartDataResponse
)


data class TravelStartDataResponse(
    val id: Int = 0,
    val project_id: Int = 0,
    val user_id: Int = 0
)