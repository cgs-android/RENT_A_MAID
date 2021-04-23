package com.task.ui.component.home.fragment.projecttraveldetail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.task.R
import com.task.TOKEN_IS_INVALID
import com.task.data.Resource
import com.task.data.dto.project.gettraveldetails.GetTravelResponse
import com.task.data.dto.project.projecttraveldetails.ProjectTravelDetailsResponse
import com.task.data.dto.project.travelend.TravelEndResponse
import com.task.data.dto.project.travelstart.TravelStartResponse
import com.task.data.local.LocalData
import com.task.databinding.FragmentProjectTravelDetailsBinding
import com.task.ui.base.BaseFragment
import com.task.ui.component.home.HomeActivity
import com.task.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_project_travel_details.*
import kotlinx.android.synthetic.main.fragment_projectlist.*
import kotlinx.android.synthetic.main.item_header.*
import javax.inject.Inject


@AndroidEntryPoint
class ProjectTravelDetailsFragment : BaseFragment(), View.OnClickListener,
    HomeActivity.OnBackPressedListner {

    @Inject
    lateinit var dialogHelper: DialogHelper


    @Inject
    lateinit var localRepository: LocalData


    private var mPauseAndResume: Boolean = false
    private lateinit var homeActivity: HomeActivity

    private var mTeamMember: String? = ""
    private var mTeamLead: String? = ""

    private var mtimerStatus: Int = 0

    private var projectTravelDetailsResponse: ProjectTravelDetailsResponse? = null

    private lateinit var projectTravelDetailsViewModel: ProjectTravelDetailsViewModel

    private lateinit var binding: FragmentProjectTravelDetailsBinding

    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 1000


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectTravelDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun gpsStatus(isGPSEnable: Boolean) {

    }

    override fun observeViewModel() {
        observeToast(projectTravelDetailsViewModel.showToast)
        observe(projectTravelDetailsViewModel.projectTravelDetails, ::handleProjectDetailResult)
        observe(projectTravelDetailsViewModel.getTravelDetails, ::handleGetProjectDetailResult)
        observeEvent(projectTravelDetailsViewModel.travelStart, ::handleTravelStartResult)
        observeEvent(projectTravelDetailsViewModel.travelEnd, ::handleTravelEndResult)
    }

    override fun initOnClickListeners() {
        binding.dfPauseTextView.setOnClickListener(this)
        binding.dfTimerTextView.setOnClickListener(this)
        binding.fptdNextButton.setOnClickListener(this)
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
                    DateUtils.getCurrentDateTime(),
                    "Chennai"
                )
            }
            EnumIntUtils.TWO.code -> {
                projectTravelDetailsViewModel.postTravelEndTime(
                    DateUtils.getCurrentDateTime(),
                    "Chennai"

                )
            }
            EnumIntUtils.THREE.code -> {
                projectTravelDetailsViewModel.getTravelDetails()
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
                when (isCheckGpsStatus()) {
                    true -> {
                        showShackBarMessage(
                            true,
                            fptdRootScrollView,
                            requireActivity().getString(R.string.msg_on_gps)
                        )
                        onStartStopTimer(mtimerStatus)
                    }
                    false -> {
                        showShackBarMessage(
                            false,
                            fptdRootScrollView,
                            requireActivity().getString(R.string.msg_on_gps)
                        )
                    }
                }
            }
            binding.fptdNextButton -> {
                navigateToWorkDetails()
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

                override fun onPositiveClicked(message: String) {
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
            homeActivity.changeFragment(EnumIntUtils.TWO.code, args)
        }

    }


    override fun onBackPressed(): Boolean {
        mtimerStatus = 0
        goneTravelTimeStart()
        goneTravelTimeEnd()
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
                    startTravelDistanceService()
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
                is Resource.Loading -> {
                    binding.ddfProgressBar.toVisible()
                }
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
                    it.message.let { it1 ->
                        when (it1) {
                            TOKEN_IS_INVALID -> {
                                projectTravelDetailsViewModel.showFailureToastMessage(it.message)
                                sessionExpiredLoginRedirection()
                            }
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun handleGetProjectDetailResult(status: Resource<GetTravelResponse>) {
        status.let {
            when (it) {
                is Resource.Loading -> {
                    binding.ddfProgressBar.toVisible()
                }
                is Resource.Success -> status.data?.let {
                    binding.ddfProgressBar.toGone()
                    bindGetTravelTimeData(it)
                }
                is Resource.DataError -> {
                    binding.ddfProgressBar.toGone()
                    status.errorCode?.let {
                        projectTravelDetailsViewModel.showToastMessage(it)
                    }
                }
                is Resource.Failure -> status.data?.let {
                    binding.ddfProgressBar.toGone()
                    it.message.let { it1 ->
                        when (it1) {
                            TOKEN_IS_INVALID -> {
                                projectTravelDetailsViewModel.showFailureToastMessage(it.message)
                                sessionExpiredLoginRedirection()
                            }
                        }
                    }
                    visibleStartButton()
                    //goneTravelTimeStart()
                    goneTravelTimeEnd()
                    changeTimerStartHint()
                }
                else -> {
                }
            }
        }
    }

    private fun changeTimerStopHint() {
        mtimerStatus = 1
        setTimerText(R.string.action_stop)
    }

    private fun changeTimerStartHint() {
        mtimerStatus = 0
        setTimerText(R.string.action_start)
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
                    it.message.let { it1 ->
                        when (it1) {
                            TOKEN_IS_INVALID -> {
                                projectTravelDetailsViewModel.showFailureToastMessage(it.message)
                                sessionExpiredLoginRedirection()
                            }
                        }
                    }
                }
                else -> {
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
                    visibleNextButton()
                    stopTravelDistanceService()
                }
                is Resource.DataError -> {
                    binding.ddfProgressBar.toGone()
                    it.errorCode?.let {
                        projectTravelDetailsViewModel.showToastMessage(it)
                    }
                }
                is Resource.Failure -> it.data?.let {
                    binding.ddfProgressBar.toGone()
                    it.message.let { it1 ->
                        when (it1) {
                            TOKEN_IS_INVALID -> {
                                projectTravelDetailsViewModel.showFailureToastMessage(it.message)
                                sessionExpiredLoginRedirection()
                            }
                        }
                    }
                }
                else -> {
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

    private fun visibleStartButton() {
        binding.dfTimerConstraintLayout.visibility = View.VISIBLE
        binding.constraintddfTravelTimeHint.visibility = View.VISIBLE
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
        bindProjectStatusColor(projectTravelDetailsResponse)
        bindProjectId(projectTravelDetailsResponse.data.project_details.id)
        bindProjectDescriptionDetails(projectTravelDetailsResponse)
        bindProjectTeamMembers(projectTravelDetailsResponse)
        apiCallBacks(EnumIntUtils.THREE.code)
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

    private fun bindGetTravelTimeData(getTravelResponse: GetTravelResponse) {
        getTravelResponse.let {
            if (it.data.started_at.isNotEmpty()) {
                visibleTravelTimeStart()
                binding.textddfTravelTimeStart.text =
                    DateUtils.returnCurrentTime(it.data.started_at)
                projectTravelDetailsViewModel.storeLocalTravelStartId(it.data.id)
                visibleTimerHint()
                goneNextButton()
            } else {
                goneTravelTimeStart()
                goneNextButton()
            }

            if (it.data.ended_at.isNotEmpty()) {
                visibleTravelTimeEnd()
                it.data.travel_distance.let { it1 ->
                    binding.textviewFptdTravelDistance.text = String.format(
                        "$it1 " + resources.getString(
                            R.string.msg_km
                        )
                    )
                }
                binding.textddfTravelTimeEnd.text =
                    DateUtils.returnCurrentTime(it.data.ended_at)
                binding.dfTimerConstraintLayout.visibility = View.GONE
                visibleTimerHint()
                visibleNextButton()
            } else {
                if (!homeActivity.isMyServiceRunning(RSSPullService::class.java)) {
                    startTravelDistanceService()
                }
                goneTravelTimeEnd()
                visibleStartButton()
                changeTimerStopHint()
                goneNextButton()
            }
        }
    }

    private fun visibleTimerHint() {
        binding.constraintddfTravelTimeHint.visibility = View.VISIBLE
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


    private fun goneTravelTimeStart() {
        binding.constraintWholeTravelTime.visibility = View.GONE
        binding.textddfTravelTimeStartHint.visibility = View.GONE
        binding.textddfTravelTimeStart.visibility = View.GONE
    }

    private fun goneTravelTimeEnd() {
        binding.textddfTravelTimeEndHint.visibility = View.GONE
        binding.textddfTravelTimeEnd.visibility = View.GONE
    }

    private fun visibleNextButton() {
        binding.fptdNextButton.visibility = View.VISIBLE
    }

    private fun goneNextButton() {
        binding.fptdNextButton.visibility = View.GONE
    }

    private fun startTravelDistanceService() {
        val serviceIntent = Intent(requireContext(), RSSPullService::class.java)
        serviceIntent.putExtra(
            EnumStringUtils.ServiceIntent.toString(),
            this.getString(R.string.app_name) + " " + this.getString(R.string.title_is_working)
        )
        ContextCompat.startForegroundService(requireContext(), serviceIntent)
        updateDistanceInPerSecond()
    }

    private fun stopTravelDistanceService() {
        val stopService = Intent(requireContext(), RSSPullService::class.java)
        stopService.putExtra(
            EnumStringUtils.ServiceIntent.toString(),
            EnumStringUtils.StopService.toString(),
        )
        ContextCompat.startForegroundService(requireContext(), stopService)
        runnable?.let { handler.removeCallbacks(it) }
    }

    override fun onResume() {
        super.onResume()
        if (homeActivity.isMyServiceRunning(RSSPullService::class.java)) {
            updateDistanceInPerSecond()
        }
    }

    private fun updateDistanceInPerSecond() {
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            binding.textviewFptdTravelDistance.text =
                String.format(
                    localRepository.getTravelDistanceInKilometer() + " " + resources.getString(
                        R.string.msg_km
                    )
                )
        }.also { runnable = it }, delay.toLong())
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        super.onNetworkConnectionChanged(isConnected)
        enableDisableLayout(isConnected, fptdRootScrollView)
        showShackBarMessage(
            isConnected,
            fptdRootScrollView,
            requireActivity().getString(R.string.msg_offline)
        )
    }


}