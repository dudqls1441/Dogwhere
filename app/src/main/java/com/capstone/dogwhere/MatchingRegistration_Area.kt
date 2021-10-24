package com.capstone.dogwhere

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ActionMode
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_google_map.*

class MatchingRegistration_Area : AppCompatActivity(), OnMapReadyCallback,
    OnCameraMoveListener, OnMapClickListener, OnMarkerClickListener, OnInfoWindowClickListener,
    OnMarkerDragListener {
    var map: GoogleMap? = null
    var mLM: LocationManager? = null
    var mProvider = LocationManager.NETWORK_PROVIDER
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)
        linear_map_marker.bringToFront()
//        center_marker.bringToFront()
        mLM = getSystemService(LOCATION_SERVICE) as LocationManager
        val fragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment?
        fragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.mapType = MAP_TYPE_NORMAL
        //        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        map.setIndoorEnabled(true);
//        map.setBuildingsEnabled(true);
//        map.setTrafficEnabled(true);
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

        val mylocation = getMyLocation()
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mylocation.latitude, mylocation.longitude), 14f))
        map!!.isMyLocationEnabled = true
        map!!.uiSettings.isCompassEnabled = true
        map!!.uiSettings.isZoomControlsEnabled = true
        map!!.setOnCameraMoveListener(this)
        map!!.setOnMapClickListener(this)
        map!!.setOnMarkerClickListener(this)
        map!!.setOnInfoWindowClickListener(this)
        map!!.setOnMarkerDragListener(this)

//        val position = map!!.cameraPosition
//        val target = position.target
//
//        target.latitude
//        target.longitude
//        btn_map_add.setOnClickListener {
//            val intent = Intent(this,MatchingRegistrationActivity::class.java)
//            Log.e("joo", target.toString())
//            intent.putExtra("choice_lat", target.latitude)
//            intent.putExtra("choice_lon", target.longitude)
//            setResult(Activity.RESULT_OK,intent)
//            finish()
//        }
    }

    override fun onActionModeFinished(mode: ActionMode?) {
        super.onActionModeFinished(mode)

    }
    override fun onMarkerDragStart(marker: Marker) {}
    override fun onMarkerDrag(marker: Marker) {}
    override fun onMarkerDragEnd(marker: Marker) {
        val latLng = marker.position
        Log.e("joo", "lat : " + latLng.latitude + ", lng : " + latLng.longitude)
    }

    override fun onInfoWindowClick(marker: Marker) {
        marker.hideInfoWindow()
    }

    var mHandler = Handler(Looper.getMainLooper())
    override fun onMarkerClick(marker: Marker): Boolean {
        Toast.makeText(this, marker.title, Toast.LENGTH_SHORT).show()
        mHandler.postDelayed({ marker.showInfoWindow() }, 2000)
        return true
    }

    override fun onMapClick(latLng: LatLng) {
//        addMarker(latLng.latitude, latLng.longitude, "My marker")
    }

    var marker: Marker? = null
    private fun addMarker(lat: Double, lng: Double, title: String) {
        if (marker != null) {
            marker!!.remove()
            marker = null
        }
        val options = MarkerOptions()
        options.position(LatLng(lat, lng))
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        options.anchor(0.5f, 1f)
        options.title(title)
        options.snippet("snippet - $title")
        options.draggable(true)
        marker = map!!.addMarker(options)
    }

    override fun onCameraMove() {
        btn_map_add.setOnClickListener {
            val position = map!!.cameraPosition

            val target = position.target
            val projection = map!!.projection
            val intent = Intent(this,MatchingRegistrationActivity::class.java)
            Log.e("joo", target.toString())
            intent.putExtra("choice_lat", target.latitude)
            intent.putExtra("choice_lon", target.longitude)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
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
                Log.e("joo", "GPSEnabled - 경도 :${getLatitude.toString()}  위도 :${getLongtitude.toString()}")
                var currentLocation = LatLng(getLongtitude!!, getLatitude!!)

                return currentLocation
            }
            isGPSEnabled -> {
                val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val getLongtitude = location?.longitude
                val getLatitude = location?.latitude
                Log.e("joo", "GPSEnabled - 경도 :${getLatitude.toString()}  위도 :${getLongtitude.toString()}")

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