package com.task.ui.component.home.fragment.projectworkdetail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.task.data.dto.worktime.WorkLogResponse
import com.task.databinding.ItemProjectlistBinding
import com.task.databinding.ItemProjectworkLogBinding
import com.task.ui.component.home.fragment.projectlist.listener.IProjectListListener


class ProjectWorkLogAdapter(
    private val context: Context,
    private val workLogResponse: MutableList<WorkLogResponse>
) : RecyclerView.Adapter<ProjectWorkLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectWorkLogViewHolder {
        val itemBinding =
            ItemProjectworkLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectWorkLogViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ProjectWorkLogViewHolder, position: Int) {
        holder.bind(
            workLogResponse[position],
            onItemClickListener,
            position,
            context
        )
    }

    override fun getItemCount(): Int {
        return workLogResponse.size
    }

    private val onItemClickListener: IProjectListListener = object : IProjectListListener {
        override fun onProjectItemSelected(
            position: Int
        ) {

        }

    }


}

