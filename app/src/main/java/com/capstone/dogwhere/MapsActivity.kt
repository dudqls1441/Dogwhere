package com.capstone.dogwhere

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.capstone.dogwhere.DTO.Matching_MapList_Item
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class MapsActivity : FragmentActivity(), OnMapReadyCallback,
    OnCameraMoveListener, OnMarkerClickListener, OnInfoWindowClickListener {
    var selectedMarker: Marker? = null
    var marker_root_view: View? = null
    var tv_marker: TextView? = null
    var map: GoogleMap? = null
    var mLM: LocationManager? = null
    val db = Firebase.firestore
    var mProvider = LocationManager.NETWORK_PROVIDER
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
//        center_marker.bringToFront()
        mLM = getSystemService(LOCATION_SERVICE) as LocationManager
        val fragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        fragment!!.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.mapType = MAP_TYPE_NORMAL
        map!!.setOnInfoWindowClickListener(this)
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
        map!!.setInfoWindowAdapter(
            CustomInfoWindowAdapter(
                window = layoutInflater.inflate(
                    R.layout.custom_marker_window_info,
                    null
                )
            )
        )
        val mylocation = getMyLocation()
        map!!.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    mylocation.latitude,
                    mylocation.longitude
                ), 14f
            )
        )
        map!!.isMyLocationEnabled = true
        map!!.uiSettings.isCompassEnabled = true
        map!!.uiSettings.isZoomControlsEnabled = true
        map!!.setOnCameraMoveListener(this)
        map!!.setOnMarkerClickListener(this)

        setCustomMarkerView()
        db.collection("Matching").get()
            .addOnSuccessListener { result ->
                val matchingList: ArrayList<Matching_MapList_Item> = ArrayList()
                for (document in result) {
                    val matchingData = document.toObject<Matching_MapList_Item>()
                    matchingList.add(matchingData)
                }
                for (markerItem in matchingList) {
                    addMarker(markerItem, false)
                }

            }
    }

    override fun onActionModeFinished(mode: ActionMode?) {
        super.onActionModeFinished(mode)

    }



    override fun onInfoWindowClick(marker: Marker) {
        Toast.makeText(this, "markeroption click", Toast.LENGTH_SHORT).show()
        marker.position
    }

    var mHandler = Handler(Looper.getMainLooper())
    override fun onMarkerClick(marker: Marker): Boolean {
        val center = CameraUpdateFactory.newLatLng(marker.position)
        map!!.animateCamera(center)
        Toast.makeText(this, marker.title, Toast.LENGTH_SHORT).show()
        mHandler.postDelayed({ marker.showInfoWindow() }, 500)
        return true
    }

    private fun addMarker(markerItem: Matching_MapList_Item, isSelectedMarker: Boolean): Marker {
        val position = LatLng(markerItem.latitude, markerItem.longitude)
        //마커 선택 여부
        if (isSelectedMarker) {
            tv_marker?.setBackgroundResource(R.drawable.ic_mappoint)
            tv_marker?.setTextColor(Color.WHITE)
        } else {
            tv_marker?.setBackgroundResource(R.drawable.ic_mappoint)
            tv_marker?.setTextColor(Color.BLACK)
        }
        val markerOptions = MarkerOptions()
        markerOptions.title(markerItem.title)
        markerOptions.snippet(markerItem.place)
        markerOptions.position(position)
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                createDrawableFromView(
                    this,
                    marker_root_view
                )
            )
        )
        return map!!.addMarker(markerOptions)
    }

//    private fun addMarker(marker: Marker, isSelectedMarker: Boolean): Marker {
//        val lat = marker.position.latitude
//        val lon = marker.position.longitude
//        val price = marker.title.toInt()
//        val temp = MarkerItem(lat, lon, price)
//        return addMarker(temp, isSelectedMarker)
//    }

    // View를 Bitmap으로 변환
    private fun createDrawableFromView(context: Context, view: View?): Bitmap {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        view?.setLayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        view?.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view?.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view?.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(
            view!!.measuredWidth,
            view!!.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.marker_layout, null)
    }

//    private val matchingMarkerItems: Unit
//        private get() {
//
//            val matchingList: ArrayList<MarkerItem> = ArrayList()
//            matchingList.add(MarkerItem(37.538523, 126.96568, 2500000))
//            matchingList.add(MarkerItem(37.527523, 126.96568, 100000))
//            matchingList.add(MarkerItem(37.549523, 126.96568, 15000))
//            matchingList.add(MarkerItem(37.538523, 126.95768, 5000))
//            for (markerItem in matchingList) {
//                addMarker(markerItem, false)
//            }
//        }

//    private fun changeSelectedMarker(marker: Marker?) {
//        // 선택했던 마커 되돌리기
//        if (selectedMarker != null) {
//            addMarker(selectedMarker!!, false)
//            selectedMarker!!.remove()
//        }
//
//        // 선택한 마커 표시
//        if (marker != null) {
//            selectedMarker = addMarker(marker, true)
//            marker.remove()
//        }
//    }

    override fun onCameraMove() {
        val position = map!!.cameraPosition
        val target = position.target
        val projection = map!!.projection

//        addMarker(target.latitude, target.longitude, "my title")
    }

    override fun onStart() {
        super.onStart()
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
        val location = mLM!!.getLastKnownLocation(mProvider)
        if (location != null) {
            mListener.onLocationChanged(location)
        }
        mLM!!.requestSingleUpdate(mProvider, mListener, null)

    }

    override fun onStop() {
        super.onStop()
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
        mLM!!.removeUpdates(mListener)
    }

    private fun moveMap(lat: Double, lng: Double) {
        if (map != null) {
            val latLng = LatLng(lat, lng)
            val position = CameraPosition.Builder()
                .target(latLng)
                .bearing(30f)
                .tilt(45f)
                .zoom(17f)
                .build()
            val update = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            //            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            map!!.moveCamera(update)
            //        map.animateCamera(update);
        }
    }

    var mListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            moveMap(location.latitude, location.longitude)
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
        override fun onProviderEnabled(s: String) {}
        override fun onProviderDisabled(s: String) {}
    }


    @SuppressLint("MissingPermission")
    private fun getMyLocation(): LatLng {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled: Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled: Boolean = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        when {
            isNetworkEnabled -> {
                val location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                val getLongtitude = location?.longitude
                val getLatitude = location?.latitude
                Log.e(
                    "joo",
                    "GPSEnabled - 경도 :${getLatitude.toString()}  위도 :${getLongtitude.toString()}"
                )
                var currentLocation = LatLng(getLongtitude!!, getLatitude!!)

                return currentLocation
            }
            isGPSEnabled -> {
                val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val getLongtitude = location?.longitude
                val getLatitude = location?.latitude
                Log.e(
                    "joo",
                    "GPSEnabled - 경도 :${getLatitude.toString()}  위도 :${getLongtitude.toString()}"
                )

                var currentLocation = LatLng(getLongtitude!!, getLatitude!!)

                return currentLocation
            }

            else -> {
                Toast.makeText(
                    this,
                    "GPS 권한 오류",
                    Toast.LENGTH_SHORT
                ).show()
                return LatLng(37.537523, 126.96558)
            }
        }
    }


}
