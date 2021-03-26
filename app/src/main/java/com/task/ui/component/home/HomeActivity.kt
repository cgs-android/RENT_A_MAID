package com.task.ui.component.home

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.task.R
import com.task.data.dto.drawer.DrawerResponse
import com.task.databinding.ActivityHomeBinding
import com.task.ui.base.BaseActivity
import com.task.ui.component.home.adapter.DrawerAdapter
import com.task.ui.component.home.fragment.projectdetail.ProjectDetailsFragment
import com.task.ui.component.home.fragment.projectlist.ProjectListFragment
import com.task.utils.EnumIntUtils
import com.task.utils.SingleEvent
import com.task.utils.setupSnackbar
import com.task.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private lateinit var drawerAdapter: DrawerAdapter
    private val projectDetailsFragment =
        ProjectDetailsFragment()

    private val projectListFragment = ProjectListFragment()

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityHomeBinding

    var args = Bundle()

    override fun observeViewModel() {
        observeSnackBarMessages(homeViewModel.showSnackBar)
        observeToast(homeViewModel.showToast)
        observerDrawerClickEvent(homeViewModel.openDrawerDetails)
    }


    override fun initViewBinding() {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRecyclerView()
        bindDrawerData(homeViewModel.loadDrawerData(this))
        changeFragment(EnumIntUtils.ONE.code, args)
    }


    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.ahDrawerRecyclerView.layoutManager = layoutManager
        binding.ahDrawerRecyclerView.setHasFixedSize(true)

    }


    private fun bindDrawerData(drawerResponse: List<DrawerResponse>) {
        if (!(drawerResponse.isNullOrEmpty())) {
            drawerAdapter = DrawerAdapter(homeViewModel, drawerResponse)
            binding.ahDrawerRecyclerView.adapter = drawerAdapter
        }
        binding.ahDashboardDrawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })
    }


    private fun observerDrawerClickEvent(event: LiveData<Int>) {
        event.observe(this, Observer {
            it.let {
                when (it) {
                    EnumIntUtils.ZERO.code -> {
                        closeDrawer()
                    }
                    EnumIntUtils.ONE.code -> {
                        closeDrawer()
                    }
                    EnumIntUtils.TWO.code -> {
                        closeDrawer()
                    }

                }
            }
        })
    }

    fun changeFragment(position: Int, bundle: Bundle?) {
        when (position) {
            EnumIntUtils.ZERO.code -> {
                loadFragment(projectDetailsFragment, bundle)
            }
            EnumIntUtils.ONE.code -> {
                loadFragment(projectListFragment, bundle)
            }
        }
    }

    private fun loadFragment(fragment: Fragment, bundle: Bundle?) {
        val transaction = supportFragmentManager.beginTransaction()
        fragment.arguments = bundle
        transaction.replace(R.id.chsHomeScreenFramelayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun drawerOpenAndClose() {
        if (binding.ahDashboardDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            binding.ahDashboardDrawerLayout.closeDrawer(Gravity.LEFT)
        } else {
            binding.ahDashboardDrawerLayout.openDrawer(Gravity.LEFT)
        }
    }


    private fun closeDrawer() {
        binding.ahDashboardDrawerLayout.closeDrawer(Gravity.LEFT)
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    override fun onBackPressed() {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.chsHomeScreenFramelayout)
        if (fragment !is OnBackPressedListner || !(fragment as OnBackPressedListner?)!!.onBackPressed()) {
            super.onBackPressed()
        }
    }


    interface OnBackPressedListner {
        fun onBackPressed(): Boolean
    }
}