package com.task.ui.component.home.fragment.dashboard

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.task.R
import com.task.databinding.DashboardFragmentBinding
import com.task.ui.base.BaseFragment
import com.task.ui.component.home.HomeActivity
import com.task.utils.SingleEvent
import com.task.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.header_item.*


@AndroidEntryPoint
class DashboardFragment : BaseFragment(), View.OnClickListener {


    var startTime: Long = 0
    var pauseTimer: Boolean = false
    var pauseAndResume: Boolean = false
    var countTimer = 0
    lateinit var homeActivity: HomeActivity
    var travelTime: String = ""
    var workTime: String = ""

    private lateinit var dashboardViewModel: DashboardViewModel

    private lateinit var binding: DashboardFragmentBinding

    var timerHandler: Handler = Handler()
    private var timerRunnable: Runnable = object : Runnable {
        override fun run() {
            val millis = System.currentTimeMillis() - startTime
            var seconds = (millis / 1000).toInt()
            val minutes = seconds / 60
            seconds %= 60

            val timeTickers = String.format(
                "%02d:%02d:%02d",
                countTimer / 3600,
                countTimer % 3600 / 60,
                countTimer % 60
            )
            /*String.format("%d:%02d", minutes, seconds)*/
            binding.dfTimerTextView.text = timeTickers
            if (!pauseTimer) {
                countTimer++
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
        binding = DashboardFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun observeViewModel() {
        observeToast(dashboardViewModel.showToast)
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
            travelTime = binding.dfTimerTextView.text.toString()
            binding.dfTravelTimeTextView.text =
                this.resources.getString(R.string.travel_time) + " : " + travelTime
            resetTimerClock()
        }
        binding.dfSwipeButtonStartWorkTimeSlidingButton.setOnStateChangeListener { active ->
            startTimerClock()
            binding.dfTimeHintTextView.text = this.resources.getString(R.string.work_time)
            binding.dfSwipeButtonEndWorkTimeSlidingButton.visibility = View.VISIBLE
            binding.dfSwipeButtonStartWorkTimeSlidingButton.visibility = View.GONE
        }
        binding.dfSwipeButtonEndWorkTimeSlidingButton.setOnStateChangeListener { active ->
            workTime = binding.dfTimerTextView.text.toString()
            binding.dfWorkTimeTextView.text =
                this.resources.getString(R.string.work_time) + "  : " + workTime
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        observeViewModel()
    }

    private fun startTimerClock() {
        countTimer = 0
        timerHandler.removeCallbacks(timerRunnable)
        startTime = System.currentTimeMillis()
        timerHandler.postDelayed(timerRunnable, 0)
    }


    private fun resumeTimerClock() {
        pauseTimer = false
        timerHandler.removeCallbacks(timerRunnable)
        timerHandler.postDelayed(timerRunnable, 0)
    }

    private fun endTimerClock() {
        pauseTimer = true
        timerHandler.removeCallbacks(timerRunnable)
    }


    private fun resetTimerClock() {
        countTimer = 0
        timerHandler.removeCallbacks(timerRunnable)
    }


    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    override fun onClick(v: View?) {
        when (v) {

            binding.dfPauseTextView -> {
                if (pauseAndResume) {
                    pauseAndResume = false
                    binding.dfPauseTextView.text = this.resources.getString(R.string.tap_to_pause)
                    binding.dfPauseTextView.setTextColor(this.resources.getColor(R.color.colorCharcoal))
                    resumeTimerClock()
                } else {
                    pauseAndResume = true
                    binding.dfPauseTextView.text = this.resources.getString(R.string.tap_to_resume)
                    binding.dfPauseTextView.setTextColor(this.resources.getColor(R.color.colorRed))
                    endTimerClock()
                }
            }
            hiMenuNavigationImageView -> {
                homeActivity.drawerOpenAndClose()
            }

        }
    }


}