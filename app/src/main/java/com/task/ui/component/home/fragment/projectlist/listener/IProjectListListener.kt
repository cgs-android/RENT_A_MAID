package com.task.ui.component.home.fragment.projectlist.listener

import com.task.data.dto.projectlist.ProjectListDataResponse
import java.text.FieldPosition

interface IProjectListListener {
    fun onProjectItemSelected(position: Int)
}