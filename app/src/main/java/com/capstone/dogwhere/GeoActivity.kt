package com.capstone.dogwhere

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_geo.*

class GeoActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = GeoActivity::class.java.simpleName
    private val gps_request_code=1000
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //위치 요청 메소드 담고 있는 객체
    private lateinit var locationRequest: LocationRequest

    //위치 요청할 때 넘겨주는 데이터에 관한 객체
    private lateinit var locationCallback: MyLocationCallBack
    //위치 확인되고 호출되는 객체

    private val polyLineOptions = PolylineOptions().width(5f).color(Color.RED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geo)


        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapfragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //프래그먼트 매니저로부터 supportMapFragment 프로그래먼트를 얻는다.-> 지도를 준비하는 기능


        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //화면이 꺼지지 않게 하기
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //세로 모드로 화면 고정

        initLocation()


        btn_mylocation.setOnClickListener {
            val isGPSEnabled: Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled: Boolean = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    0
                )
            } else {
                when {
                    isNetworkEnabled -> {
                        val location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        val getLongtitude = location?.longitude
                        val getLatitude = location?.latitude
                        Toast.makeText(
                            this,
                            "현재위치를 불러옵니다." + getLongtitude.toString() + "<-위도 경도 ->" + getLatitude.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    isGPSEnabled -> {
                        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        val getLongtitude = location?.longitude
                        val getLatitude = location?.latitude
                        Toast.makeText(
                            this,
                            "현재위치를 불러옵니다." + getLongtitude.toString() + "<-위도 경도 ->" + getLatitude.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
//
//                lm.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER,
//                    gps_request_code,
//                    1F,
//                    gpsLocationListener
//                )
            }
        }
//        lm.allProviders(gpsLocationListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            1000 ->{
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    addLocationListener()
                else{
                    Toast.makeText(this, "권한거부", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private val gpsLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            val provider: String = location?.provider.toString()
            val longitude: Double = location!!.longitude
            val latitude: Double = location!!.latitude
            val altitude: Double = location!!.altitude
        }
    }


    //googleMap 객체 생성
    //googleMap을 이용하여 마커나 카메라, 선 등을 조정하는 것이다.
    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        val seoul = LatLng(37.5134380, 126.7073128)
        mMap.addMarker(MarkerOptions().position(seoul).title("우리집"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10f))

    }

    private fun initLocation() {
        // 위치 정보를 얻기 위한 각종 초기화
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        locationCallback = MyLocationCallBack()
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        //가장 정확한 위치를 요청
        locationRequest.interval = 10000
        //위치를 갱신하는데 필요한 시간 <밀리초>
        locationRequest.fastestInterval = 5000
        //다른 앱에서 위치를 갱신했을 때 그 정보를 가져오는 시간 <밀리초?
    }

    override fun onResume() {
        //프로그램이 켜졌을 때만 위치 정보를 요청한다.
        super.onResume()
        Toast.makeText(this, "이 앱은 GPS(위치)를 켜야 이용 가능합니다!", Toast.LENGTH_SHORT).show()
        addLocationListener()
    }

    override fun onPause() {
        //프로그램이 중단되면 위치 요청을 삭제한다.
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)

    }



    @SuppressLint("MissingPermisson")
    private fun addLocationListener() {
        //위치 정보 요청
        // (정보 요청할 때 넘겨줄 데이터)에 관한 객체, 위치 갱신되면 호출되는 콜백, 특정 스레드 지정(별 일 없으니 null)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "권한없어서 리턴")
            return

        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    inner class MyLocationCallBack : LocationCallback() {
        //위치 정보를 찾고 나서 인스턴스화되는 클래스
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            val location = locationResult?.lastLocation

            if (locationResult == null) {
                //gps가 꺼져있을 때는 null을 반환 -> gps가 켜져 있고 위치 정보를 찾을 수 있을 때
                //다음ㅊ함수를 호출한다 -> <? .: 안전한 호출 >
                return
            } else {
                location?.run {
                    val latLng = LatLng(latitude, longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                    mMap.addMarker(MarkerOptions().position(latLng).title("changedLocation"))
                    polyLineOptions.add(latLng)
                    //선 그리기
                    mMap.addPolyline(polyLineOptions)

                    Log.d(TAG, latLng.toString())
                }

            }

        }

    }

    //    private val REQUEST_ACCESS_FIND_LOCATION=1000
//    private fun permissioncheck(cancel:()->Unit,ok:()->Unit){
//        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
//                cancel()
//            }else{
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_ACCESS_FIND_LOCATION)
//            }
//        }else{
//            ok()
//        }
//    }


}

