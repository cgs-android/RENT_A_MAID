package com.task.ui.component.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.task.data.dto.drawer.DrawerResponse
import com.task.databinding.ItemDrawerBinding
import com.task.ui.component.home.HomeViewModel
import com.task.ui.component.home.listener.IDrawerItemListener


class DrawerAdapter(
    private val homeViewModel: HomeViewModel,
    private val drawerResponse: List<DrawerResponse>
) : RecyclerView.Adapter<DrawerViewHolder>() {

    private val onItemClickListener: IDrawerItemListener = object : IDrawerItemListener {
        override fun onItemSelected(position: Int) {
            homeViewModel.onDrawerItemOnTap(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawerViewHolder {
        val itemBinding =
            ItemDrawerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrawerViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: DrawerViewHolder, position: Int) {
        holder.bind(drawerResponse[position], onItemClickListener, position)
    }

    override fun getItemCount(): Int {
        return drawerResponse.size
    }
}

