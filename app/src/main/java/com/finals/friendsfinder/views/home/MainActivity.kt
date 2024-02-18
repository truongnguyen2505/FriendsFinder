package com.finals.friendsfinder.views.home

import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.PermissionUtils
import com.finals.friendsfinder.R
import com.finals.friendsfinder.bases.BaseActivity
import com.finals.friendsfinder.databinding.ActivityMainBinding
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.views.chatting.AllMessageFragment
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
    private var mListLinear: List<LinearLayout> = listOf()
    private var mListTV: List<TextView> = listOf()
    private var mListImg: List<ImageView> = listOf()

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
        mListTV = listOf(rootView.tvHome, rootView.tvChat, rootView.tvProfile)
        mListImg = listOf(rootView.imgHome, rootView.imgChat, rootView.imgProfile)
        mListLinear = listOf(rootView.btnHome, rootView.btnChat, rootView.btnProfile)
        setButtonSelect()
    }

    override fun showMain() {
        super.showMain()
        setHomeButton()
    }

    private fun setHomeButton() {
        with(rootView) {
            mListLinear.forEachIndexed { index, linearLayout ->
                when (index) {
                    0 -> {
                        mListLinear[0].setBackgroundResource(R.drawable.bg_selected_black_border)
                        mListTV[0].setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                        mListImg[0].setImageResource(R.drawable.ic_home_white)
                    }
                    1->{
                        mListLinear[1].setBackgroundResource(R.drawable.bg_unselected_white_border)
                        mListTV[1].setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
                        mListImg[1].setImageResource(R.drawable.ic_chat_black)
                    }
                    2->{
                        mListLinear[2].setBackgroundResource(R.drawable.bg_unselected_white_border)
                        mListTV[2].setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
                        mListImg[2].setImageResource(R.drawable.ic_user_black)
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun setButtonSelect() {
        with(rootView) {
            mListLinear.forEachIndexed { index, linearLayout ->
                linearLayout.setOnClickListener {
                    mListLinear.forEach { v ->
                        v.setBackgroundResource(R.drawable.bg_unselected_white_border)
                    }
                    mListTV.forEach { v ->
                        v.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
                    }
                    mListImg.forEachIndexed { mIndex, imageView ->
                        when (mIndex) {
                            0 -> {
                                mListImg[0].setImageResource(R.drawable.ic_home_black)
                            }

                            1 -> {
                                mListImg[1].setImageResource(R.drawable.ic_chat_black)
                            }

                            2 -> {
                                mListImg[2].setImageResource(R.drawable.ic_user_black)
                            }

                            else -> {
                                mListImg[index].setImageResource(R.drawable.ic_home_black)
                            }
                        }
                    }
                    when (index) {
                        0 -> {
                            mListImg[0].setImageResource(R.drawable.ic_home_white)
                        }

                        1 -> {
                            mListImg[1].setImageResource(R.drawable.ic_chat_white)
                            addFragmentToBackstack(
                                android.R.id.content,
                                AllMessageFragment.newInstance()
                            )
                        }

                        2 -> {
                            mListImg[2].setImageResource(R.drawable.ic_user_white)
                            addFragmentToBackstack(
                                android.R.id.content,
                                MenuFragment.newInstance()
                            )
                        }

                        else -> {
                            mListImg[index].setImageResource(R.drawable.ic_home_white)
                        }
                    }
                    mListLinear[index].setBackgroundResource(R.drawable.bg_selected_black_border)
                    mListTV[index].setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        )
                    )
                }
            }
        }
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