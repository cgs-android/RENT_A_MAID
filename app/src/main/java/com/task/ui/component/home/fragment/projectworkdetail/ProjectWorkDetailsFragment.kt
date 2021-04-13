package com.task.ui.component.home.fragment.projectworkdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.task.BUNDLE_PROJECT_DETAILS
import com.task.BUNDLE_PROJECT_STATUS
import com.task.R
import com.task.data.Resource
import com.task.data.dto.project.getworkdetails.GetWorkDetailListData
import com.task.data.dto.project.getworkdetails.GetWorkDetailListsResponse
import com.task.data.dto.project.projecttraveldetails.ProjectTravelDetailsResponse
import com.task.data.dto.project.travelend.TravelEndResponse
import com.task.data.dto.project.travelstart.TravelStartResponse
import com.task.data.dto.worktime.WorkLogResponse
import com.task.data.dto.worktime.workend.WorkEndResponse
import com.task.data.dto.worktime.workstart.WorkStartResponse
import com.task.databinding.FragmentProjectWorkDetailsBinding
import com.task.ui.base.BaseFragment
import com.task.ui.component.home.HomeActivity
import com.task.ui.component.home.fragment.projectlist.adapter.ProjectListAdapter
import com.task.ui.component.home.fragment.projecttraveldetail.ProjectTravelDetailsViewModel
import com.task.ui.component.home.fragment.projectworkdetail.adapter.ProjectWorkLogAdapter
import com.task.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.item_header.*
import javax.inject.Inject


@AndroidEntryPoint
class ProjectWorkDetailsFragment : BaseFragment(), View.OnClickListener,
    HomeActivity.OnBackPressedListner {


    @Inject
    lateinit var dialogHelper: DialogHelper

    private var mPauseAndResume: Boolean = false
    private lateinit var homeActivity: HomeActivity


    private var mTeamMember: String? = ""
    private var mTeamLead: String? = ""

    private var mtimerStatus: Int = 0
    private var mDrawerFlag: Boolean = true

    private lateinit var projectWorkLogAdapter: ProjectWorkLogAdapter

    private lateinit var projectWorkDetailsViewModel: ProjectWorkDetailsViewModel

    private lateinit var binding: FragmentProjectWorkDetailsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectWorkDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun observeViewModel() {
        observeToast(projectWorkDetailsViewModel.showToast)
        observe(projectWorkDetailsViewModel.projectWorkDetails, ::handleProjectDetailResult)
        observe(projectWorkDetailsViewModel.getWorkDetails, ::handleGetWorkDetailResult)
        observeEvent(projectWorkDetailsViewModel.workStart, ::handleWorkStartResult)
        observeEvent(projectWorkDetailsViewModel.workEnd, ::handleWorkEndResult)
    }

    override fun initOnClickListeners() {
        binding.fpwdPauseTextView.setOnClickListener(this)
        binding.imagePwdUpDownArrow.setOnClickListener(this)
        binding.ddfWholeConstraintLayout.setOnClickListener(this)
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
                projectWorkDetailsViewModel.getProjectDetails()
            }
            EnumIntUtils.ONE.code -> {
                projectWorkDetailsViewModel.postWorkStartTime(DateUtils.getCurrentDateTime())
            }
            EnumIntUtils.TWO.code -> {
                projectWorkDetailsViewModel.getWorkDetails()
            }
        }
    }

    fun apiEndWorkTime(event: Int, comment: String = "", status: String = "") {
        projectWorkDetailsViewModel.postWorkEndTime(
            DateUtils.getCurrentDateTime(),
            comment,
            status,
            event
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        projectWorkDetailsViewModel =
            ViewModelProviders.of(this).get(ProjectWorkDetailsViewModel::class.java)
        observeViewModel()
        apiCallBacks(EnumIntUtils.ZERO.code)
    }


    private fun onPauseAndResume(status: Boolean) {
        status.let {
            when (it) {
                true -> {
                    binding.fpwdPauseTextView.apply {
                        binding.dfTimerTextView.visibility = View.VISIBLE
                        mPauseAndResume = false
                        text = this.resources.getString(R.string.tap_to_pause)
                        setTextColor(this.resources.getColor(R.color.colorAliceBlue))
                        apiCallBacks(EnumIntUtils.ONE.code)
                    }
                }
                false -> {
                    binding.fpwdPauseTextView.apply {
                        binding.dfTimerTextView.visibility = View.GONE
                        mPauseAndResume = true
                        text = this.resources.getString(R.string.tap_to_resume)
                        setTextColor(this.resources.getColor(R.color.colorRed))
                        apiEndWorkTime(EnumIntUtils.ZERO.code)
                    }
                }
            }
        }

    }

    private fun setTimerText(timeText: Int) {
        binding.dfTimerTextView.text =
            requireActivity().getString(timeText)
    }

    private fun setDrawerImage(imageDrawable: Int) {
        binding.imagePwdUpDownArrow.apply {
            setImageDrawable(
                ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    imageDrawable, requireActivity().theme
                )
            )
        }
    }

    private fun onStartStopTimer(timerStatus: Int) {
        timerStatus.let {
            when (it) {
                0 -> {
                    setTimerText(R.string.action_stop)
                    binding.fpwdPauseTextView.visibility = View.VISIBLE
                    binding.constraintFpwdWholeTimeStamp.visibility = View.VISIBLE
                    mtimerStatus = 1
                    apiCallBacks(EnumIntUtils.ONE.code)
                }
                1 -> {
                    setTimerText(R.string.action_stop)
                    driveTimeCloseLogDialog()
                }
            }
        }
    }


    private fun driveTimeCloseLogDialog() {
        dialogHelper.showAlertDialog(
            object : DialogHelper.DialogPickListener {


                override fun onPositiveClicked(message: String) {
                    apiEndWorkTime(EnumIntUtils.ONE.code, message)
                }

                override fun onNegativeClicked() {

                }
            },
            requireActivity().resources.getString(R.string.title_end_of_work),
            requireActivity().resources.getString(R.string.title_msg_travel_time),
            requireActivity().resources.getString(R.string.action_confirm),
            requireActivity().resources.getString(R.string.action_cancel), true
        )
    }


    private fun openCloseArrowDrawer(drawerFlag: Boolean) {
        drawerFlag.let {
            when (it) {
                true -> {
                    binding.pliProjectDetailsConstraintLayout.visibility = View.VISIBLE
                    binding.imagePwdUpDownArrow.apply {
                        setDrawerImage(R.drawable.ic_down_arrow)
                        mDrawerFlag = false
                    }
                }

                false -> {
                    binding.pliProjectDetailsConstraintLayout.visibility = View.GONE
                    binding.imagePwdUpDownArrow.apply {
                        setDrawerImage(R.drawable.ic_right_arrow)
                        mDrawerFlag = true
                    }
                }
            }
        }
    }


    override fun onClick(v: View?) {
        when (v) {
            binding.fpwdPauseTextView -> {
                onPauseAndResume(mPauseAndResume)
            }
            hiMenuNavigationImageView -> {
                homeActivity.drawerOpenAndClose()
            }
            binding.dfTimerTextView -> {
                onStartStopTimer(mtimerStatus)
            }
            binding.ddfWholeConstraintLayout -> {
                openCloseArrowDrawer(mDrawerFlag)
            }
        }
    }


    override fun onBackPressed(): Boolean {
        binding.fpwdPauseTextView.visibility = View.GONE
        mtimerStatus = 0
        projectWorkDetailsViewModel.clearList()
        val arg = Bundle()
        homeActivity.changeFragment(EnumIntUtils.ZERO.code, arg)
        return true
    }


    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(context, 1)
        binding.recyclerFpwdTravelTimeList.layoutManager = layoutManager
        binding.recyclerFpwdTravelTimeList.setHasFixedSize(true)

    }


    private fun bindDrawerData(getWorkDetailListData: List<GetWorkDetailListData>) {
        if (!(getWorkDetailListData.isNullOrEmpty())) {
            binding.constraintFpwdWholeTimeStamp.visibility = View.VISIBLE
            projectWorkLogAdapter = ProjectWorkLogAdapter(requireContext(), getWorkDetailListData)
            binding.recyclerFpwdTravelTimeList.adapter = projectWorkLogAdapter
        }
    }


    private fun handleProjectDetailResult(status: Resource<ProjectTravelDetailsResponse>) {
        when (status) {
            is Resource.Loading -> binding.ddfProgressBar.toVisible()
            is Resource.Success -> status.data?.let {
                binding.ddfProgressBar.toGone()
                bindProjectDetailsData(it)
            }
            is Resource.DataError -> {
                binding.ddfProgressBar.toGone()
                status.errorCode?.let {
                    projectWorkDetailsViewModel.showToastMessage(it)
                }
            }
            is Resource.Failure -> status.data?.let {
                binding.ddfProgressBar.toGone()
                projectWorkDetailsViewModel.showFailureToastMessage(it.message)
            }
        }
    }

    private fun handleGetWorkDetailResult(status: Resource<GetWorkDetailListsResponse>) {
        when (status) {
            is Resource.Loading -> binding.ddfProgressBar.toVisible()
            is Resource.Success -> status.data?.let {
                binding.ddfProgressBar.toGone()
                bindGetAllWorkDetails(it)
            }
            is Resource.DataError -> {
                binding.ddfProgressBar.toGone()
                status.errorCode?.let {
                    projectWorkDetailsViewModel.showToastMessage(it)
                }
            }
            is Resource.Failure -> status.data?.let {
                binding.ddfProgressBar.toGone()
            }
        }
    }


    private fun bindGetAllWorkDetails(getWorkDetailListsResponse: GetWorkDetailListsResponse) {
        getWorkDetailListsResponse.let {
            if (!(getWorkDetailListsResponse.data.isNullOrEmpty())) {
                bindDrawerData(it.data)
                it.data.let { it1 ->
                    val size: Int = it.data.size
                    if (size > 0) {
                        val startedAt: String = it1.get(size - 1).started_at
                        val endedAt: String = it1.get(size - 1).ended_at
                        val workStartId: Int = it1.get(size - 1).id

                        if (startedAt.isNotEmpty() and endedAt.isNotEmpty()) {
                            startTimerButtonHint()
                        } else if (endedAt.isEmpty()) {
                            projectWorkDetailsViewModel.storeLocalTravelStartId(workStartId)
                            stopTimerButtonHint()
                        }
                    }


                }
            }
        }

    }

    private fun startTimerButtonHint() {
        mtimerStatus = 0
        setTimerText(R.string.action_start)
    }

    private fun stopTimerButtonHint() {
        mtimerStatus = 1
        binding.fpwdPauseTextView.visibility = View.VISIBLE
        setTimerText(R.string.action_stop)
    }

    private fun handleWorkStartResult(status: SingleEvent<Resource<WorkStartResponse>>) {
        status.getContentIfNotHandled()?.let {
            when (it) {
                is Resource.Loading -> {
                    binding.ddfProgressBar.toVisible()
                    binding.ddfProgressBar.setBackgroundResource(0)
                }
                is Resource.Success -> it.data.let {
                    binding.ddfProgressBar.toGone()
                    projectWorkDetailsViewModel.storeLocalTravelStartId(it!!.data.id)
                    apiCallBacks(EnumIntUtils.TWO.code)
                }
                is Resource.DataError -> {
                    binding.ddfProgressBar.toGone()
                    it.errorCode?.let {
                        projectWorkDetailsViewModel.showToastMessage(it)
                    }
                }
                is Resource.Failure -> it.data?.let {
                    binding.ddfProgressBar.toGone()
                    projectWorkDetailsViewModel.showFailureToastMessage(it.message)
                }
                else -> {
                }
            }
        }
    }

    private fun handleWorkEndResult(status: SingleEvent<Resource<WorkEndResponse>>) {
        status.getContentIfNotHandled()?.let {
            when (it) {
                is Resource.Loading -> {
                    binding.ddfProgressBar.toVisible()
                    binding.ddfProgressBar.setBackgroundResource(0)
                }
                is Resource.Success -> it.data.let {
                    binding.ddfProgressBar.toGone()
                    apiCallBacks(EnumIntUtils.TWO.code)
                }

                is Resource.SuccessHandling -> it.data.let {
                    binding.ddfProgressBar.toGone()
                    onSuccessUpdateWorkTimer()
                }
                is Resource.DataError -> {
                    binding.ddfProgressBar.toGone()
                    it.errorCode?.let {
                        projectWorkDetailsViewModel.showToastMessage(it)
                    }
                }
                is Resource.Failure -> it.data?.let {
                    binding.ddfProgressBar.toGone()
                    projectWorkDetailsViewModel.showFailureToastMessage(it.message)
                }
            }
        }
    }

    private fun onSuccessUpdateWorkTimer() {
        binding.dfTimerConstraintLayout.visibility = View.GONE
        mtimerStatus = 0
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
        bindProjectId(projectTravelDetailsResponse.data.project_details.id)
        bindProjectStatusColor(projectTravelDetailsResponse)
        bindProjectDescriptionDetails(projectTravelDetailsResponse)
        bindProjectTeamMembers(projectTravelDetailsResponse)
        apiCallBacks(EnumIntUtils.TWO.code)
    }

    private fun bindProjectStatusColor(projectTravelDetailsResponse: ProjectTravelDetailsResponse) {
        val serverStartDate =
            DateUtils.formatDate(projectTravelDetailsResponse.data.project_details.start_date)
        serverStartDate.let {
            changeStatusColor(
                DateUtils.isTodayOrTomorrowProject(
                    DateUtils.returnCurrentDate(),
                    it
                )
            )
        }

    }


    private fun changeStatusColor(colorStatus: Int?) {
        colorStatus?.let {
            when (it) {
                1 -> {
                    projectStatusColor(R.color.colorGreen)
                }
                -1 -> {
                    projectStatusColor(R.color.colorBlue)
                    //goneStartButton()
                }
                0 -> {
                    projectStatusColor(R.color.colorOrange)
                    //goneStartButton()
                }
            }
        }
    }


    private fun goneStartButton() {
        binding.textddfTravelTimeHint.visibility = View.GONE
        binding.dfTimerConstraintLayout.visibility = View.GONE
    }


    private fun projectStatusColor(color: Int) {
        binding.ddfProjectStatusImageView.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                color
            )
        )
    }


    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


}