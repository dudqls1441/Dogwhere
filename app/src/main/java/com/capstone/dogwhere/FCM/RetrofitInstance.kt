package com.capstone.dogwhere.FCM


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.capstone.dogwhere.FCM.Constants.Companion.BASE_URL
import com.capstone.dogwhere.FCM.NotificationAPI

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {
            Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}
//    private const val BASE_URL = "https://fcm.googleapis.com/"
//    private var retrofit: Retrofit? = null
//    fun getclinet(): Retrofit?
//        {
//            if (retrofit == null) {
//                retrofit = Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build()
//            }
//            return retrofit
//        }
