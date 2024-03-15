package com.finals.friendsfinder.views.home.menu

import android.os.Bundle
import android.view.LayoutInflater
import com.blankj.utilcode.util.PermissionUtils
import com.finals.friendsfinder.R
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentMapFriendBinding
import com.finals.friendsfinder.databinding.FragmentMyAccountBinding
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.utilities.commons.TableKey
import com.finals.friendsfinder.views.friends.data.Location
import com.finals.friendsfinder.views.friends.data.UserLocationDTO
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MapFriendFragment : BaseFragment<FragmentMapFriendBinding>(), OnMapReadyCallback {

    companion object {
        fun newInstance(userLocationDTO: UserLocationDTO): MapFriendFragment {
            val arg = Bundle().apply {
                putParcelable("USER_INFO", userLocationDTO)
            }
            return MapFriendFragment().apply {
                arguments = arg
            }
        }
    }

    private var userLocationDTO: UserLocationDTO? = null
    private var mMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private var listUserLocation: MutableList<Location?> = mutableListOf()
    private val myFinishMarkerOptions: MarkerOptions by lazy {
        val marker = MarkerOptions()
        val bitmapCar = BitmapDescriptorFactory.fromResource(R.drawable.icon_finish_schedule)
        marker.icon(bitmapCar)
        marker.title("Điểm")
    }

    override fun observeHandle() {
        super.observeHandle()
        arguments?.let {
            userLocationDTO = it.getParcelable("USER_INFO")
        }
        //val currentUserId = BaseAccessToken.accessToken
        val dbReference2 = FirebaseDatabase.getInstance().getReference(TableKey.LOCATIONS.key)
        dbReference2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listUserLocation.clear()
                for (dataSnap: DataSnapshot in snapshot.children) {
                    val userLocation = dataSnap.getValue(Location::class.java)
                    //check not me
                    listUserLocation.add(userLocation)
                }
                drawMarker()
            }

            override fun onCancelled(error: DatabaseError) {
                listUserLocation.clear()
            }

        })
    }

    override fun bindData() {
        super.bindData()
        setText()
        setListener()
    }

    override fun setupView() {
        super.setupView()
        initMap()
    }

    private fun initMap() {
        mapFragment = activity?.supportFragmentManager
            ?.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun setListener() {
        with(rootView) {
            layoutHeader.imgBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }

    private fun setText() {
        with(rootView) {
            layoutHeader.tvMessage.text = "Map"
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

    private fun drawMarker() {
        val locations = mutableListOf<LatLng>()
        listUserLocation.forEach {
            if (it?.userId == userLocationDTO?.userId) {
                val split = it?.coordinate?.split(",")
                if ((split?.size ?: 0) >= 2) {
                    val lat = split?.get(0)?.toDouble() ?: 0.0
                    val lon = split?.get(1)?.toDouble() ?: 0.0
                    locations.add(LatLng(lat, lon))
                }
            }
        }
        mMap?.uiSettings?.isZoomControlsEnabled = true
        if (locations.size > 0) {
            val size = locations.size
            for (i in 0 until size) {
//            for (place in locations!!) {
                val place = locations[i]
                if (i >= (size - 1)) {
                    place.let { myFinishMarkerOptions.position(it) }.let { markerOption ->
                        mMap?.addMarker(markerOption)
                    }
                } else {
                    place.let { MarkerOptions().position(it) }.let { markerOption ->
                        mMap?.addMarker(
                            markerOption
                        )
                    }
                }
            }
            val options = PolylineOptions()
            options.color(requireContext().getColor(R.color.color_btn_blue))
            options.width(10f)
            options.addAll(locations)
            mMap?.addPolyline(options)
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(locations.last(), 16f))
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentMapFriendBinding {
        return FragmentMapFriendBinding.inflate(inflater)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        checkShowLocationFriend()
        checkPermission {
            mMap?.apply {
                isMyLocationEnabled = true
                uiSettings.isMyLocationButtonEnabled = true
            }
        }
    }
}