package com.task.ui.component.home.fragment.projectlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.task.BUNDLE_PROJECT_DETAILS
import com.task.R
import com.task.RECIPE_ITEM_KEY
import com.task.data.Resource
import com.task.data.dto.projectlist.ProjectListDataResponse
import com.task.data.dto.projectlist.ProjectListsResponse
import com.task.databinding.FragmentProjectlistBinding
import com.task.ui.base.BaseFragment
import com.task.ui.component.details.DetailsActivity
import com.task.ui.component.home.HomeActivity
import com.task.ui.component.home.fragment.projectlist.adapter.ProjectListAdapter
import com.task.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectListFragment : BaseFragment(), View.OnClickListener,
    HomeActivity.OnBackPressedListner {


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
        observeViewModel()
        apiCallBacks(0)
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

    private fun bindDrawerData(projectListsResponse: ProjectListsResponse) {
        if (!(projectListsResponse.data.isNullOrEmpty())) {
            projectListAdapter =
                context?.let {
                    ProjectListAdapter(
                        projectListViewModel,
                        projectListsResponse,
                        it
                    )
                }!!
            binding.fplProjectListRecyclerView.adapter = projectListAdapter
        }
    }


    override fun observeViewModel() {
        observeToast(projectListViewModel.showToast)
        observe(projectListViewModel.projectDetails, ::handleProjectDetailResult)
        observeEvent(projectListViewModel.openProjectDetails, ::observerProjectClickEvent)
    }


    private fun handleProjectDetailResult(status: Resource<ProjectListsResponse>) {
        when (status) {
            is Resource.Loading -> binding.fplProgressBar.toVisible()
            is Resource.Success -> status.data?.let {
                binding.fplProgressBar.toGone()
                bindDrawerData(it)
            }
            is Resource.DataError -> {
                binding.fplProgressBar.toGone()
                status.errorCode?.let {
                    projectListViewModel.showToastMessage(it)
                }
            }
            is Resource.Failure -> status.data?.let {
                binding.fplProgressBar.toGone()
                projectListViewModel.showFailureToastMessage(it.message)
            }
        }
    }

    override fun initOnClickListeners() {

    }


    override fun initAppHeader() {

    }

    override fun init() {
        homeActivity = activity as HomeActivity
    }


    override fun apiCallBacks(event: Int) {
        when (event) {
            EnumIntUtils.ZERO.code -> {
                projectListViewModel.getAllProjectList()
            }
        }

    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun observerProjectClickEvent(event: SingleEvent<ProjectListDataResponse>) {
        event.getContentIfNotHandled()?.let {
            val args = Bundle()
            val jsonString = GsonBuilder().create().toJson(it)
            args.putString(BUNDLE_PROJECT_DETAILS, jsonString)
            homeActivity.changeFragment(EnumIntUtils.ZERO.code, args)
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }

    override fun onBackPressed(): Boolean {
        return true
    }
}