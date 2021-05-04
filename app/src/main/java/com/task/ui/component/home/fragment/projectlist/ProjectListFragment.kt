package com.task.ui.component.home.fragment.projectlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.task.BUNDLE_PROJECT_DETAILS
import com.task.BUNDLE_PROJECT_STATUS
import com.task.R
import com.task.TOKEN_IS_INVALID
import com.task.data.Resource
import com.task.data.dto.project.projectlist.ProjectListDataResponse
import com.task.data.dto.project.projectlist.ProjectListsResponse
import com.task.databinding.FragmentProjectlistBinding
import com.task.ui.base.BaseFragment
import com.task.ui.component.home.HomeActivity
import com.task.ui.component.home.fragment.projectlist.adapter.ProjectListAdapter
import com.task.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_projectlist.*
import kotlinx.android.synthetic.main.item_header.*

@AndroidEntryPoint
class ProjectListFragment : BaseFragment(), View.OnClickListener,
    HomeActivity.OnBackPressedListner {


    private lateinit var projectListAdapter: ProjectListAdapter
    lateinit var homeActivity: HomeActivity

    private lateinit var projectListViewModel: ProjectListViewModel

    private lateinit var binding: FragmentProjectlistBinding

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

    override fun gpsStatus(isGPSEnable: Boolean) {

    }

    override fun observeViewModel() {
        observeToast(projectListViewModel.showToast)
        observe(projectListViewModel.projectDetails, ::handleProjectDetailResult)
        observeEvent(projectListViewModel.openProjectDetails, ::observerProjectClickEvent)
    }

    override fun initOnClickListeners() {
        fplHeaderLogoutImageView.setOnClickListener(this)
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


    override fun onClick(v: View?) {
        when (v) {
            fplHeaderLogoutImageView -> {
                homeActivity.navigateToLoginScreen()
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return true
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
                it.message.let { it1 ->
                    when (it1) {
                        TOKEN_IS_INVALID -> {
                            sessionExpiredLoginRedirection()
                        }
                    }
                }
            }
        }
    }


    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun observerProjectClickEvent(event: SingleEvent<ProjectListDataResponse>) {
        var userRoleBool: Boolean = false
        event.getContentIfNotHandled()?.let {
            it.project_details.id.let {
                projectListViewModel.localPrefStoreProjectId(it)
            }
            it.team_members.let {
                for (team in it) {
                    when (team.Roles.rolename) {
                        requireActivity().resources.getString(
                            R.string.team_leader
                        ) -> {
                            if (projectListViewModel.getLoginUserId()
                                    .equals(team.Users.id)
                            ) {
                                userRoleBool = true
                            }
                        }
                    }
                }
            }
            when (userRoleBool) {
                true -> {
                    projectListViewModel.putLocalUserRole(true)
                    val args = Bundle()
                    homeActivity.changeFragment(EnumIntUtils.ZERO.code, args)
                }
                else -> {
                    projectListViewModel.putLocalUserRole(false)
                    val args = Bundle()
                    when (it.project_details.projectStatusColor) {
                        1 -> {
                            homeActivity.changeFragment(EnumIntUtils.TWO.code, args)
                        }
                        -1 -> {
                            homeActivity.changeFragment(EnumIntUtils.ZERO.code, args)
                        }
                        0 -> {
                            homeActivity.changeFragment(EnumIntUtils.ZERO.code, args)
                        }
                    }
                }
            }
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        super.onNetworkConnectionChanged(isConnected)
        enableDisableLayout(isConnected, fplRootConstraintLayout)
        showShackBarMessage(
            isConnected,
            fplRootConstraintLayout,
            requireActivity().getString(R.string.msg_offline)
        )
    }


}

