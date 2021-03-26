package com.task.data.dto.projectdetails

open class ProjectDetailsResponse(
    val status: Boolean,
    val message: String,
    val data: ProjectDetailsDataResponse
)