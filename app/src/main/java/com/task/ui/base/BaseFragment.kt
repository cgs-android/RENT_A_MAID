package com.task.ui.base


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.task.ui.component.login.LoginActivity
import com.task.utils.GpsUtils


abstract class BaseFragment : Fragment(), GpsUtils.onGpsListener {


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

    override fun onResume() {
        super.onResume()
        turnGPSOn()
    }

    private fun turnGPSOn() {
        GpsUtils(requireActivity()).turnGPSOn(this)
    }

    fun sessionExpiredLoginRedirection() {
        requireActivity().finishAffinity()
        val loginIntent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(loginIntent)
    }

}