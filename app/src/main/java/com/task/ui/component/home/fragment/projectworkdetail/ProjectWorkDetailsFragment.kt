package com.task.ui.component.home.fragment.projectworkdetail

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.task.BUNDLE_PROJECT_DETAILS
import com.task.R
import com.task.data.Resource
import com.task.data.dto.drawer.DrawerResponse
import com.task.data.dto.projectdetails.ProjectDetailsResponse
import com.task.data.dto.projectlist.ProjectListDataResponse
import com.task.data.dto.worktime.WorkLogResponse
import com.task.databinding.FragmentProjectDetailsBinding
import com.task.databinding.FragmentProjectWorkDetailsBinding
import com.task.ui.base.BaseFragment
import com.task.ui.component.home.HomeActivity
import com.task.ui.component.home.adapter.DrawerAdapter
import com.task.ui.component.home.fragment.projectworkdetail.adapter.ProjectWorkLogAdapter
import com.task.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.item_header.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@AndroidEntryPoint
class ProjectWorkDetailsFragment : BaseFragment(), View.OnClickListener,
    HomeActivity.OnBackPressedListner {


    @Inject
    lateinit var dialogHelper: DialogHelper

    private var mStartTime: Long = 0
    private var mPauseTimer: Boolean = false
    private var mPauseAndResume: Boolean = false
    private var mCountTimer = 0
    private lateinit var homeActivity: HomeActivity


    private var mTeamMember: String? = ""
    private var mTeamLead: String? = ""

    private var mtimerStatus: Int = 0
    private var mDrawerFlag: Boolean = true
    private var mEndTime: String? = ""

    private lateinit var projectWorkLogAdapter: ProjectWorkLogAdapter

    private lateinit var projectWorkDetailsViewModel: ProjectWorkDetailsViewModel

    private lateinit var binding: FragmentProjectWorkDetailsBinding

    @RequiresApi(Build.VERSION_CODES.O)
    val mCurrentTime =
        DateTimeFormatter.ofPattern("HH:mm").format(LocalTime.now()).toString()

    var timerHandler: Handler = Handler()
    private var timerRunnable: Runnable = object : Runnable {
        override fun run() {
            val millis = System.currentTimeMillis() - mStartTime
            var seconds = (millis / 1000).toInt()
            val minutes = seconds / 60
            seconds %= 60

            val timeTickers = String.format(
                "%02d:%02d:%02d",
                mCountTimer / 3600,
                mCountTimer % 3600 / 60,
                mCountTimer % 60
            )
            /*String.format("%d:%02d", minutes, seconds)*/
            //binding.dfTimerTextView.text = timeTickers
            mEndTime = timeTickers
            println(mEndTime)
            if (!mPauseTimer) {
                mCountTimer++
                timerHandler.postDelayed(this, 1000)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }


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
        observe(projectWorkDetailsViewModel.projectDetails, ::handleProjectDetailResult)
    }

    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(context, 2)
        binding.recyclerFpwdTravelTimeList.layoutManager = layoutManager
        binding.recyclerFpwdTravelTimeList.setHasFixedSize(true)

    }


    private fun bindDrawerData(workLogResponse: MutableList<WorkLogResponse>) {
        if (!(workLogResponse.isNullOrEmpty())) {
            projectWorkLogAdapter = ProjectWorkLogAdapter(requireContext(), workLogResponse)
            binding.recyclerFpwdTravelTimeList.adapter = projectWorkLogAdapter
        }
    }


    private fun handleProjectDetailResult(status: Resource<ProjectDetailsResponse>) {
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

    private fun bindProjectDetailsData(projectDetailsResponse: ProjectDetailsResponse) {

        binding.pdfProjectIdTextView.text =
            String.format(
                requireActivity().resources.getString(
                    R.string.project
                ) + requireActivity().resources.getString(
                    R.string.zeros
                ) + projectDetailsResponse.data.project_details.id
            )
        projectDetailsResponse.let {

            binding.pdfProjectLocationDescriptionTextView.text =
                String.format(
                    it.data.project_location.company_name + "\n" +
                            it.data.project_location.address_line1 + "" +
                            it.data.project_location.address_line2 + "\n" +
                            it.data.project_location.pincode + " " +
                            it.data.project_location.city
                )

            binding.ddfWorkStartTimeTextView.text =
                String.format(
                    it.data.project_details.work_start_time + " " + requireActivity().resources.getString(
                        R.string.hrs
                    )
                )


            mTeamLead = ""
            mTeamMember = ""
            for (team in it.data.team_members) {
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

    override fun initOnClickListeners() {
        binding.dfPauseTextView.setOnClickListener(this)
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


    private fun getBundleProjectListDataResponse(): ProjectDetailsResponse {
        val args = arguments
        val projectListJsonString = args?.getString(BUNDLE_PROJECT_DETAILS)
        return GsonBuilder().create()
            .fromJson(projectListJsonString, ProjectDetailsResponse::class.java)
    }

    override fun apiCallBacks(event: Int) {
        when (event) {
            EnumIntUtils.ZERO.code -> {
                //projectWorkDetailsViewModel.getProjectDetails(getBundleProjectListDataResponse().project_details.id)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        projectWorkDetailsViewModel =
            ViewModelProviders.of(this).get(ProjectWorkDetailsViewModel::class.java)
        observeViewModel()
        bindProjectDetailsData(getBundleProjectListDataResponse())
        //apiCallBacks(0)
    }

    private fun startTimerClock() {
        mCountTimer = 0
        timerHandler.removeCallbacks(timerRunnable)
        mStartTime = System.currentTimeMillis()
        timerHandler.postDelayed(timerRunnable, 0)
    }


    private fun resumeTimerClock() {
        mPauseTimer = false
        timerHandler.removeCallbacks(timerRunnable)
        timerHandler.postDelayed(timerRunnable, 0)
    }

    private fun endTimerClock() {
        mPauseTimer = true
        timerHandler.removeCallbacks(timerRunnable)
    }


    private fun resetTimerClock() {
        mCountTimer = 0
        timerHandler.removeCallbacks(timerRunnable)
    }


    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v) {
            binding.dfPauseTextView -> {
                if (mPauseAndResume) {
                    binding.dfTimerTextView.visibility = View.VISIBLE
                    mPauseAndResume = false
                    binding.dfPauseTextView.text = this.resources.getString(R.string.tap_to_pause)
                    binding.dfPauseTextView.setTextColor(this.resources.getColor(R.color.colorAliceBlue))
                    resumeTimerClock()
                    bindDrawerData(
                        projectWorkDetailsViewModel.loadWorkLogData(
                            requireActivity(),
                            WorkLogResponse(
                                mCurrentTime,
                                requireActivity().resources.getString(R.string.start_work_time),
                                true
                            )
                        )
                    )
                } else {
                    binding.dfTimerTextView.visibility = View.GONE
                    mPauseAndResume = true
                    binding.dfPauseTextView.text = this.resources.getString(R.string.tap_to_resume)
                    binding.dfPauseTextView.setTextColor(this.resources.getColor(R.color.colorRed))
                    endTimerClock()
                    bindDrawerData(
                        projectWorkDetailsViewModel.loadWorkLogData(
                            requireActivity(),
                            WorkLogResponse(
                                mCurrentTime,
                                requireActivity().resources.getString(R.string.end_work_time),
                                true
                            )
                        )
                    )
                }
            }
            hiMenuNavigationImageView -> {
                homeActivity.drawerOpenAndClose()
            }
            binding.dfTimerTextView -> {
                val dateTimeFormatter =
                    DateTimeFormatter.ofPattern("HH:mm").format(LocalTime.now()).toString()
                when (mtimerStatus) {

                    0 -> {
                        binding.dfPauseTextView.visibility = View.VISIBLE
                        //visibleWorkTimeStart()
                        startTimerClock()
                        binding.dfTimerTextView.text =
                            requireActivity().getString(R.string.action_stop)
                        mtimerStatus = 1
                        bindDrawerData(
                            projectWorkDetailsViewModel.loadWorkLogData(
                                requireActivity(),
                                WorkLogResponse(
                                    mCurrentTime,
                                    requireActivity().resources.getString(R.string.start_work_time),
                                    true
                                )
                            )
                        )

                    }
                    1 -> {
                        binding.dfTimerTextView.text =
                            requireActivity().getString(R.string.action_stop)
                        dialogHelper.showAlertDialog(
                            object : DialogHelper.DialogPickListener {

                                override fun onPositiveClicked() {
                                    resetTimerClock()

                                    binding.dfTimerConstraintLayout.visibility = View.GONE
                                    mtimerStatus = 0
                                    bindDrawerData(
                                        projectWorkDetailsViewModel.loadWorkLogData(
                                            requireActivity(),
                                            WorkLogResponse(
                                                mCurrentTime,
                                                requireActivity().resources.getString(R.string.end_work_time),
                                                true
                                            )
                                        )
                                    )
                                }

                                override fun onNegativeClicked() {

                                }
                            },
                            requireActivity().resources.getString(R.string.title_msg_travel_time),
                            requireActivity().resources.getString(R.string.action_confirm),
                            requireActivity().resources.getString(R.string.action_cancel), true
                        )
                    }
                }

            }
            binding.ddfWholeConstraintLayout -> {
                if (mDrawerFlag) {
                    binding.pliProjectDetailsConstraintLayout.visibility = View.VISIBLE
                    binding.imagePwdUpDownArrow.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            requireActivity().resources,
                            R.drawable.ic_down_arrow, requireActivity().theme
                        )
                    )
                    mDrawerFlag = false
                } else {
                    binding.pliProjectDetailsConstraintLayout.visibility = View.GONE
                    binding.imagePwdUpDownArrow.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            requireActivity().resources,
                            R.drawable.ic_right_arrow, requireActivity().theme
                        )
                    )
                    mDrawerFlag = true
                }
            }
        }
    }


    override fun onBackPressed(): Boolean {
        binding.dfPauseTextView.visibility = View.GONE
        mtimerStatus = 0
        projectWorkDetailsViewModel.clearList()
        val arg = Bundle()
        homeActivity.changeFragment(EnumIntUtils.ONE.code, arg)
        return true
    }


}