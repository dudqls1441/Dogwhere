package com.capstone.dogwhere

import android.Manifest
import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.capstone.dogwhere.Dust.DUST
import com.capstone.dogwhere.Dust.DustInterface
import com.capstone.dogwhere.NearMeasuring.NEARMEASURE
import com.capstone.dogwhere.NearMeasuring.NearInterface
import com.capstone.dogwhere.TMxy.TMInterface
import com.capstone.dogwhere.TMxy.TMxy
import com.capstone.dogwhere.Weather.WEATHER
import com.capstone.dogwhere.Weather.WeatherInterface
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_weather.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

var TO_GRID = 0
var TO_GPS = 1
val num_of_rows = 33
val page_no = 1
val data_type = "JSON"
val fcstTime = currentTime()
val base_time = baseTimeSetting(fcstTime)
var base_date = currentDate()
var nx = ""
var ny = ""

val dataTerm = "Daily"
val ver = "1.3"

// 강수확률
var POP: String? = null

// 하늘 상태
var SKY: String? = null

// 비/눈 여부
var PTY: String? = null

// 온도
var TMP: String? = null

val kakaokey = "KakaoAK eb4a52cbe3a768ea9eb22447d4655f3d"
val output_coord = "TM"


class GpsActivity : Fragment() {
    private var gpsTracker: GpsTracker? = null
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    companion object {
        fun newInstance(): GpsActivity {
            return GpsActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_weather, container, false)
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting()
        } else {
            checkRunTimePermission()
        }
        Log.e("joo", "지금 시간" + fcstTime)
        // gps 값 가져오는 부분
        gpsTracker = GpsTracker(this@GpsActivity)
        val latitude: Double = gpsTracker!!.getLatitude()
        val longitude: Double = gpsTracker!!.getLongitude()
        val tmp = convertGRID_GPS(TO_GRID, latitude, longitude)
        val area = getCurrentAddress(latitude, longitude)
        nx = tmp.x.toInt().toString()
        ny = tmp.y.toInt().toString()

        getDustApiInfo(longitude, latitude)

        getWeatherApiInfo(area)

        return view
    }


    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    override fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permissions: Array<String>,
        grandResults: IntArray
    ) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            var check_result = true


            // 모든 퍼미션을 허용했는지 체크합니다.
            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {

                //위치 값을 가져올 수 있음
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this.requireActivity(),
                        REQUIRED_PERMISSIONS[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this.requireActivity(),
                        REQUIRED_PERMISSIONS[1]
                    )
                ) {
                    Toast.makeText(
                        context,
                        "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    this.requireActivity().finish()
                } else {
                    Toast.makeText(
                        context,
                        "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this.requireActivity(),
                    REQUIRED_PERMISSIONS[0]
                )
            ) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(
                    this.requireContext(),
                    "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                    Toast.LENGTH_LONG
                )
                    .show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this.requireActivity(), REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this.requireActivity(), REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    private fun getCurrentAddress(latitude: Double, longitude: Double): String {

        //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>?
        addresses = try {
            geocoder.getFromLocation(
                latitude,
                longitude,
                7
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(context, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(context, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }
        if (addresses == null || addresses.size == 0) {
            Toast.makeText(context, "주소 미발견", Toast.LENGTH_LONG).show()
            return "주소 미발견"
        }
        val address: Address = addresses[0]
        Log.e("joo", address.getAddressLine(0).toString())
        val area = address.getAddressLine(0).toString()
        val arealist = area.split(" ")
        return "${arealist[1]} ${arealist[2]} ${arealist[3]}의 날씨"
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
                앱을 사용하기 위해서는 위치 서비스가 필요합니다.
                위치 설정을 수정하실래요?
                """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        })
        builder.setNegativeButton("취소",
            DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음")
                        checkRunTimePermission()
                        return
                    }
                }
        }
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager =
            this.requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }


    //tm 좌표 구하기
    private fun getDustApiInfo(long: Double, lati: Double) {

        val call = ApiObject.retrofitService3.getTMxy(
            kakaokey,
            long,
            lati,
            output_coord
        )
        Log.e("joo", "long :" + long + "  lati :" + lati + "  output:" + output_coord)
        call.enqueue(object : retrofit2.Callback<TMxy> {
            override fun onResponse(call: Call<TMxy>, response: Response<TMxy>) {
                Log.e("joo", response.body()!!.toString())

                if (response.isSuccessful) {
                    val tmx = response.body()!!.documents[0].x
                    val tmy = response.body()!!.documents[0].y
                    Log.e("joo", "tmx:" + tmx.toString() + "  tmy:" + tmy.toString())
                    getNearMeasuring(tmx, tmy)
                }
            }

            override fun onFailure(call: Call<TMxy>, t: Throwable) {
                Log.e("joo", "TM fail : " + t)
            }

        })

    }

    //가까운 측정소 찾기
    private fun getNearMeasuring(tmx: Double, tmy: Double) {
        val call = ApiObject.retrofitService4.getMeasure(tmx, tmy)

        call.enqueue(object : retrofit2.Callback<NEARMEASURE> {
            override fun onResponse(call: Call<NEARMEASURE>, response: Response<NEARMEASURE>) {
                Log.e("joo", response.body()!!.response.body.items.toString())
                if (response.isSuccessful) {
                    // 0 or 1 0에 값이 안나오면 1로 바꿔서 출력할 수 있도록 수정해야 됨

                    var station = response.body()!!.response.body.items[0].stationName
                    var resultDust = getDustInform(station)
                    if (resultDust == null) {
                        station = response.body()!!.response.body.items[1].stationName
                        getDustInform(station)
                    }

                }

            }

            override fun onFailure(call: Call<NEARMEASURE>, t: Throwable) {

            }


        })
    }

    private fun getDustInform(station: String): Int? {
        var getTime: String? = null
        var pm10grade: Int? = null
        val call = ApiObject.retrofitService2.getDust(
            data_type,
            5,
            page_no,
            station,
            dataTerm,
            ver
        )

        call.enqueue(object : retrofit2.Callback<DUST> {

            override fun onResponse(call: Call<DUST>, response: Response<DUST>) {

                Log.e("joo", response.body()!!.response.body.items.toString())
                if (response.isSuccessful) {
                    try {
                        for (result in response.body()!!.response.body.items) {
                            if (result.pm10Grade.toInt() > 0) {
                                // 발표시간
                                getTime = result.dataTime
                                // 시간별 미세먼지
                                pm10grade = result.pm10Grade1h.toInt()
                                break
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("joo", "미세먼지 error: " + e.toString())
                    }

                    Log.e("joo", "미세먼지 발표 시간 :" + getTime)
                    when (pm10grade) {
                        1 -> text_weather_dust.setText("좋음")
                        2 -> text_weather_dust.setText("보통")
                        3 -> text_weather_dust.setText("나쁨")
                        4 -> text_weather_dust.setText("매우나쁨")
                        else -> return
                    }
                }

            }

            override fun onFailure(call: Call<DUST>, t: Throwable) {

            }


        })

        return pm10grade
    }

    // 날씨 예보 api로 값 받아와 출력해주는 메서드
    private fun getWeatherApiInfo(area: String) {
        var itemsize = 0

        if (base_time == "2300") {
            base_date = currentNextDate()
            if (fcstTime.toInt() == 100) {
                itemsize = 12
            } else if (fcstTime.toInt() == 200) {
                itemsize = 24
            } else {
                itemsize = 12
            }
        } else {
            if (fcstTime.toInt() - base_time.toInt() == 200) {
                itemsize = 12
            } else if (fcstTime.toInt() - base_time.toInt() == 300) {
                itemsize = 24
            } else {
                itemsize = 12
            }
        }
        Log.e(
            "joo",
            "basedata:" + base_date + "  basetime:" + base_time + "  nx,ny:" + nx + ny + "  fcstTime: " + fcstTime + "  itemsize:" + itemsize
        )
        val call = ApiObject.retrofitService.getWeather(
            data_type,
            itemsize,
            page_no,
            base_date,
            base_time,
            nx,
            ny
        )
        call.enqueue(object : retrofit2.Callback<WEATHER> {
            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                if (response.isSuccessful) {
                    lateinit var weather: String
                    Log.d("api", response.body().toString())
                    if (response.body()!!.response.body !== null) {
                        for (i in 0..itemsize - 1) {
                            if (response.body()!!.response.body.items.item[i].baseTime == base_time.toInt()) {
                                if (response.body()!!.response.body.items.item[i].category == "POP") {
                                    POP = response.body()!!.response.body.items.item[i].fcstValue
                                } else if (response.body()!!.response.body.items.item[i].category == "SKY") {
                                    SKY = response.body()!!.response.body.items.item[i].fcstValue
                                } else if (response.body()!!.response.body.items.item[i].category == "TMP") {
                                    TMP = response.body()!!.response.body.items.item[i].fcstValue
                                } else if (response.body()!!.response.body.items.item[i].category == "PTY") {
                                    PTY = response.body()!!.response.body.items.item[i].fcstValue
                                }
                            }

                        }
//                    if (response.body()!!.response.body.items.item[])
//                    POP = response.body()!!.response.body.items.item[7+itemsize].fcstValue
//                    SKY = response.body()!!.response.body.items.item[5+itemsize].fcstValue
//                    TMP = response.body()!!.response.body.items.item[0+itemsize].fcstValue
//                    PTY = response.body()!!.response.body.items.item[6+itemsize].fcstValue
                        Log.e(
                            "api",
                            "POP = " + POP + " SKY = " + SKY + " T3H = " + TMP + " PTY = " + PTY
                        )


                        if (PTY.equals("0")) {
                            if (SKY == "1") {
                                img_weather_index.setImageResource(R.drawable.ic_nice)
                            } else if (SKY == "3") {
                                img_weather_index.setImageResource(R.drawable.ic_nomal)
                            } else {
                                img_weather_index.setImageResource(R.drawable.ic_nomal)
                            }
                            weather = skyState(SKY.toString())
                        } else {
                            weather = ptyState(PTY.toString())
                            img_weather_index.setImageResource(R.drawable.ic_bad)
                        }

                        if (weather.equals("구름많음")) {
                            text_weather_index.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30.0f)
                        } else {
                            text_weather_index.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40.0f)
                        }

                        text_weather_index.setText(weather)
                        text_weather_area.setText(area)
                        text_weather_percent.setText(POP + " %")
                        text_weather_temp.setText(TMP + " \u2103")
                    }
                }
            }


            override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                Log.e("api", t.toString())
            }
        })
    }
}


// 날짜 가져오기
private fun currentDate(): String {

    val time = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("yyyyMMdd")
    val curTime = dateFormat.format(Date(time))

    return curTime
}

private fun currentNextDate(): String {

    val time = System.currentTimeMillis() - 1000 * 60 * 60 * 24
    val dateFormat = SimpleDateFormat("yyyyMMdd")
    val curTime = dateFormat.format(Date(time))

    return curTime
}

// 시간 가져오기 00분으로 만들어준다
private fun currentTime(): String {

    val time = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("kk00")
    val curTime = dateFormat.format(Date(time))

    return curTime
}

// 단기예보 api에서 값을 가져오기 위한 현재 시간에 따른 기준 시간 설 메서드
private fun baseTimeSetting(time: String): String {

    val time = time.toInt()
    var strTime = ""
    if ((time == 200) or (time == 100) or (time == 2400)) {
        strTime = "2300"
    } else if (time in 300..500) {
        strTime = "0200"
    } else if (time in 600..800) {
        strTime = "0500"
    } else if (time in 900..1100) {
        strTime = "0800"
    } else if (time in 1200..1400) {
        strTime = "1100"
    } else if (time in 1500..1700) {
        strTime = "1400"
    } else if (time in 1800..2000) {
        strTime = "1700"
    } else if (time in 2100..2300) {
        strTime = "2000"
    }
    Log.e("joo", "base_time: " + strTime)

    return strTime
}

// 평상시 날씨 출력 메서드
private fun skyState(sky: String): String {
    var SKY = sky
    if (SKY.equals("1")) {
        SKY = "맑음"
    } else if (SKY.equals("3")) {
        SKY = "구름많음"
    } else {
        SKY = "흐림"
    }
    return SKY
}

// 비 또는 눈이 올 때 날씨 출력 메서드
private fun ptyState(pty: String): String {
    Log.e("joo", "pty : " + pty)
    var PTY = pty
    if (PTY.equals("1")) {
        PTY = "비"
    } else if (PTY.equals("2")) {
        PTY = "비/눈"
    } else if (PTY.equals("3")) {
        PTY = "눈"
    } else {
        PTY = "소나기"
    }
    return PTY
}

//GPS
private fun convertGRID_GPS(mode: Int, lat_X: Double, lng_Y: Double): LatXLngY {
    val RE = 6371.00877 // 지구 반경(km)
    val GRID = 5.0 // 격자 간격(km)
    val SLAT1 = 30.0 // 투영 위도1(degree)
    val SLAT2 = 60.0 // 투영 위도2(degree)
    val OLON = 126.0 // 기준점 경도(degree)
    val OLAT = 38.0 // 기준점 위도(degree)
    val XO = 43.0 // 기준점 X좌표(GRID)
    val YO = 136.0 // 기준점 Y좌표(GRID)

    //
    // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
    //
    val DEGRAD = Math.PI / 180.0
    val RADDEG = 180.0 / Math.PI
    val re = RE / GRID
    val slat1 = SLAT1 * DEGRAD
    val slat2 = SLAT2 * DEGRAD
    val olon = OLON * DEGRAD
    val olat = OLAT * DEGRAD
    var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
    sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
    var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5)
    sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
    var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
    ro = re * sf / Math.pow(ro, sn)
    val rs = LatXLngY()
    if (mode == TO_GRID) {
        rs.lat = lat_X
        rs.lng = lng_Y
        var ra = Math.tan(Math.PI * 0.25 + lat_X * DEGRAD * 0.5)
        ra = re * sf / Math.pow(ra, sn)
        var theta = lng_Y * DEGRAD - olon
        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn
        rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5)
        rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5)
    } else {
        rs.x = lat_X
        rs.y = lng_Y
        val xn = lat_X - XO
        val yn = ro - lng_Y + YO
        var ra = Math.sqrt(xn * xn + yn * yn)
        if (sn < 0.0) {
            ra = -ra
        }
        var alat = Math.pow(re * sf / ra, 1.0 / sn)
        alat = 2.0 * Math.atan(alat) - Math.PI * 0.5
        var theta = 0.0
        if (Math.abs(xn) <= 0.0) {
            theta = 0.0
        } else {
            if (Math.abs(yn) <= 0.0) {
                theta = Math.PI * 0.5
                if (xn < 0.0) {
                    theta = -theta
                }
            } else theta = Math.atan2(xn, yn)
        }
        val alon = theta / sn + olon
        rs.lat = alat * RADDEG
        rs.lng = alon * RADDEG
    }
    return rs
}


internal class LatXLngY {
    var lat = 0.0
    var lng = 0.0
    var x = 0.0
    var y = 0.0
}


// 날씨 예보 API
private val retrofit = Retrofit.Builder()
    .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/") // 마지막 / 반드시 들어가야 함
    .addConverterFactory(GsonConverterFactory.create()) // converter 지정
    .build() // retrofit 객체 생성

// 미세먼지 API
private val retrofit2 = Retrofit.Builder()
    .baseUrl("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/") // 마지막 / 반드시 들어가야 함
    .addConverterFactory(GsonConverterFactory.create()) // converter 지정
    .build() // retrofit 객체 생성

private val retrofit3 = Retrofit.Builder()
    .baseUrl("https://dapi.kakao.com/") // 마지막 / 반드시 들어가야 함
    .addConverterFactory(
        GsonConverterFactory.create(
            Gson().newBuilder().setLenient().create()
        )
    ) // converter 지정
    .build() // retrofit 객체 생성

private val retrofit4 = Retrofit.Builder()
    .baseUrl("http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/") // 마지막 / 반드시 들어가야 함
    .addConverterFactory(GsonConverterFactory.create()) // converter 지정
    .build() // retrofit 객체 생성

object ApiObject {
    // 동네 날씨 예보
    val retrofitService: WeatherInterface by lazy {
        retrofit.create(WeatherInterface::class.java)
    }
    val retrofitService2: DustInterface by lazy {
        retrofit2.create(DustInterface::class.java)
    }

    // 좌표 TM 좌표로 변환
    val retrofitService3: TMInterface by lazy {
        retrofit3.create((TMInterface::class.java))
    }

    // TM 좌표로 가까운 측정소 찾기
    val retrofitService4: NearInterface by lazy {
        retrofit4.create((NearInterface::class.java))
    }
}

