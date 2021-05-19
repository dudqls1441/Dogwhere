package com.capstone.dogwhere

import android.Manifest
import android.R.id.button1
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.android.synthetic.main.activity_weather.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.util.*


val num_of_rows = 10
val page_no = 1
val data_type = "JSON"
val base_time = 1100
val base_data = 20210519
val nx = "59"
val ny = "123"


var POP : String? = null
var SKY : String? = null
var T3H : String? = null


//////////////////////////////////
//
//var locationManager : LocationManager? = null
//private val REQUEST_CODE_LOCATION : Int = 2
//var currentLocation : String = ""
//var latitude : Double? = null
//var longitude : Double? = null
//
//private fun getCurrentLoc() {
//    locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
//    var userLocation: Location = getLatLng()
//    if (userLocation != null) {
//        latitude = userLocation.latitude
//        longitude = userLocation.longitude
//        Log.d("CheckCurrentLocation", "현재 내 위치 값: $latitude, $longitude")
//
//        var mGeocoder = Geocoder(applicationContext, Locale.KOREAN)
//        var mResultList: List<Address>? = null
//        try {
//            mResultList = mGeocoder.getFromLocation(
//                latitude!!, longitude!!, 1
//            )
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        if (mResultList != null) {
//            Log.d("CheckCurrentLocation", mResultList[0].getAddressLine(0))
//            currentLocation = mResultList[0].getAddressLine(0)
//            currentLocation = currentLocation.substring(11)
//        }
//    }
//}
//
//private fun getLatLng() : Location {
//    var currentLatLng: Location? = null
//    if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//        && ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), this.REQUEST_CODE_LOCATION)
//        getLatLng()
//    } else {
//        val locationProvider = LocationManager.GPS_PROVIDER
//        currentLatLng = locationManager?.getLastKnownLocation(locationProvider)
//    }
//    return currentLatLng!!
//}

////////////////////////////////






data class WEATHER(
    val response: RESPONSE
)
data class RESPONSE(
    val header: HEADER,
    val body: BODY
)
data class HEADER(
    val resultCode: Int,
    val resultMsg: String
)
data class BODY(
    val dataType: String,
    val items: ITEMS
)
data class ITEMS(
    val item: List<ITEM>
)
data class ITEM(
    val baseData: Int,
    val baseTime: Int,
    val category: String,
    val fcstValue: String
)

interface WeatherInterface {
    @GET("getVilageFcst?serviceKey=GN%2Br4yL8PgIgO%2FK5aqch2TVxK6opk2CdyjKj9Xm6cTb%2FPf6kLaZb8FbXa6tc4zQbWWAL8ih93ZCn4go9pvOUvg%3D%3D")
    fun GetWeather(
        @Query("dataType") data_type: String,
        @Query("numOfRows") num_of_rows: Int,
        @Query("pageNo") page_no: Int,
        @Query("base_date") base_date: Int,
        @Query("base_time") base_time: Int,
        @Query("nx") nx: String,
        @Query("ny") ny: String
    ): Call<WEATHER>
}


private val retrofit = Retrofit.Builder()
    .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService/") // 마지막 / 반드시 들어가야 함
    .addConverterFactory(GsonConverterFactory.create()) // converter 지정
    .build() // retrofit 객체 생성

object ApiObject {
    val retrofitService: WeatherInterface by lazy {
        retrofit.create(WeatherInterface::class.java)
    }
}

@SuppressLint("MissingPermission")
class WeatherActivity : AppCompatActivity() {
//    val lm : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    val location : Location? = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//
//    val gpsLocationListener: LocationListener = object : LocationListener {
//        override fun onLocationChanged(location: Location) {
//            val provider = location.provider
//            val longitude = location.longitude
//            val latitude = location.latitude
//            val altitude = location.altitude
//            testtext1.setText(
//                """
//                위치정보 : $provider
//                위도 : $longitude
//                경도 : $latitude
//                고도  : $altitude
//                """.trimIndent()
//            )
//        }
//
//        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
//
//    }


    val gpsLocationListener: LocationListener = object : LocationListener{
        override fun onLocationChanged(location: Location) {
            val provider: String = location.getProvider()
            val longitude: Double = location.getLongitude()
            val latitude: Double = location.getLatitude()
            val altitude: Double = location.getAltitude()
            testtext1.setText(
                """
                위치정보 : $provider
                위도 : $longitude
                경도 : $latitude
                고도  : $altitude
                """.trimIndent()
            )
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        val lm: LocationManager? = getSystemService(LOCATION_SERVICE) as LocationManager

        testbtn1.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@WeatherActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        0
                    )
                } else {
                    val location = lm!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    val provider = location!!.provider
                    val longitude = location!!.longitude
                    val latitude = location!!.latitude
                    val altitude = location!!.altitude
                    Log.e("api",                        """
                    위치정보 : $provider
                    위도 : $longitude
                    경도 : $latitude
                    고도  : $altitude
                    """.trimIndent())
                    testtext1.setText(
                        """
                    위치정보 : $provider
                    위도 : $longitude
                    경도 : $latitude
                    고도  : $altitude
                    """.trimIndent()
                    )
                    lm!!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000, 1f,
                        gpsLocationListener
                    )
                    lm!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        1000, 1f,
                        gpsLocationListener
                    )
                }
            }
        })

        val call = ApiObject.retrofitService.GetWeather(
            data_type,
            num_of_rows,
            page_no,
            base_data,
            base_time,
            nx,
            ny
        )
        call.enqueue(object : retrofit2.Callback<WEATHER> {
            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                if (response.isSuccessful) {
                    Log.d("api", response.body().toString())
                    Log.d("api", response.body()!!.response.body.items.item.toString())
                    Log.d("api", response.body()!!.response.body.items.item[0].category)
                    Log.d("api", response.body()!!.response.body.items.item[0].fcstValue)
                    val res = response.body()!!.response.body.items.item[1].fcstValue
                    POP = response.body()!!.response.body.items.item[0].fcstValue.toString()
                    SKY = response.body()!!.response.body.items.item[3].fcstValue.toString()
                    T3H = response.body()!!.response.body.items.item[4].fcstValue.toString()
                    Log.d("api", "POP = " + POP + " SKY = " + SKY + " T3H = " + T3H)
                }
            }


            override fun onFailure(call: Call<WEATHER>, t: Throwable) {
            }
        })

        testbtn.setOnClickListener {
            if(SKY.equals("1")){
                SKY = "맑음"
            }else if(SKY.equals("3")){
                SKY = "구름많음"
            }else{
                SKY = "흐림"
            }
            testtext.setText("강수확률=" + POP + "%  SKY=" + SKY + "  기온=" + T3H + "°c")
            Toast.makeText(
                this.applicationContext,
                "강수확률 = " + POP + "% SKY = " + SKY + " 기온 = " + T3H,
                Toast.LENGTH_LONG
            ).show()
        }


    }
}

