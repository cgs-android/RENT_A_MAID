package com.task.ui.component.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.VideoView
import androidx.activity.viewModels
import com.task.ui.base.BaseActivity
import com.task.ui.component.login.LoginActivity
import com.task.SPLASH_DELAY
import com.task.databinding.ActivitySplashBinding
import com.task.ui.component.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivitySplashBinding

    override fun initViewBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (loginViewModel.isNetworkAvailable()) {
            binding.textAsNoNetwork.visibility = VideoView.GONE
            navigateToMainScreen()
        } else {
            binding.textAsNoNetwork.visibility = VideoView.VISIBLE
        }

    }

    override fun observeViewModel() {
    }

    private fun navigateToMainScreen() {
        Handler().postDelayed({
            val nextScreenIntent = Intent(this, LoginActivity::class.java)
            startActivity(nextScreenIntent)
            finish()
        }, SPLASH_DELAY.toLong())
    }
}
