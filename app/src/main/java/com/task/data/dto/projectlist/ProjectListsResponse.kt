package com.task.data.dto.projectlist

data class ProjectListsResponse(
    val status: Boolean,
    val message: String,
    val data: List<ProjectListDataResponse>
)