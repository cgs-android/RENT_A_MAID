package com.task.ui.component.home.fragment.projecttraveldetail

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.task.BUNDLE_PROJECT_DETAILS
import com.task.BUNDLE_PROJECT_STATUS
import com.task.R
import com.task.data.Resource
import com.task.data.dto.project.projecttraveldetails.ProjectTravelDetailsResponse
import com.task.data.dto.project.projectlist.ProjectListDataResponse
import com.task.data.dto.project.travelend.TravelEndResponse
import com.task.data.dto.project.travelstart.TravelStartResponse
import com.task.databinding.FragmentProjectTravelDetailsBinding
import com.task.ui.base.BaseFragment
import com.task.ui.component.home.HomeActivity
import com.task.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.item_header.*
import javax.inject.Inject


@AndroidEntryPoint
class ProjectTravelDetailsFragment : BaseFragment(), View.OnClickListener,
    HomeActivity.OnBackPressedListner {

    @Inject
    lateinit var dialogHelper: DialogHelper

    private var mPauseAndResume: Boolean = false
    private lateinit var homeActivity: HomeActivity

    private var mTeamMember: String? = ""
    private var mTeamLead: String? = ""

    private var mtimerStatus: Int = 0

    private var projectTravelDetailsResponse: ProjectTravelDetailsResponse? = null

    private lateinit var projectTravelDetailsViewModel: ProjectTravelDetailsViewModel

    private lateinit var binding: FragmentProjectTravelDetailsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectTravelDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun observeViewModel() {
        observeToast(projectTravelDetailsViewModel.showToast)
        observe(projectTravelDetailsViewModel.projectTravelDetails, ::handleProjectDetailResult)
        observeEvent(projectTravelDetailsViewModel.travelStart, ::handleTravelStartResult)
        observeEvent(projectTravelDetailsViewModel.travelEnd, ::handleTravelEndResult)
    }

    override fun initOnClickListeners() {
        binding.dfPauseTextView.setOnClickListener(this)
        binding.dfTimerTextView.setOnClickListener(this)
        hiMenuNavigationImageView.setOnClickListener(this)
    }

    override fun initAppHeader() {

    }

    override fun init() {
        homeActivity = activity as HomeActivity
    }

    override fun apiCallBacks(event: Int) {
        when (event) {
            EnumIntUtils.ZERO.code -> {
                projectTravelDetailsViewModel.getProjectDetails()
            }

            EnumIntUtils.ONE.code -> {
                projectTravelDetailsViewModel.postTravelStartTime(
                    "2021-04-01 00:03:00",
                    "Chennai"
                )
            }
            EnumIntUtils.TWO.code -> {
                projectTravelDetailsViewModel.postTravelEndTime(
                    "2021-04-01 00:03:00",
                    "Chennai"

                )
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        projectTravelDetailsViewModel =
            ViewModelProviders.of(this).get(ProjectTravelDetailsViewModel::class.java)
        observeViewModel()
        apiCallBacks(EnumIntUtils.ZERO.code)
    }


    override fun onClick(v: View?) {
        when (v) {
            binding.dfPauseTextView -> {
                onPauseAndResume(mPauseAndResume)
            }
            hiMenuNavigationImageView -> {
                homeActivity.drawerOpenAndClose()
            }
            binding.dfTimerTextView -> {
                onStartStopTimer(mtimerStatus)
            }
        }
    }


    private fun setTimerText(timeText: Int) {
        binding.dfTimerTextView.text =
            requireActivity().getString(timeText)
    }

    private fun driveTimeCloseLogDialog() {
        dialogHelper.showAlertDialog(
            object : DialogHelper.DialogPickListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onPositiveClicked() {
                    apiCallBacks(EnumIntUtils.TWO.code)
                }

                override fun onNegativeClicked() {

                }
            },
            requireActivity().resources.getString(R.string.title_are_you_there),
            requireActivity().resources.getString(R.string.title_msg_travel_time),
            requireActivity().resources.getString(R.string.action_confirm),
            requireActivity().resources.getString(R.string.action_cancel), false
        )
    }

    private fun onSuccessUpdateTravelTimer() {
        visibleTravelTimeEnd()
        binding.textddfTravelTimeEnd.apply {
            text = DateUtils.returnCurrentTime()
            mtimerStatus = 0
            setTextColor(
                requireActivity().resources.getColor(
                    R.color.colorAliceBlue
                )
            )
        }
        binding.dfTimerConstraintLayout.visibility = View.GONE
        binding.textddfTravelTimeStart.setTextColor(
            requireActivity().resources.getColor(
                R.color.colorAliceBlue
            )
        )
    }

    private fun navigateToWorkDetails() {
        projectTravelDetailsResponse.let {
            val args = Bundle()
            getBundleProjectListDataResponse().project_details.projectStatusColor?.let { it1 ->
                args.putInt(
                    BUNDLE_PROJECT_STATUS,
                    it1
                )
            }
            homeActivity.changeFragment(EnumIntUtils.TWO.code, args)
        }

    }


    override fun onBackPressed(): Boolean {
        mtimerStatus = 0
        val arg = Bundle()
        homeActivity.changeFragment(EnumIntUtils.ONE.code, arg)
        return true
    }


    private fun onPauseAndResume(status: Boolean) {
        status.let {
            when (it) {
                true -> {
                    binding.dfPauseTextView.apply {
                        mPauseAndResume = false
                        text = this.resources.getString(R.string.tap_to_pause)
                        setTextColor(this.resources.getColor(R.color.colorAliceBlue))
                    }
                }
                false -> {
                    binding.dfPauseTextView.apply {
                        mPauseAndResume = true
                        text = this.resources.getString(R.string.tap_to_resume)
                        setTextColor(this.resources.getColor(R.color.colorRed))
                    }
                }
            }
        }

    }

    private fun onStartStopTimer(timerStatus: Int) {
        timerStatus.let {
            when (it) {
                0 -> {
                    binding.textddfTravelTimeStart.apply {
                        mtimerStatus = 1
                        text = DateUtils.returnCurrentTime()
                    }
                    visibleTravelTimeStart()
                    setTimerText(R.string.action_stop)
                    apiCallBacks(EnumIntUtils.ONE.code)
                }
                1 -> {
                    setTimerText(R.string.action_stop)
                    driveTimeCloseLogDialog()
                }
            }
        }
    }

    private fun handleProjectDetailResult(status: Resource<ProjectTravelDetailsResponse>) {
        status.let {
            when (it) {
                is Resource.Loading -> binding.ddfProgressBar.toVisible()
                is Resource.Success -> status.data?.let {
                    binding.ddfProgressBar.toGone()
                    bindProjectDetailsData(it)
                }
                is Resource.DataError -> {
                    binding.ddfProgressBar.toGone()
                    status.errorCode?.let {
                        projectTravelDetailsViewModel.showToastMessage(it)
                    }
                }
                is Resource.Failure -> status.data?.let {
                    binding.ddfProgressBar.toGone()
                    projectTravelDetailsViewModel.showFailureToastMessage(it.message)
                }
            }
        }
    }


    private fun handleTravelStartResult(status: SingleEvent<Resource<TravelStartResponse>>) {
        status.getContentIfNotHandled()?.let {
            when (it) {
                is Resource.Loading -> {
                    binding.ddfProgressBar.toVisible()
                    binding.ddfProgressBar.setBackgroundResource(0)
                }
                is Resource.Success -> it.data.let {
                    binding.ddfProgressBar.toGone()
                    projectTravelDetailsViewModel.storeLocalTravelStartId(it!!.data.id)
                }
                is Resource.DataError -> {
                    binding.ddfProgressBar.toGone()
                    it.errorCode?.let {
                        projectTravelDetailsViewModel.showToastMessage(it)
                    }
                }
                is Resource.Failure -> it.data?.let {
                    binding.ddfProgressBar.toGone()
                    projectTravelDetailsViewModel.showFailureToastMessage(it.message)
                }
            }
        }
    }

    private fun handleTravelEndResult(status: SingleEvent<Resource<TravelEndResponse>>) {
        status.getContentIfNotHandled()?.let {
            when (it) {
                is Resource.Loading -> {
                    binding.ddfProgressBar.toVisible()
                    binding.ddfProgressBar.setBackgroundResource(0)
                }
                is Resource.Success -> it.data.let {
                    binding.ddfProgressBar.toGone()
                    onSuccessUpdateTravelTimer()
                    navigateToWorkDetails()
                }
                is Resource.DataError -> {
                    binding.ddfProgressBar.toGone()
                    it.errorCode?.let {
                        projectTravelDetailsViewModel.showToastMessage(it)
                    }
                }
                is Resource.Failure -> it.data?.let {
                    binding.ddfProgressBar.toGone()
                    projectTravelDetailsViewModel.showFailureToastMessage(it.message)
                }
            }
        }
    }


    private fun projectStatusColor(color: Int) {
        binding.ddfProjectStatusImageView.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                color
            )
        )
    }

    private fun changeStatusColor(colorStatus: Int?) {
        colorStatus?.let {
            when (it) {
                1 -> {
                    projectStatusColor(R.color.colorGreen)
                }
                -1 -> {
                    projectStatusColor(R.color.colorBlue)
                    goneStartButton()
                }
                0 -> {
                    projectStatusColor(R.color.colorOrange)
                    goneStartButton()
                }
            }
        }
    }

    private fun goneStartButton() {
        binding.dfTimerConstraintLayout.visibility = View.GONE
        binding.constraintddfTravelTimeHint.visibility = View.GONE
    }

    private fun bindProjectId(projectId: String) {
        projectId.let {
            binding.pdfProjectIdTextView.apply {
                text = String.format(
                    requireActivity().resources.getString(
                        R.string.project
                    ) + " " + requireActivity().resources.getString(
                        R.string.zeros
                    ) + it
                )
            }

        }
    }

    private fun bindProjectDescriptionDetails(projectTravelDetailsResponse: ProjectTravelDetailsResponse) {
        projectTravelDetailsResponse.let {
            binding.pdfProjectLocationDescriptionTextView.apply {
                text = String.format(
                    it.data.project_location.company_name + "\n" +
                            it.data.project_location.address_line1 + "" +
                            it.data.project_location.address_line2 + "\n" +
                            it.data.project_location.pincode + " " +
                            it.data.project_location.city
                )
            }

            binding.ddfWorkStartTimeTextView.apply {
                text = String.format(
                    it.data.project_details.work_start_time + " " + requireActivity().resources.getString(
                        R.string.hrs
                    )
                )
            }

        }
    }

    private fun bindProjectTeamMembers(projectTravelDetailsResponse: ProjectTravelDetailsResponse) {
        projectTravelDetailsResponse.let {
            mTeamLead = ""
            mTeamMember = ""
            it.data.team_members.let {
                for (team in it) {
                    if (team.Roles.rolename.contains(
                            requireActivity().resources.getString(
                                R.string.team_leader
                            )
                        )
                    ) {
                        mTeamLead =
                            String.format(
                                team.Users.first_name + " " + team.Users.last_name + requireActivity().getString(
                                    R.string.team_lead
                                )
                            )
                    } else {
                        mTeamMember +=
                            String.format(team.Users.first_name + " " + team.Users.last_name + ", ")
                    }
                }
                if (!mTeamMember.isNullOrEmpty()) {
                    mTeamMember = RegexUtils.removeLastChar(mTeamMember)
                }
                if (mTeamLead.isNullOrEmpty()) {
                    binding.pdfProjectDescriptionTextView.text = mTeamMember
                } else {
                    if (mTeamMember.isNullOrBlank()) {
                        binding.pdfProjectDescriptionTextView.text = mTeamLead
                    } else {
                        binding.pdfProjectDescriptionTextView.text =
                            String.format(mTeamLead + "\n" + mTeamMember)
                    }

                }
            }
        }
    }

    private fun bindProjectDetailsData(projectTravelDetailsResponse: ProjectTravelDetailsResponse) {
        this.projectTravelDetailsResponse = projectTravelDetailsResponse
        changeStatusColor(getBundleProjectListDataResponse().project_details.projectStatusColor)
        bindProjectId(getBundleProjectListDataResponse().project_details.id)
        bindProjectDescriptionDetails(projectTravelDetailsResponse)
        bindProjectTeamMembers(projectTravelDetailsResponse)
    }


    private fun getBundleProjectListDataResponse(): ProjectListDataResponse {
        val args = arguments
        val projectListJsonString = args?.getString(BUNDLE_PROJECT_DETAILS)
        return GsonBuilder().create()
            .fromJson(projectListJsonString, ProjectListDataResponse::class.java)
    }


    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun visibleTravelTimeStart() {
        binding.constraintWholeTravelTime.visibility = View.VISIBLE
        binding.textddfTravelTimeStartHint.visibility = View.VISIBLE
        binding.textddfTravelTimeStart.visibility = View.VISIBLE
    }

    private fun visibleTravelTimeEnd() {
        binding.textddfTravelTimeEndHint.visibility = View.VISIBLE
        binding.textddfTravelTimeEnd.visibility = View.VISIBLE
    }


}