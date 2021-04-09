package com.task.ui.component.home.fragment.projectworkdetail.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

import com.task.data.dto.worktime.WorkLogResponse
import com.task.databinding.ItemProjectworkLogBinding
import com.task.ui.component.home.fragment.projectlist.listener.IProjectListListener


class ProjectWorkLogViewHolder(private val itemBinding: ItemProjectworkLogBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {


    fun bind(
        workLogResponse: WorkLogResponse,
        iProjectListListener: IProjectListListener,
        position: Int,
        context: Context
    ) {
        itemBinding.textIpwlWorkTimeStart.apply {
            text = workLogResponse.travelTime
        }

    }


}
