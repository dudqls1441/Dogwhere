package com.capstone.dogwhere.NearMeasuring

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NearInterface {
    @GET("getNearbyMsrstnList?serviceKey=GN%2Br4yL8PgIgO%2FK5aqch2TVxK6opk2CdyjKj9Xm6cTb%2FPf6kLaZb8FbXa6tc4zQbWWAL8ih93ZCn4go9pvOUvg%3D%3D&returnType=json")
    fun getMeasure(
        @Query("tmX") tmx: Double,
        @Query("tmY") tmy: Double,
    ): Call<NEARMEASURE>
}