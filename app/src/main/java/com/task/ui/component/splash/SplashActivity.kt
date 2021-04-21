package com.task.ui.component.splash

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.VideoView
import androidx.activity.viewModels
import com.task.PERMISSION_ALL_PERMISSION
import com.task.R
import com.task.SPLASH_DELAY
import com.task.databinding.ActivitySplashBinding
import com.task.ui.base.BaseActivity
import com.task.ui.component.login.LoginActivity
import com.task.ui.component.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashActivity : BaseActivity(), View.OnClickListener {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivitySplashBinding


    override fun initViewBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initOnClickListener()
        checkPermissionAndNavigate()
    }


    private fun checkPermissionAndNavigate() {
        when (hasPermissions()) {
            true -> {
                if (loginViewModel.isNetworkAvailable()) {
                    binding.textAsNoNetwork.visibility = VideoView.GONE
                    navigateToMainScreen()
                } else {
                    binding.textAsNoNetwork.visibility = VideoView.VISIBLE
                }
            }
            false -> {
                requestNecessaryPermissions()
            }
        }
    }

    private fun initOnClickListener() {
        binding.constraintAsWholeLayout.setOnClickListener(this)
    }


    override fun observeViewModel() {
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }


    private fun navigateToMainScreen() {
        Handler().postDelayed({
            val nextScreenIntent = Intent(this, LoginActivity::class.java)
            startActivity(nextScreenIntent)
            finish()
        }, SPLASH_DELAY.toLong())
    }


    override fun onClick(v: View?) {

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var allowed = true
        when (requestCode) {
            PERMISSION_ALL_PERMISSION -> for (res in grantResults) {
                allowed = allowed && res == PackageManager.PERMISSION_GRANTED
            }
            else ->
                allowed = false
        }
        if (allowed) {
            navigateToMainScreen()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    permissionCustomAlert()
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    permissionCustomAlert()
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_NETWORK_STATE)) {
                    permissionCustomAlert()
                } else {
                    permissionCustomAlert()
                }
            }
        }

    }

    private fun permissionCustomAlert() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(this.resources.getString(R.string.permission_required_title))
            .setMessage(this.resources.getString(R.string.permission_required_message))
            .setPositiveButton(this.resources.getString(R.string.setting)) { dialog, which ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = (uri)
                startActivity(intent)
            }
            .setCancelable(false)
            .create()
            .show()
    }

}
