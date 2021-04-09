package com.task.ui.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment


abstract class BaseFragment : Fragment() {


    abstract fun observeViewModel()
    abstract fun initOnClickListeners()
    abstract fun initAppHeader()
    abstract fun init()
    abstract fun apiCallBacks(event: Int)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initOnClickListeners()
        initAppHeader()
    }


}