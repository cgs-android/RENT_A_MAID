package com.task.utils


import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.google.android.gms.location.*
import com.task.CHANNEL_ID
import com.task.R
import com.task.data.local.LocalData
import com.task.ui.component.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RSSPullService : Service() {

    @Inject
    lateinit var localRepository: LocalData


    private var mFusedLocationClient: FusedLocationProviderClient? = null
    lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    private var floatTotalDistance = -1.00

    override fun onCreate() {
        super.onCreate()
        startFetchLocation();
    }

    private fun startFetchLocation() {
        initLocationFushedApi()
        getLocation()
    }


    private fun initLocationFushedApi() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.priority = (LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.interval = (1 * 1000.toLong()) // 10 seconds
        locationRequest.fastestInterval = (1 * 1000.toLong()) // 5 // seconds
        //locationRequest.smallestDisplacement = (10f)


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        runOnUiThread {
                            if (floatTotalDistance < 0) {
                                floatTotalDistance = 0.00
                            } else if (floatTotalDistance >= 0) {
                                floatTotalDistance += 10.00
                            }
                            val km = floatTotalDistance / 1000
                            localRepository.putTravelDistanceInKilometer(km.toString())
                        }
                    }
                }
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra(EnumStringUtils.ServiceIntent.toString())
        if (input.equals(EnumStringUtils.StopService.toString())) {
            mFusedLocationClient?.removeLocationUpdates(locationCallback)
            stopForeground(true)
            stopSelf()
        } else if (input.equals(EnumStringUtils.PauseService.toString())) {
            mFusedLocationClient?.removeLocationUpdates(locationCallback)
        } else if (input.equals(EnumStringUtils.ResumeService.toString())) {
            startFetchLocation()
        }
        initNotification(intent)
        return START_NOT_STICKY
    }


    private fun initNotification(intent: Intent?) {
        val input = intent?.getStringExtra(EnumStringUtils.ServiceIntent.toString())
        createNotificationChannel()
        val notificationIntent = Intent(this, HomeActivity::class.java)
        notificationIntent.putExtra(
            EnumStringUtils.NotificationTravelRedirection.toString(),
            EnumStringUtils.NotificationTravelRedirection.toString()
        )
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }

    }


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient!!.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }
}


