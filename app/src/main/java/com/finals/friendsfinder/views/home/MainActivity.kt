package com.finals.friendsfinder.views.home

import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import com.blankj.utilcode.util.PermissionUtils
import com.finals.friendsfinder.R
import com.finals.friendsfinder.bases.BaseActivity
import com.finals.friendsfinder.databinding.ActivityMainBinding
import com.finals.friendsfinder.utilities.commons.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MainActivity : BaseActivity<ActivityMainBinding>(), OnMapReadyCallback {

    companion object {
        const val TAG = "MAP_ACTIVITY"
    }

    private var isFirstZoom = true
    private var mMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val lat = locationResult.lastLocation?.latitude ?: 0.0
            val lng = locationResult.lastLocation?.longitude ?: 0.0
            val latLng = LatLng(lat, lng)
            Log.d(TAG, "onLocationResult: $lat $lng $isFirstZoom")
            if (isFirstZoom) {
                isFirstZoom = false
                updateMyLocation(latLng)
            }
        }
    }

    private fun checkPermission(onSuccess: (() -> Unit)) {
        PermissionUtils.permission(*Constants.LOCATION_PER)
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(granted: MutableList<String>) {
                    onSuccess.invoke()
                }

                override fun onDenied(
                    deniedForever: MutableList<String>,
                    denied: MutableList<String>,
                ) {
                    //showMessage("Please allow permission Location")
                }
            }).request()

    }

    private fun updateMyLocation(latLng: LatLng) {
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
    }

    @SuppressLint("MissingPermission")
    private fun createLocationRequest() {
        // request get location
        val mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60000L)
            .setWaitForAccurateLocation(false)
            .build()

        fusedLocationProviderClient?.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.getMainLooper()
        )
    }

    private fun initMap() {
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun observeHandle() {
        super.observeHandle()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        initMap()
    }

    override fun setupView() {
        super.setupView()

    }

    override fun setupEventControl() {
        super.setupEventControl()
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        checkPermission {
            createLocationRequest()
        }
    }
}