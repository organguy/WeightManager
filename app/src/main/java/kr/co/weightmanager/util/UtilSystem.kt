package kr.co.weightmanager.util

import android.content.Context
import android.net.ConnectivityManager
import android.util.DisplayMetrics

class UtilSystem {
    companion object {
        fun checkNetworkState(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val currentNetwork = connectivityManager.activeNetwork

            return currentNetwork != null
        }

        fun convertDpToPx(context: Context, dp: Int): Float {
            val resources = context.resources;
            val metrics = resources.displayMetrics;
            val px = (dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toFloat()
            return px;
        }
    }
}