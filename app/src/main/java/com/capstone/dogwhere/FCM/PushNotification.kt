package com.capstone.dogwhere.FCM

import com.capstone.dogwhere.FCM.NotificationData

data class PushNotification(
    val data : NotificationData,
    val to:String //알림을 보낼 디바이스 토큰 , 또는 채널("구독"형식)
)
