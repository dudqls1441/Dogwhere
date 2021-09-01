package com.capstone.dogwhere.NearMeasuring

data class NEARMEASURE(
    val response: Response
)

data class Response(
    val body: Body,
    val header: Header
)

data class Header(
    val resultCode: String,
    val resultMsg: String
)

data class Body(
    val items: List<Item>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class Item(
    val addr: String,
    val stationName: String,
    val tm: Double
)