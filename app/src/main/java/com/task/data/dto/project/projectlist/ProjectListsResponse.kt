package com.task.data.dto.project.projectlist

data class ProjectListsResponse(
    val status: Boolean=false,
    val message: String="",
    val data: List<ProjectListDataResponse>
)