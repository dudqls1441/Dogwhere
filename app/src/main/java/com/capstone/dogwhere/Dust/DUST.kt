package com.capstone.dogwhere.Dust

data class DUST(
    val response: Response
)

data class Response(
    val body: Body,
    val header: Header
)

data class Body(
    val items: List<Item>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class Header(
    val resultCode: String,
    val resultMsg: String
)

data class Item(
    val dataTime: String,
    val pm10Grade: String,
    val pm10Grade1h: String,
    val pm10Value: String,
    val pm10Value24: String,
    val pm25Grade: Any,
    val pm25Grade1h: String,
    val pm25Value: String,
)