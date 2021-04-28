package com.task.data.dto.project.projecttraveldetails

open class ProjectTravelDetailsResponse(
    val status: Boolean = false,
    val message: String = "",
    val data: ProjectDetailsDataResponse
)