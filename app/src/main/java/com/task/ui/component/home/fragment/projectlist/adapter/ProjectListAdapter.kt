package com.task.ui.component.home.fragment.projectlist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.task.data.dto.projectlist.ProjectListResponse
import com.task.databinding.ProjectlistItemBinding
import com.task.ui.component.home.fragment.projectlist.ProjectListViewModel
import com.task.ui.component.home.fragment.projectlist.listener.IProjectListListener


class ProjectListAdapter(
    private val projectListViewModel: ProjectListViewModel,
    private val projectListResponse: List<ProjectListResponse>,
    private val context: Context
) : RecyclerView.Adapter<ProjectListViewHolder>() {

    private val onItemClickListener: IProjectListListener = object : IProjectListListener {
        override fun onProjectItemSelected(position: Int) {
            projectListViewModel.onProjectListItemOnTap(position)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectListViewHolder {
        val itemBinding =
            ProjectlistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectListViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ProjectListViewHolder, position: Int) {
        holder.bind(projectListResponse[position], onItemClickListener, position, context)
    }

    override fun getItemCount(): Int {
        return projectListResponse.size
    }
}

