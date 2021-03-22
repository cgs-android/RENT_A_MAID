package com.task.ui.component.home.fragment.projectlist.adapter

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.task.R
import com.task.data.dto.projectlist.ProjectListResponse
import com.task.databinding.ProjectlistItemBinding
import com.task.ui.component.home.fragment.projectlist.listener.IProjectListListener


class ProjectListViewHolder(private val itemBinding: ProjectlistItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(
        projectListResponse: ProjectListResponse,
        iProjectListListener: IProjectListListener,
        position: Int,
        context: Context
    ) {
        itemBinding.pliProjectIdTextView.text = projectListResponse.projectId
        itemBinding.pliProjectDescriptionTextView.text = projectListResponse.projectDescription
        itemBinding.pliProjectDateTextView.text = projectListResponse.projectDate

        itemBinding.pliWholeConstraintLayout.setOnClickListener {
            iProjectListListener.onProjectItemSelected(position)
        }

        when (projectListResponse.isRecentJobs) {
            true -> {
                itemBinding.pliProjectDetailsConstraintLayout.visibility = View.VISIBLE
                itemBinding.pliProjectStatusImageView.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.colorGreen
                    )
                )
            }
            false -> {
                itemBinding.pliProjectDetailsConstraintLayout.visibility = View.GONE
                itemBinding.pliProjectStatusImageView.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.colorBlue
                    )
                )
            }
        }

        if (position == 1) {
            itemBinding.pliProjectStatusImageView.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.colorOrange
                )
            )
        }


    }
}

