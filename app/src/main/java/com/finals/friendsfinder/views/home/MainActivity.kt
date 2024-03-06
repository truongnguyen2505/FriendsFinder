package com.finals.friendsfinder.views.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.PermissionUtils
import com.finals.friendsfinder.R
import com.finals.friendsfinder.bases.BaseActivity
import com.finals.friendsfinder.databinding.ActivityMainBinding
import com.finals.friendsfinder.utilities.UserDefaults
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.utilities.commons.LocationKey
import com.finals.friendsfinder.utilities.commons.TableKey
import com.finals.friendsfinder.views.chatting.AllMessageFragment
import com.finals.friendsfinder.views.friends.AddFriendsFragment
import com.finals.friendsfinder.views.friends.data.Friends
import com.finals.friendsfinder.views.friends.data.Location
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.finals.friendsfinder.views.friends.data.UserLocationDTO
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout.Tab
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class MainActivity : BaseActivity<ActivityMainBinding>(), OnMapReadyCallback {

    companion object {
        const val TIME_UPDATE_LOCATION = 60000 * 5L

        //const val TIME_UPDATE_LOCATION = 30000 * 1L
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
            createLocationDB(lat, lng)
            // Log.d(TAG, "onLocationResult: $lat $lng $isFirstZoom")
            if (isFirstZoom) {
                val latLng = LatLng(lat, lng)
                isFirstZoom = false
                updateMyLocation(latLng)
            }
        }
    }
    private var mListLinear: List<LinearLayout> = listOf()
    private var mListTV: List<TextView> = listOf()
    private var mListImg: List<ImageView> = listOf()
    private var fbUser: FirebaseUser? = null
    private var listUserLocation: MutableList<Location?> = mutableListOf()
    private var listUserFriend: MutableList<Friends?> = mutableListOf()
    private var listAllUser: MutableList<UserInfo?> = mutableListOf()
    private var typeCheckLogout = false

    private fun createLocationDB(lat: Double, lng: Double) {
        val userInfo = Utils.shared.getUser()
        if (userInfo?.shareLocation == "0") {
            return
        } else {
            val uuId = Utils.shared.autoGenerateId()
            val dateTimeNow = Utils.shared.getDateTimeNow()
            val dbReference =
                FirebaseDatabase.getInstance().getReference(TableKey.LOCATIONS.key).child(uuId)

            val hasMap: HashMap<String, String> = HashMap()
            hasMap[LocationKey.LOCATION_ID.key] = uuId
            hasMap[LocationKey.COORDINATE.key] = "$lat, $lng"
            hasMap[LocationKey.CREATE_AT.key] = dateTimeNow
            hasMap[LocationKey.USER_ID.key] = userInfo?.userId ?: ""
            hasMap[LocationKey.USERNAME.key] = userInfo?.userName ?: ""

            dbReference.setValue(hasMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // do if success
                }
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

    private fun addMarkerMap(
        userLocationDTO: UserLocationDTO,
        zIndex: Float,
    ) {
        rootView.btnHome.post {
            mMap?.addMarker(
                MarkerOptions()
                    .position(userLocationDTO.location)
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            createMarker(userLocationDTO.userName, userLocationDTO.isOnline)!!
                        )
                    )
                    .zIndex(zIndex)
            )
        }
    }

    private fun createMarker(userName: String, isOnline: String): Bitmap? {
        val view: View =
            (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.layout_custom_marker,
                null
            )
        val tvName = view.findViewById<TextView>(R.id.tvUserName)
        val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
        tvName.text = userName
        if (isOnline == "0") {
            tvStatus.setBackgroundResource(R.drawable.ic_custom_offline)
        } else tvStatus.setBackgroundResource(R.drawable.ic_custom_online)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        val bitmap =
            Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    private fun updateMyLocation(latLng: LatLng) {
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
    }

    @SuppressLint("MissingPermission")
    private fun createLocationRequest() {
        // request get location
        val mLocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TIME_UPDATE_LOCATION)
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
        setDB()
        setButtonSelect()
        setListener()
    }

    fun setLogout(isLogout: Boolean) {
        this.typeCheckLogout = isLogout
    }

    private fun setListener() {
        with(rootView) {
            btnFriend.clickWithDebounce {
                addFragmentToBackstack(android.R.id.content, AddFriendsFragment.newInstance())
            }
            btnSearch.clickWithDebounce {
                addFragmentToBackstack(
                    android.R.id.content,
                    SearchFragment.newInstance(ArrayList(listAllUser))
                )
            }
        }
    }

    private fun setDB() {
        fbUser = FirebaseAuth.getInstance().currentUser

        //get list user
        val dbReference = FirebaseDatabase.getInstance().getReference(TableKey.USERS.key)
        val dbReference2 = FirebaseDatabase.getInstance().getReference(TableKey.LOCATIONS.key)
        val dbReference3 = FirebaseDatabase.getInstance().getReference(TableKey.FRIENDS.key)

        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listAllUser.clear()
                for (dataSnap: DataSnapshot in snapshot.children) {
                    val user = dataSnap.getValue(UserInfo::class.java)
                    //check not me
                    if (user?.userId.equals(fbUser?.uid)) {
                        val gson = Gson()
                        val json = gson.toJson(user)
                        UserDefaults.standard.setSharedPreference(Constants.CURRENT_USER, json)
                        if (!typeCheckLogout) {
                            Log.d(TAG, "onDestroy: 3")
                            user?.online = "1"
                            dbReference.child(user?.userId ?: "").setValue(user)
                        }
                        //Log.d(TAG, "CURRENT_USER main: save userinfo")
                    } else {
                        listAllUser.add(user)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        dbReference2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listUserLocation.clear()
                for (dataSnap: DataSnapshot in snapshot.children) {
                    val userLocation = dataSnap.getValue(Location::class.java)
                    //check not me
                    if (!userLocation?.userId.equals(fbUser?.uid)) {
                        listUserLocation.add(userLocation)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        dbReference3.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listUserFriend.clear()
                for (dataSnap: DataSnapshot in snapshot.children) {
                    val friend = dataSnap.getValue(Friends::class.java)
                    //check not me
                    if (friend?.userId.equals(fbUser?.uid) || friend?.receiverId.equals(fbUser?.uid)) {
                        if (friend?.friend == "2") {
                            listUserFriend.add(friend)
                        }
                    }
                }
                checkShowLocationFriend()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun checkShowLocationFriend() {
        var mPos = 0
        val mListLocationDTO: MutableList<UserLocationDTO> = mutableListOf()
        listUserFriend.forEachIndexed { indexF, friends ->
            mPos++
            mListLocationDTO.clear()
            listUserLocation.forEachIndexed { indexL, location ->
                if (friends?.userId == location?.userId || friends?.receiverId == location?.userId) {
                    val mListFilterUser = listAllUser.filter {
                        it?.userId == location?.userId
                    }
                    val split = location?.coordinate?.split(", ")
                    mListLocationDTO.add(
                        UserLocationDTO(
                            userName = location?.userName ?: "",
                            location = LatLng(
                                (split?.get(0)?.toDouble() ?: 0.0),
                                (split?.get(1)?.toDouble() ?: 0.0)
                            ),
                            createAt = location?.createAt ?: "",
                            isOnline = mListFilterUser[0]?.online ?: "0"
                        )
                    )
                }
            }
            val sortList = mListLocationDTO.sortedByDescending {
                it.createAt
            }
            if (sortList.isNotEmpty()) {
                addMarkerMap(
                    sortList.first(),
                    zIndex = mPos.toFloat()
                )
            }
            //Log.d(TAG, "checkShowLocationFriend: $sortList")
        }
    }

    override fun onStart() {
        super.onStart()
        typeCheckLogout = false
    }

    override fun onDestroy() {
        //Log.d(TAG, "onDestroy: 1")
        //Log.d(TAG, "onDestroy: $typeCheckLogout")
        if (!typeCheckLogout) {
            typeCheckLogout = true
            val user = Utils.shared.getUser()
            user?.online = "0"
            //Log.d(TAG, "onDestroy: 1.5")
            FirebaseDatabase.getInstance().getReference(TableKey.USERS.key).child("${user?.userId}")
                .setValue(user)
        }
        super.onDestroy()
        //Log.d(TAG, "onDestroy: 2")
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
                        mListTV[0].setTextColor(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.white
                            )
                        )
                        mListImg[0].setImageResource(R.drawable.ic_home_white)
                    }

                    1 -> {
                        mListLinear[1].setBackgroundResource(R.drawable.bg_unselected_white_border)
                        mListTV[1].setTextColor(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.black
                            )
                        )
                        mListImg[1].setImageResource(R.drawable.ic_chat_black)
                    }

                    2 -> {
                        mListLinear[2].setBackgroundResource(R.drawable.bg_unselected_white_border)
                        mListTV[2].setTextColor(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.black
                            )
                        )
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
                            val fragment = MenuFragment.newInstance()
                            fragment.onBackEvent = {
                                setHomeButton()
                            }
                            addFragmentToBackstack(
                                android.R.id.content,
                                fragment
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
        checkShowLocationFriend()
        checkPermission {
            mMap?.apply {
                isMyLocationEnabled = true
                uiSettings.isMyLocationButtonEnabled = false
            }
            createLocationRequest()
        }
    }
}