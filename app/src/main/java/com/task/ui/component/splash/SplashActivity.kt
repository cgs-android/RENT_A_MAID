package com.task.ui.component.splash

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.widget.VideoView
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.task.BuildConfig
import com.task.R
import com.task.SPLASH_DELAY
import com.task.databinding.ActivitySplashBinding
import com.task.ui.base.BaseActivity
import com.task.ui.component.login.LoginActivity
import com.task.ui.component.login.LoginViewModel
import com.task.utils.GpsTracker
import com.task.utils.GpsUtils
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import java.lang.Math.*


@AndroidEntryPoint
class SplashActivity : BaseActivity(), LocationListener {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivitySplashBinding
    private var mMapController: MapController? = null

    private var gpsTracker: GpsTracker? = null


    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var wayLatitude = 0.0
    private var wayLongitude = 0.0
    lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    private val stringBuilder: StringBuilder? = null

    private val isContinue = true
    private var isGPS = false


    override fun initViewBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //gpsTracker = GpsTracker(this)


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(10 * 1000.toLong()) // 10 seconds
        locationRequest.setFastestInterval(5 * 1000.toLong()) // 5 seconds


        GpsUtils(this).turnGPSOn(object : GpsUtils.onGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                isGPS = isGPSEnable
            }
        })

//        Configuration.getInstance().load(
//            this,
//            PreferenceManager.getDefaultSharedPreferences(this)
//        )
//
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult?) {
//                if (locationResult == null) {
//                    return
//                }
//                for (location in locationResult.getLocations()) {
//                    if (location != null) {
//                        wayLatitude = location.latitude
//                        wayLongitude = location.longitude
//                        if (!isContinue) {
//                            plotCurrentLatAndLon()
//                            drawLineOverMap()
//                            Log.e("eeee" + wayLatitude, "eeee" + wayLongitude)
//                        } else {
//                            plotCurrentLatAndLon()
//                            drawLineOverMap()
//                            Log.e("eeeerr" + wayLatitude, "eeeerrrrrrr" + wayLongitude)
//                        }
//                        if (!isContinue && mFusedLocationClient != null) {
//                            mFusedLocationClient!!.removeLocationUpdates(locationCallback)
//                        }
//                    }
//                }
//            }
//        }
//
//        getLocation()
//        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
//
//
//        binding.mapView.setBuiltInZoomControls(true)
//        binding.mapView.setMultiTouchControls(true)
//        binding.mapView.getController().setZoom(4)
//        drawLineOverMap()


        if (loginViewModel.isNetworkAvailable()) {
            binding.textAsNoNetwork.visibility = VideoView.GONE
            navigateToMainScreen()
        } else {
            binding.textAsNoNetwork.visibility = VideoView.VISIBLE
        }


    }

    private fun drawLineOverMap() {
        Thread(Runnable {
            Log.e(
                "distanceLog",
                "" + meterDistanceBetweenPoints(13.0704245, 80.2027604, 9.5121171, 77.6340213)
                        + "timeTaken " + timeTaken()
            )
            //val roadManager: RoadManager = MapQuestRoadManager("AATMQWBWwlkvdGSbJcwR5TjdXvCkAH1u")
            val roadManager: RoadManager = OSRMRoadManager(this)
            //roadManager.addRequestOption("routeType=bicycle");
            //val roadManager: RoadManager = OSRMRoadManager(this)
            val waypoints = ArrayList<GeoPoint>()
            waypoints.add(GeoPoint(13.0704245, 80.2027604))
            val endPoint = GeoPoint(9.5121171, 77.6340213)
            waypoints.add(endPoint)
            val road = roadManager.getRoad(waypoints)

            runOnUiThread {
                if (road.mStatus != Road.STATUS_OK) {
                    //handle error... warn the user, etc.
                    Log.d("Error", "" + road.mStatus)
                }
                val roadOverlay = RoadManager.buildRoadOverlay(road, Color.BLUE, 10.0F)
                roadOverlay.isGeodesic = true
                binding.mapView.getOverlays().add(roadOverlay)
            }



            binding.mapView.invalidate()
        }).start()
    }

    private fun plotCurrentLatAndLon() {
        val berlinGeoPoint = GeoPoint(wayLatitude, wayLongitude)
        val overlayItem = OverlayItem("Berlin", "City", berlinGeoPoint)
        val markerDrawable: Drawable = this.resources.getDrawable(R.drawable.ic_menu_mylocation)
        overlayItem.setMarker(markerDrawable)
        val overlayItemArrayList: ArrayList<OverlayItem> = ArrayList<OverlayItem>()
        overlayItemArrayList.clear()
        overlayItemArrayList.add(overlayItem)
        val locationOverlay: ItemizedOverlay<OverlayItem> =
            ItemizedIconOverlay<OverlayItem>(this, overlayItemArrayList, null)
        //binding.mapView.overlays.remove(locationOverlay)
        binding.mapView.invalidate()
        binding.mapView.overlayManager.clear()
        binding.mapView.getOverlays().add(locationOverlay)
    }

    private fun meterDistanceBetweenPoints(
        lat_a: Double,
        lng_a: Double,
        lat_b: Double,
        lng_b: Double
    ): Double {
        val pk = (180f / PI).toFloat()
        val a1 = lat_a / pk
        val a2 = lng_a / pk
        val b1 = lat_b / pk
        val b2 = lng_b / pk
        val t1 =
            cos(a1.toDouble()) * cos(a2.toDouble()) * cos(
                b1.toDouble()
            ) * cos(b2.toDouble())
        val t2 =
            cos(a1.toDouble()) * sin(a2.toDouble()) * cos(
                b1.toDouble()
            ) * sin(b2.toDouble())
        val t3 =
            sin(a1.toDouble()) * sin(b1.toDouble())
        val tt = acos(t1 + t2 + t3)
        return 6366000 * tt
    }


    val AVERAGE_RADIUS_OF_EARTH_KM = 6371.0
    fun calculateDistanceInKilometer(
        userLat: Double, userLng: Double,
        venueLat: Double, venueLng: Double
    ): Int {
        val latDistance = toRadians(userLat - venueLat)
        val lngDistance = toRadians(userLng - venueLng)
        val a =
            (sin(latDistance / 2) * sin(latDistance / 2)
                    + (cos(toRadians(userLat)) * cos(
                toRadians(venueLat)
            )
                    * sin(lngDistance / 2) * sin(lngDistance / 2)))
        val c =
            2 * atan2(sqrt(a), sqrt(1 - a))
        return round(AVERAGE_RADIUS_OF_EARTH_KM * c).toInt()
    }

    private fun getLocation() {
        if (isContinue) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mFusedLocationClient!!.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        } else {
            mFusedLocationClient!!.lastLocation
                .addOnSuccessListener(this) { location ->
                    if (location != null) {
                        wayLatitude = location.getLatitude()
                        wayLongitude = location.getLongitude()
                    } else {
                        mFusedLocationClient!!.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            null
                        )
                    }
                }
        }

    }


    private fun calculateDistance(): Float {
        val mylocation = Location("")
        val dest_location = Location("")
        dest_location.setLatitude(9.5121171)
        dest_location.setLongitude(77.6340213)
        mylocation.setLatitude(13.0704245)
        mylocation.setLongitude(80.2027604)
        val distance: Float = mylocation.distanceTo(dest_location) //in meters
        return distance
    }

    private fun timeTaken(): Double {
        val speed = 40
        val time: Double =
            meterDistanceBetweenPoints(13.0704245, 80.2027604, 9.5121171, 77.6340213) / speed
        return time
    }


    fun haversine(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        var lat1 = lat1
        var lat2 = lat2
        val Rad = 6372.8 //Earth's Radius In kilometers
        // TODO Auto-generated method stub
        val dLat = toRadians(lat2 - lat1)
        val dLon = toRadians(lon2 - lon1)
        lat1 = toRadians(lat1)
        lat2 = toRadians(lat2)
        val a =
            sin(dLat / 2) * sin(dLat / 2) + sin(dLon / 2) * sin(
                dLon / 2
            ) * cos(lat1) * cos(lat2)
        val c = 2 * asin(sqrt(a))
        return Rad * c
    }

    override fun observeViewModel() {
    }

    override fun onResume() {
        super.onResume()
        Configuration.getInstance().load(
            this,
            PreferenceManager.getDefaultSharedPreferences(this)
        )
        binding.mapView.onResume()
//        requestPermission(
//            this,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )
//        requestPermission(this, Manifest.permission.INTERNET)
//        requestPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
//        requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//        requestPermission(
//            this,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        )
    }

    override fun onPause() {
        super.onPause()
        Configuration.getInstance().save(
            this,
            PreferenceManager.getDefaultSharedPreferences(this)
        );
    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        for (permission in permissions) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
//            }
//        }
//    }


    private fun navigateToMainScreen() {
        Handler().postDelayed({
            val nextScreenIntent = Intent(this, LoginActivity::class.java)
            startActivity(nextScreenIntent)
            finish()
        }, SPLASH_DELAY.toLong())
    }

    fun requestPermission(activity: Activity?, permission: String) {
        if (ContextCompat.checkSelfPermission(activity!!, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), 0)
        }
    }


    private fun distance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
        unit: String
    ): Double {

        return if (lat1 == lat2 && lon1 == lon2) {
            0.0
        } else {
            val theta = lon1 - lon2
            var dist =
                sin(toRadians(lat1)) * sin(
                    toRadians(lat2)
                ) + cos(toRadians(lat1)) * cos(
                    toRadians(
                        lat2
                    )
                ) * cos(toRadians(theta))
            dist = acos(dist)
            dist = toDegrees(dist)
            dist = dist * 60 * 1.1515
            if (unit == "K") {
                dist = dist * 1.609344
            } else if (unit == "N") {
                dist = dist * 0.8684
            }
            dist
        }
    }


    fun getKilometers(
        lat1: Double,
        long1: Double,
        lat2: Double,
        long2: Double
    ): Double {
        val PI_RAD = Math.PI / 180.0
        val phi1 = lat1 * PI_RAD
        val phi2 = lat2 * PI_RAD
        val lam1 = long1 * PI_RAD
        val lam2 = long2 * PI_RAD
        return 6371.01 * acos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(lam2 - lam1))
    }

    private fun distance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {


        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return milesTokm(dist)
    }

    private fun milesTokm(distanceInMiles: Double): Double {
        return distanceInMiles * 1.60934
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    override fun onLocationChanged(location: Location) {

    }


}
