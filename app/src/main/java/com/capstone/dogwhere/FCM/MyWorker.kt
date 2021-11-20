package com.capstone.dogwhere.FCM

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams){
    override fun doWork(): Result {
        Log.d("ybyb","MyWorker -> Performing long running task in scheduled job")

        return ListenableWorker.Result.success()

    }

    companion object{
        private val TAG ="MyWorker"
    }


}
