package com.capstone.dogwhere

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.capstone.dogwhere.Map.MarkerItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : FragmentActivity(), OnMapReadyCallback,
    OnMarkerClickListener, OnMapClickListener {
    var selectedMarker: Marker? = null
    var marker_root_view: View? = null
    var tv_marker: TextView? = null
    private var mMap: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.537523, 126.96558), 14f))
        mMap!!.setOnMarkerClickListener(this)
        mMap!!.setOnMapClickListener(this)
        setCustomMarkerView()
        sampleMarkerItems
    }

    private fun setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.marker_layout, null)
    }

    private val sampleMarkerItems: Unit
        private get() {
            val sampleList: ArrayList<MarkerItem> = ArrayList()
            sampleList.add(MarkerItem(37.538523, 126.96568, 2500000))
            sampleList.add(MarkerItem(37.527523, 126.96568, 100000))
            sampleList.add(MarkerItem(37.549523, 126.96568, 15000))
            sampleList.add(MarkerItem(37.538523, 126.95768, 5000))
            for (markerItem in sampleList) {
                addMarker(markerItem, false)
            }
        }

    private fun addMarker(markerItem: MarkerItem, isSelectedMarker: Boolean): Marker {
        val position = LatLng(markerItem.lat, markerItem.lon)
        if (isSelectedMarker) {
            tv_marker?.setBackgroundResource(R.drawable.ic_mappoint)
            tv_marker?.setTextColor(Color.WHITE)
        } else {
            tv_marker?.setBackgroundResource(R.drawable.ic_mappoint)
            tv_marker?.setTextColor(Color.BLACK)
        }
        val markerOptions = MarkerOptions()
//        markerOptions.title(Integer.toString(price))
        markerOptions.position(position)
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                createDrawableFromView(
                    this,
                    marker_root_view
                )
            )
        )
        return mMap!!.addMarker(markerOptions)
    }

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

    private fun addMarker(marker: Marker, isSelectedMarker: Boolean): Marker {
        val lat = marker.position.latitude
        val lon = marker.position.longitude
        val price = marker.title.toInt()
        val temp = MarkerItem(lat, lon, price)
        return addMarker(temp, isSelectedMarker)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val center = CameraUpdateFactory.newLatLng(marker.position)
        mMap!!.animateCamera(center)
        changeSelectedMarker(marker)
        return true
    }

    private fun changeSelectedMarker(marker: Marker?) {
        // 선택했던 마커 되돌리기
        if (selectedMarker != null) {
            addMarker(selectedMarker!!, false)
            selectedMarker!!.remove()
        }

        // 선택한 마커 표시
        if (marker != null) {
            selectedMarker = addMarker(marker, true)
            marker.remove()
        }
    }

    override fun onMapClick(latLng: LatLng) {
        changeSelectedMarker(null)
    }
}
