package com.task.ui.component.home.adapter

import androidx.recyclerview.widget.RecyclerView
import com.task.data.dto.drawer.DrawerResponse
import com.task.databinding.ItemDrawerBinding
import com.task.ui.component.home.listener.IDrawerItemListener


class DrawerViewHolder(private val itemBinding: ItemDrawerBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(
        drawerResponse: DrawerResponse,
        iDrawerItemListener: IDrawerItemListener,
        position: Int
    ) {
        itemBinding.diTitleTextView.text = drawerResponse.drawerTitle
        itemBinding.diWholeConstraintLayout.setOnClickListener {
            iDrawerItemListener.onItemSelected(position)
        }
    }
}

