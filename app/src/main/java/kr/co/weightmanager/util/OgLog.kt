package kr.co.weightmanager.util

import android.util.Log
import kr.co.weightmanager.BuildConfig

class OgLog {
    companion object{
        val TAG = "organ"
        fun d(msg: String?) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, msg!!)
            }
        }
    }
}