package com.capstone.dogwhere.TMxy

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TMInterface {

    @GET("v2/local/geo/transcoord.json")    // Keyword.json의 정보를 받아옴
    fun getTMxy(
        @Header("Authorization") key: String,     // 카카오 API 인증키 [필수]
        @Query("x") x: Double, // 검색을 원하는 질의어 [필수]
        @Query("y") y: Double,
        @Query("output_coord") output_coord: String
        // 매개변수 추가 가능
        // @Query("category_group_code") category: String

    ): Call<TMxy>    // 받아온 정보가 ResultSearchKeyword 클래스의 구조로 담김
}