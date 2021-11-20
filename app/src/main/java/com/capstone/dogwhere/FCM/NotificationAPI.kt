package com.capstone.dogwhere.FCM

import com.capstone.dogwhere.FCM.Constants.Companion.SERVER_KEY
import com.capstone.dogwhere.FCM.Constants.Companion.CONTENT_TYPE
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    //서버 키와 보낼 형식을 헤더에 넣는다 . (JSON)

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun sendNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}