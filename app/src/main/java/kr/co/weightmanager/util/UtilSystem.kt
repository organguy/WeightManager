package kr.co.weightmanager.util

import android.content.Context
import android.net.ConnectivityManager

class UtilSystem {
    companion object{
        fun checkNetworkState(context: Context): Boolean{
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val currentNetwork = connectivityManager.activeNetwork

            return currentNetwork != null
        }
    }
}