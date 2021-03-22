package com.task.ui.component.home.fragment.projectlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.task.data.dto.projectlist.ProjectListResponse
import com.task.databinding.FragmentProjectlistBinding
import com.task.ui.base.BaseFragment
import com.task.ui.component.home.HomeActivity
import com.task.ui.component.home.fragment.projectlist.adapter.ProjectListAdapter
import com.task.utils.EnumIntUtils
import com.task.utils.SingleEvent
import com.task.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectListFragment : BaseFragment(), View.OnClickListener {


    private lateinit var projectListAdapter: ProjectListAdapter
    lateinit var homeActivity: HomeActivity

    private lateinit var projectListViewModel: ProjectListViewModel

    private lateinit var binding: FragmentProjectlistBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        projectListViewModel =
            ViewModelProviders.of(this).get(ProjectListViewModel::class.java)
        initRecyclerView()
        bindDrawerData(projectListViewModel.loadStaticProjectListData())
        observeViewModel()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProjectlistBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(activity)
        binding.fplProjectListRecyclerView.layoutManager = layoutManager
        binding.fplProjectListRecyclerView.setHasFixedSize(true)
    }

    private fun bindDrawerData(projectListResponse: List<ProjectListResponse>) {
        if (!(projectListResponse.isNullOrEmpty())) {
            projectListAdapter =
                context?.let { ProjectListAdapter(projectListViewModel, projectListResponse, it) }!!
            binding.fplProjectListRecyclerView.adapter = projectListAdapter
        }
    }


    override fun observeViewModel() {
        observeToast(projectListViewModel.showToast)
        observerProjectClickEvent(projectListViewModel.openProjectDetails)
    }

    override fun initOnClickListeners() {

    }

    override fun initAppHeader() {

    }

    override fun init() {
        homeActivity = activity as HomeActivity
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun observerProjectClickEvent(event: LiveData<Int>) {
        activity?.let { it ->
            event.observe(it, Observer {
                homeActivity.changeFragment(EnumIntUtils.ZERO.code, null)
            })
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }
}