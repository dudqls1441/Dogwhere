package com.capstone.dogwhere.Dust

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//대기질 예보통보 조회
interface DustInterface {
    @GET("getMsrstnAcctoRltmMesureDnsty?serviceKey=GN%2Br4yL8PgIgO%2FK5aqch2TVxK6opk2CdyjKj9Xm6cTb%2FPf6kLaZb8FbXa6tc4zQbWWAL8ih93ZCn4go9pvOUvg%3D%3D")
    fun getDust(
        @Query("returnType") returnType: String,
        @Query("numOfRows") num_of_rows: Int,
        @Query("pageNo") page_no: Int,
        @Query("stationName") stationName: String,
        @Query("dataTerm") data_type: String,
        @Query("ver") ver: String
    ): Call<DUST>
}