package com.capstone.dogwhere

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


val num_of_rows = 10
val page_no = 1
val data_type = "JSON"
val base_time = 1100
val base_data = 20210515
val nx = "60"
val ny = "27"

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
    val category: String
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

class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                }
            }

            override fun onFailure(call: Call<WEATHER>, t: Throwable) {
            }
        })
    }
}

//private const val serviceKey = "GN%2Br4yL8PgIgO%2FK5aqch2TVxK6opk2CdyjKj9Xm6cTb%2FPf6kLaZb8FbXa6tc4zQbWWAL8ih93ZCn4go9pvOUvg%3D%3D" /*공공데이터포털에서 받은 인증키*/
//private const val BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst"
//
//@RequiresApi(Build.VERSION_CODES.N)
//fun getVilageFcst() {
//    try {
//        val urlBuilder = StringBuilder(BASE_URL)
//        urlBuilder.append(
//            "?" + URLEncoder.encode("ServiceKey", "UTF-8").toString() + "=" + serviceKey
//        )
//        urlBuilder.append(
//            "&" + URLEncoder.encode("pageNo", "UTF-8").toString() + "=" + URLEncoder.encode(
//                "1",
//                "UTF-8"
//            )
//        )
//        urlBuilder.append(
//            "&" + URLEncoder.encode("numOfRows", "UTF-8").toString() + "=" + URLEncoder.encode(
//                "10",
//                "UTF-8"
//            )
//        )
//        urlBuilder.append(
//            "&" + URLEncoder.encode("dataType", "UTF-8")
//                .toString() + "=" + URLEncoder.encode("JSON", "UTF-8")
//        )
//        urlBuilder.append(
//            "&" + URLEncoder.encode("base_date", "UTF-8")
//                .toString() + "=" + URLEncoder.encode("20200807", "UTF-8")
//        )
//        urlBuilder.append(
//            "&" + URLEncoder.encode("base_time", "UTF-8")
//                .toString() + "=" + URLEncoder.encode("1100", "UTF-8")
//        )
//        urlBuilder.append(
//            "&" + URLEncoder.encode("nx", "UTF-8").toString() + "=" + URLEncoder.encode(
//                "76",
//                "UTF-8"
//            )
//        )
//        urlBuilder.append(
//            "&" + URLEncoder.encode("ny", "UTF-8").toString() + "=" + URLEncoder.encode(
//                "122",
//                "UTF-8"
//            )
//        )
//        val url = URL(urlBuilder.toString())
//        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
//        conn.setRequestMethod("GET")
//        conn.setRequestProperty("Content-type", "application/json")
//        System.out.println("Response code: " + conn.getResponseCode())
//        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//            val sb = StringBuilder()
//            val `in` = BufferedReader(InputStreamReader(conn.getInputStream()))
//            `in`.lines().forEach { line -> sb.append(line) }
//            `in`.close()
//            conn.disconnect()
//            println(sb.toString())
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//}

