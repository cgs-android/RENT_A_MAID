package com.task.data.dto.project.travelstart

data class TravelStartRequest(
    val token: String = "",
    val project_id: String = "",
    val started_at: String = "",
    val start_geo_location: String = ""
)