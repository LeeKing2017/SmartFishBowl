package com.example.smartfishbowl.work

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {
    override fun doWork(): ListenableWorker.Result {
        //장기 실행 작업
        Log.d(TAG, "Performing long running task in scheduled job")
        return ListenableWorker.Result.success()
    }
    companion object{
        private val TAG = "MyWorker"
    }
}