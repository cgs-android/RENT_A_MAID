package com.task.ui.component.home.fragment.projectlist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.task.data.dto.project.projectlist.ProjectListsResponse
import com.task.databinding.ItemProjectlistBinding
import com.task.ui.component.home.fragment.projectlist.ProjectListViewModel
import com.task.ui.component.home.fragment.projectlist.listener.IProjectListListener


class ProjectListAdapter(
    private val projectListViewModel: ProjectListViewModel,
    private val projectListsResponse: ProjectListsResponse,
    private val context: Context
) : RecyclerView.Adapter<ProjectListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectListViewHolder {
        val itemBinding =
            ItemProjectlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectListViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ProjectListViewHolder, position: Int) {
        holder.bind(
            projectListsResponse.data[position].project_details,
            projectListsResponse.data[position].team_members,
            onItemClickListener,
            position,
            context
        )
    }

    override fun getItemCount(): Int {
        return projectListsResponse.data.size
    }

    private val onItemClickListener: IProjectListListener = object : IProjectListListener {
        override fun onProjectItemSelected(
            position: Int
        ) {
            projectListViewModel.onProjectListItemOnTap(
                projectListsResponse.data[position], position
            )
        }

    }
}

