package com.task.ui.component.home.fragment.projectworkdetail.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.task.data.dto.project.getworkdetails.GetWorkDetailListData

import com.task.data.dto.worktime.WorkLogResponse
import com.task.databinding.ItemProjectworkLogBinding
import com.task.ui.component.home.fragment.projectlist.listener.IProjectListListener
import com.task.utils.DateUtils


class ProjectWorkLogViewHolder(private val itemBinding: ItemProjectworkLogBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {


    fun bind(
        getWorkDetailListData: GetWorkDetailListData,
        iProjectListListener: IProjectListListener,
        position: Int,
        context: Context
    ) {
        itemBinding.textIpwlWorkTimeStart.apply {
            getWorkDetailListData.started_at.let {
                text = DateUtils.returnCurrentTime(it)
            }
        }

        itemBinding.textIpwlWorkTimeEnd.apply {
            getWorkDetailListData.ended_at.let {
                text = DateUtils.returnCurrentTime(it)
            }
        }

    }


}
