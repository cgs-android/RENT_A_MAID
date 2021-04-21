package com.task.data.dto.project.gettraveldetails


data class GetTravelResponse(
    val status: Boolean = false,
    val message: String = "",
    val data: GetTravelDataResponse
)


data class GetTravelDataResponse(
    val id: Int = 0,
    val project_id: Int = 0,
    val user_id: Int = 0,
    val started_at: String = "",
    val travel_distance: Float = 0f,
    val ended_at: String = "",
)