package com.task.data.dto.project.projecttraveldetails

import com.task.data.dto.project.projectlist.TeamMembers

open class ProjectDetailsDataResponse(
    val project_details: ProjectDetails,
    val project_location: ProjectLocation,
    val team_members: List<TeamMembers>
)


data class ProjectDetails(
    val id: String = "",
    val proj_comp_same_aadress: Boolean = false,
    val company_id: Int = 0,
    val work_start_time: String = "",
    val work_end_time: String = "",
    val start_date: String = "",
)

data class ProjectLocation(
    val contact_person: String = "",
    val company_name: String = "",
    val address_line1: String = "",
    val address_line2: String = "",
    val pincode: String = "",
    val city: String = "",
    val country: String = ""
)