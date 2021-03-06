//package com.capstone.dogwhere
//
//import BBS_CommonBBS
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.*
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.content.ContextCompat.getSystemService
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.capstone.dogwhere.DTO.BBS_Common
//import kotlinx.android.synthetic.main.activity_weather.*
//import retrofit2.Call
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.GET
//import retrofit2.http.Query
//import java.io.IOException
//import java.util.*
//import kotlin.collections.ArrayList
//
//
//var TO_GRID = 0
//var TO_GPS = 1
//val num_of_rows = 10
//val page_no = 1
//val data_type = "JSON"
//val base_time = 1100
//val base_data = 20210522
//var nx = ""
//var ny = ""
//
//
//var POP : String? = null
//var SKY : String? = null
//var T3H : String? = null
//
//
//
//data class WEATHER(
//    val response: RESPONSE
//)
//data class RESPONSE(
//    val header: HEADER,
//    val body: BODY
//)
//data class HEADER(
//    val resultCode: Int,
//    val resultMsg: String
//)
//data class BODY(
//    val dataType: String,
//    val items: ITEMS
//)
//data class ITEMS(
//    val item: List<ITEM>
//)
//data class ITEM(
//    val baseData: Int,
//    val baseTime: Int,
//    val category: String,
//    val fcstValue: String
//)
//
//interface WeatherInterface {
//    @GET("getVilageFcst?serviceKey=GN%2Br4yL8PgIgO%2FK5aqch2TVxK6opk2CdyjKj9Xm6cTb%2FPf6kLaZb8FbXa6tc4zQbWWAL8ih93ZCn4go9pvOUvg%3D%3D")
//    fun GetWeather(
//        @Query("dataType") data_type: String,
//        @Query("numOfRows") num_of_rows: Int,
//        @Query("pageNo") page_no: Int,
//        @Query("base_date") base_date: Int,
//        @Query("base_time") base_time: Int,
//        @Query("nx") nx: String,
//        @Query("ny") ny: String
//    ): Call<WEATHER>
//}
//
//
//private val retrofit = Retrofit.Builder()
//    .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService/") // ????????? / ????????? ???????????? ???
//    .addConverterFactory(GsonConverterFactory.create()) // converter ??????
//    .build() // retrofit ?????? ??????
//
//object ApiObject {
//    val retrofitService: WeatherInterface by lazy {
//        retrofit.create(WeatherInterface::class.java)
//    }
//}
//
//
//class HomeWorkIndexFragment(var tab:String) : Fragment() {
//
//    lateinit var information: ArrayList<BBS_Common>
//    private val TAG = BBS_CommonBBS::class.java.simpleName
//    private lateinit var recyclerView: RecyclerView
//
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_home_work_index, container, false)
//        fun getCurrentAddress(latitude: Double, longitude: Double): String? {
//
//            //????????????... GPS??? ????????? ??????
//            val geocoder = Geocoder(view.context, Locale.getDefault())
//            val addresses: List<Address>?
//            addresses = try {
//                geocoder.getFromLocation(
//                    latitude,
//                    longitude,
//                    7
//                )
//            } catch (ioException: IOException) {
//                //???????????? ??????
//                Toast.makeText(view.context, "???????????? ????????? ????????????", Toast.LENGTH_LONG).show()
//                return "???????????? ????????? ????????????"
//            } catch (illegalArgumentException: IllegalArgumentException) {
//                Toast.makeText(view.context, "????????? GPS ??????", Toast.LENGTH_LONG).show()
//                return "????????? GPS ??????"
//            }
//            if (addresses == null || addresses.size == 0) {
//                Toast.makeText(view.context, "?????? ?????????", Toast.LENGTH_LONG).show()
//                return "?????? ?????????"
//            }
//            val address = addresses[0]
//            val splitaddress = address.getAddressLine(0).split(" ")
//            return """
//        ${splitaddress[2]} ${splitaddress[3]}
//
//        """.trimIndent()
//        }
//
//        val gpsLocationListener: LocationListener = object : LocationListener {
//            override fun onLocationChanged(location: Location) {
//                val provider: String = location.getProvider()
//                val longitude: Double = location.getLongitude()
//                val latitude: Double = location.getLatitude()
//                val altitude: Double = location.getAltitude()
//
//            }
//
//            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
//        }
//
//        //GPS ?????? ????????????
//        val lm: LocationManager? = getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
//
//        if (Build.VERSION.SDK_INT >= 23 &&
//            ContextCompat.checkSelfPermission(
//                view.context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this@HomeWorkIndexFragment, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                0
//            )
//        } else {
//            val location = lm!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            val provider = location!!.provider
//            val longitude = location!!.longitude
//            val latitude = location!!.latitude
//            val altitude = location!!.altitude
//            val tmp = convertGRID_GPS(TO_GRID, latitude, longitude)
//            nx = tmp.x.toInt().toString()
//            ny = tmp.y.toInt().toString()
//            Log.e(
//                "api", """
//                    ???????????? : $provider
//                    ?????? : $longitude  x : $nx
//                    ?????? : $latitude  y : $ny
//                    ??????  : $altitude
//                    """.trimIndent()
//            )
//
//            Log.e("api", "x = " + tmp.y + ", y = " + tmp.y)
//            val area = getCurrentAddress(latitude.toDouble(), longitude.toDouble())
////            testtext1.setText(
////                """
////                    ???????????? : $provider
////                    ?????? : $latitude  x : $nx
////                    ?????? : $longitude  y : $ny
////                    ??????  : $altitude
////                    ?????? : $area
////                    """.trimIndent()
////            )
//            area_text1.setText(area.toString())
//
//            lm.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                1000, 1f,
//                gpsLocationListener
//            )
//            lm.requestLocationUpdates(
//                LocationManager.NETWORK_PROVIDER,
//                1000, 1f,
//                gpsLocationListener
//            )
//        }
//
//
//        val call = ApiObject.retrofitService.GetWeather(
//            data_type,
//            num_of_rows,
//            page_no,
//            base_data,
//            base_time,
//            nx,
//            ny
//        )
//        call.enqueue(object : retrofit2.Callback<WEATHER> {
//            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
//                if (response.isSuccessful) {
//                    Log.d("api", response.body().toString())
//                    Log.d("api", response.body()!!.response.body.items.item.toString())
//                    Log.d("api", response.body()!!.response.body.items.item[0].category)
//                    Log.d("api", response.body()!!.response.body.items.item[0].fcstValue)
//                    val res = response.body()!!.response.body.items.item[1].fcstValue
//                    POP = response.body()!!.response.body.items.item[0].fcstValue
//                    SKY = response.body()!!.response.body.items.item[3].fcstValue
//                    T3H = response.body()!!.response.body.items.item[4].fcstValue
//                    Log.d("api", "POP = " + POP + " SKY = " + SKY + " T3H = " + T3H)
//                    if(SKY.equals("1")){
//                        SKY = "??????"
//                    }else if(SKY.equals("3")){
//                        SKY = "????????????"
//                    }else{
//                        SKY = "??????"
//                    }
//                    weather_text1.setText(SKY)
//                    temp_text1.setText(T3H)
////                    testtext.setText("????????????=" + POP + "%  SKY=" + SKY + "  ??????=" + T3H + "??c  X=" + nx + "  Y=" + ny)
//
//                }
//            }
//
//
//            override fun onFailure(call: Call<WEATHER>, t: Throwable) {
//            }
//        })
//
//
//
//
//        return view
//    }
//
//
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//
//    }
//
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//
//
//
//
//
//    }
//}
//
//@SuppressLint("MissingPermission")
//class WeatherActivity : AppCompatActivity() {
//
//
//}
//
//
//
//
//
//
////
//
//
//
//
//
//private fun convertGRID_GPS(mode: Int, lat_X: Double, lng_Y: Double): LatXLngY {
//    val RE = 6371.00877 // ?????? ??????(km)
//    val GRID = 5.0 // ?????? ??????(km)
//    val SLAT1 = 30.0 // ?????? ??????1(degree)
//    val SLAT2 = 60.0 // ?????? ??????2(degree)
//    val OLON = 126.0 // ????????? ??????(degree)
//    val OLAT = 38.0 // ????????? ??????(degree)
//    val XO = 43.0 // ????????? X??????(GRID)
//    val YO = 136.0 // ???1?????? Y??????(GRID)
//
//    //
//    // LCC DFS ???????????? ( code : "TO_GRID"(?????????->??????, lat_X:??????,  lng_Y:??????), "TO_GPS"(??????->?????????,  lat_X:x, lng_Y:y) )
//    //
//    val DEGRAD = Math.PI / 180.0
//    val RADDEG = 180.0 / Math.PI
//    val re = RE / GRID
//    val slat1 = SLAT1 * DEGRAD
//    val slat2 = SLAT2 * DEGRAD
//    val olon = OLON * DEGRAD
//    val olat = OLAT * DEGRAD
//    var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
//    sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
//    var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5)
//    sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
//    var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
//    ro = re * sf / Math.pow(ro, sn)
//    val rs = LatXLngY()
//    if (mode == TO_GRID) {
//        rs.lat = lat_X
//        rs.lng = lng_Y
//        var ra = Math.tan(Math.PI * 0.25 + lat_X * DEGRAD * 0.5)
//        ra = re * sf / Math.pow(ra, sn)
//        var theta = lng_Y * DEGRAD - olon
//        if (theta > Math.PI) theta -= 2.0 * Math.PI
//        if (theta < -Math.PI) theta += 2.0 * Math.PI
//        theta *= sn
//        rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5)
//        rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5)
//    } else {
//        rs.x = lat_X
//        rs.y = lng_Y
//        val xn = lat_X - XO
//        val yn = ro - lng_Y + YO
//        var ra = Math.sqrt(xn * xn + yn * yn)
//        if (sn < 0.0) {
//            ra = -ra
//        }
//        var alat = Math.pow(re * sf / ra, 1.0 / sn)
//        alat = 2.0 * Math.atan(alat) - Math.PI * 0.5
//        var theta = 0.0
//        if (Math.abs(xn) <= 0.0) {
//            theta = 0.0
//        } else {
//            if (Math.abs(yn) <= 0.0) {
//                theta = Math.PI * 0.5
//                if (xn < 0.0) {
//                    theta = -theta
//                }
//            } else theta = Math.atan2(xn, yn)
//        }
//        val alon = theta / sn + olon
//        rs.lat = alat * RADDEG
//        rs.lng = alon * RADDEG
//    }
//    return rs
//}
//
//
//internal class LatXLngY {
//    var lat = 0.0
//    var lng = 0.0
//    var x = 0.0
//    var y = 0.0
//}