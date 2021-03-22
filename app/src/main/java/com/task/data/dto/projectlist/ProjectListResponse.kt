package com.task.data.dto.projectlist

data class ProjectListResponse(
    var projectId: String?,
    var projectDescription: String?,
    var projectDate: String?,
    var isExpand: Boolean?,
    var isRecentJobs: Boolean?,
)