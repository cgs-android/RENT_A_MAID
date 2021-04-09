package com.task.data.dto.project.projectlist

data class ProjectListDataResponse(
    val project_details: ProjectDetailsResponse,
    val team_members: List<TeamMembers>
)

data class ProjectDetailsResponse(
    val id: String = "",
    val project_name: String = "",
    val description: String = "",
    val start_date: String = "",
    val end_date: String = "",
    var projectStatusColor: Int? = 0
)

data class TeamMembers(val Users: UsersResponse, val Roles: RolesResponse)
data class UsersResponse(
    val id: String = "",
    val first_name: String = "",
    val last_name: String = ""
)

data class RolesResponse(val rolename: String = "")