package com.task.ui.base


import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.task.R
import com.task.ui.component.login.LoginActivity
import com.task.utils.ConnectivityReceiver
import com.task.utils.GpsUtils


abstract class BaseFragment : Fragment(), GpsUtils.onGpsListener,
    ConnectivityReceiver.ConnectivityReceiverListener {

    private var locationManager: LocationManager? = null
    private var snackBar: Snackbar? = null
    abstract fun observeViewModel()
    abstract fun initOnClickListeners()
    abstract fun initAppHeader()
    abstract fun init()
    abstract fun apiCallBacks(event: Int)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initNetworkReceiver()
        initOnClickListeners()
        initAppHeader()
    }

    private fun initNetworkReceiver() {
        requireActivity().registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onResume() {
        super.onResume()
        turnGPSOn()
        networkListener()
    }

    private fun turnGPSOn() {
        GpsUtils(requireActivity()).turnGPSOn(this)
    }

    fun sessionExpiredLoginRedirection() {
        requireActivity().finishAffinity()
        val loginIntent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(loginIntent)
    }

    private fun networkListener() {
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

    }


    fun showShackBarMessage(isConnected: Boolean, view: View, msg: String = "") {
        if (!isConnected) {
            snackBar = Snackbar.make(
                view,
                msg,
                Snackbar.LENGTH_LONG
            )
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.setBackgroundTint(requireActivity().resources.getColor(R.color.colorRed))
            snackBar?.changeFont()
            snackBar?.show()
        } else {
            snackBar?.dismiss()
        }
    }

    private fun Snackbar.changeFont() {
        val tv = view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        val font = Typeface.createFromAsset(context.assets, "font/Montserrat-Bold.ttf")
        tv.typeface = font
    }


    fun enableDisableLayout(isConnected: Boolean, view: View, ) {
        setViewAndChildrenEnabled(view, isConnected)
    }

    private fun setViewAndChildrenEnabled(
        view: View,
        enabled: Boolean
    ) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                setViewAndChildrenEnabled(child, enabled)
            }
        }
    }

    fun isCheckGpsStatus(): Boolean {
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val gpsInfo = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return gpsInfo != null && gpsInfo
    }

}