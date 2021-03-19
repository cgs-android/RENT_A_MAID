package com.task.ui.component.home.adapter

import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.task.R
import com.task.data.dto.drawer.DrawerResponse
import com.task.data.dto.recipes.RecipesItem
import com.task.databinding.DrawerItemBinding
import com.task.databinding.RecipeItemBinding
import com.task.ui.base.listeners.RecyclerItemListener
import com.task.ui.component.home.listener.IDrawerItemListener
import java.text.FieldPosition


class DrawerViewHolder(private val itemBinding: DrawerItemBinding) :
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

