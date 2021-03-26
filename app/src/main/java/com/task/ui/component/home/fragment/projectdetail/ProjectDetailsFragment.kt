package com.task.ui.component.home.fragment.projectdetail

import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.task.R
import com.task.data.Resource
import com.task.data.dto.projectdetails.ProjectDetailsResponse
import com.task.data.dto.projectlist.ProjectListDataResponse
import com.task.databinding.FragmentProjectDetailsBinding
import com.task.ui.base.BaseFragment
import com.task.ui.component.home.HomeActivity
import com.task.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.item_header.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class ProjectDetailsFragment : BaseFragment(), View.OnClickListener,
    HomeActivity.OnBackPressedListner {


    private var mStartTime: Long = 0
    private var mPauseTimer: Boolean = false
    private var mPauseAndResume: Boolean = false
    private var mCountTimer = 0
    private lateinit var homeActivity: HomeActivity
    private var mTravelTime: String = ""
    private var mWorkTime: String = ""

    private var mTeamMember: String? = ""
    private var mTeamLead: String? = ""

    private var mtimerStatus: Int = 0
    private var mEndTime: String? = ""

    private lateinit var projectDetailsViewModel: ProjectDetailsViewModel

    private lateinit var binding: FragmentProjectDetailsBinding

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
        binding = FragmentProjectDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun observeViewModel() {
        observeToast(projectDetailsViewModel.showToast)
        observe(projectDetailsViewModel.projectDetails, ::handleProjectDetailResult)
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
                    projectDetailsViewModel.showToastMessage(it)
                }
            }
            is Resource.Failure -> status.data?.let {
                binding.ddfProgressBar.toGone()
                projectDetailsViewModel.showFailureToastMessage(it.message)
            }
        }
    }

    private fun bindProjectDetailsData(projectDetailsResponse: ProjectDetailsResponse) {
        when (getBundleProjectListDataResponse().project_details.projectStatusColor) {
            1 -> {
                binding.ddfProjectStatusImageView.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorGreen
                    )
                )
                binding.dfSwipeButtonStartSlidingButton.visibility = View.GONE
            }
            -1 -> {
                binding.ddfProjectStatusImageView.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorBlue
                    )
                )
            }
            0 -> {
                binding.ddfProjectStatusImageView.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorOrange
                    )
                )
            }
        }

        binding.pdfProjectIdTextView.text =
            String.format(
                requireActivity().resources.getString(
                    R.string.project
                ) + " 000" + getBundleProjectListDataResponse().project_details.id
            )
        projectDetailsResponse.let {

            binding.pdfProjectLocationDescriptionTextView.text =
                String.format(
                    it.data.project_location.company_name + "\n" +
                            it.data.project_location.address_line1 + "" +
                            it.data.project_location.address_line2 + "\n" +
                            it.data.project_location.pincode + "" +
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
        binding.dfTimerTextView.setOnClickListener(this)
        hiMenuNavigationImageView.setOnClickListener(this)

        binding.dfSwipeButtonStartSlidingButton.setOnStateChangeListener { active ->
            startTimerClock()
            binding.dfSwipeButtonStartSlidingButton.visibility = View.GONE
            binding.dfSwipeButtonEndSlidingButton.visibility = View.VISIBLE
        }
        binding.dfSwipeButtonEndSlidingButton.setOnStateChangeListener { active ->
            binding.dfSwipeButtonStartWorkTimeSlidingButton.visibility = View.VISIBLE
            binding.dfSwipeButtonEndSlidingButton.visibility = View.GONE
            mTravelTime = binding.dfTimerTextView.text.toString()
            binding.dfTravelTimeTextView.text =
                this.resources.getString(R.string.travel_time) + " : " + mTravelTime
            resetTimerClock()
        }
        binding.dfSwipeButtonStartWorkTimeSlidingButton.setOnStateChangeListener { active ->
            startTimerClock()
            binding.dfTimeHintTextView.text = this.resources.getString(R.string.work_time)
            binding.dfSwipeButtonEndWorkTimeSlidingButton.visibility = View.VISIBLE
            binding.dfSwipeButtonStartWorkTimeSlidingButton.visibility = View.GONE
        }
        binding.dfSwipeButtonEndWorkTimeSlidingButton.setOnStateChangeListener { active ->
            mWorkTime = binding.dfTimerTextView.text.toString()
            binding.dfWorkTimeTextView.text =
                this.resources.getString(R.string.work_time) + "  : " + mWorkTime
            binding.dfSwipeButtonEndWorkTimeSlidingButton.visibility = View.GONE
            binding.dfTravelTimeCard.visibility = View.VISIBLE
            binding.dfTimerTextView.text = this.resources.getString(R.string.zeros)
            binding.dfPauseTextView.visibility = View.INVISIBLE
            binding.dfTimeHintTextView.visibility = View.GONE
            resetTimerClock()
        }
    }

    override fun initAppHeader() {

    }

    override fun init() {
        homeActivity = activity as HomeActivity
    }


    private fun getBundleProjectListDataResponse(): ProjectListDataResponse {
        val args = arguments
        val projectListJsonString = args?.getString(BUNDLE_PROJECT_DETAILS)
        return GsonBuilder().create()
            .fromJson(projectListJsonString, ProjectListDataResponse::class.java)
    }

    override fun apiCallBacks(event: Int) {
        when (event) {
            EnumIntUtils.ZERO.code -> {
                projectDetailsViewModel.getProjectDetails(getBundleProjectListDataResponse().project_details.id)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        projectDetailsViewModel =
            ViewModelProviders.of(this).get(ProjectDetailsViewModel::class.java)
        observeViewModel()
        apiCallBacks(0)
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
                    mPauseAndResume = false
                    binding.dfPauseTextView.text = this.resources.getString(R.string.tap_to_pause)
                    binding.dfPauseTextView.setTextColor(this.resources.getColor(R.color.colorAliceBlue))
                    resumeTimerClock()
                } else {
                    mPauseAndResume = true
                    binding.dfPauseTextView.text = this.resources.getString(R.string.tap_to_resume)
                    binding.dfPauseTextView.setTextColor(this.resources.getColor(R.color.colorRed))
                    endTimerClock()
                }
            }
            hiMenuNavigationImageView -> {
                homeActivity.drawerOpenAndClose()
            }
            binding.dfTimerTextView -> {
                val dateTimeFormatter =
                    DateTimeFormatter.ofPattern("hh:mm").format(LocalTime.now()).toString()
                when (mtimerStatus) {
                    0 -> {
                        binding.textddfTravelTimeStart.text = dateTimeFormatter
                        visibleTravelTimeStart()
                        startTimerClock()
                        binding.dfTimerTextView.text =
                            requireActivity().getString(R.string.action_stop)
                        mtimerStatus = 1
                    }

                    1 -> {
                        visibleTravelTimeEnd()
                        binding.textddfTravelTimeEnd.text = dateTimeFormatter
                        resetTimerClock()
                        binding.dfTimerTextView.text =
                            requireActivity().getString(R.string.action_start)
                        mtimerStatus = 2
                        binding.textddfTravelTimeEnd.setTextColor(
                            requireActivity().resources.getColor(
                                R.color.colorAliceBlue
                            )
                        )
                        binding.textddfTravelTimeStart.setTextColor(
                            requireActivity().resources.getColor(
                                R.color.colorAliceBlue
                            )
                        )
                    }
                    2 -> {
                        binding.textddfWorkTimeStart.text = dateTimeFormatter
                        visibleWorkTimeStart()
                        startTimerClock()
                        binding.dfTimerTextView.text =
                            requireActivity().getString(R.string.action_stop)
                        mtimerStatus = 3
                    }
                    3 -> {
                        visibleWorkTimeEnd()
                        binding.textddfWorkTimeEnd.text = dateTimeFormatter
                        resetTimerClock()
                        binding.dfTimerTextView.text =
                            requireActivity().getString(R.string.action_stop)
                        binding.dfTimerConstraintLayout.visibility = View.GONE
                        mtimerStatus = 0
                        binding.textddfWorkTimeEnd.setTextColor(
                            requireActivity().resources.getColor(
                                R.color.colorAliceBlue
                            )
                        )
                        binding.textddfWorkTimeStart.setTextColor(
                            requireActivity().resources.getColor(
                                R.color.colorAliceBlue
                            )
                        )
                    }
                }

            }

        }
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


    private fun visibleWorkTimeStart() {
        binding.constraintWholeWorkTime.visibility = View.VISIBLE
        binding.textddfWorkTimeStartHint.visibility = View.VISIBLE
        binding.textddfWorkTimeStart.visibility = View.VISIBLE
    }

    private fun visibleWorkTimeEnd() {
        binding.textddfWorkTimeEndHint.visibility = View.VISIBLE
        binding.textddfWorkTimeEnd.visibility = View.VISIBLE
    }


    override fun onBackPressed(): Boolean {
        val arg = Bundle()
        homeActivity.changeFragment(EnumIntUtils.ONE.code, arg)
        return true
    }


}