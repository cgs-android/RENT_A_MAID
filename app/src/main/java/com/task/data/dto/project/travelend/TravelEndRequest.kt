package com.task.data.dto.project.travelend

data class TravelEndRequest(
    val token: String = "",
    val travel_start_id: String = "",
    val ended_at: String = "",
    val end_geo_location: String = "",
    val travel_distance: String = "",
)