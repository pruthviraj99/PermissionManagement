package com.permission_management.utils

import android.content.Context
import android.widget.Toast

object Global {
    const val REQUEST_CODE = "REQUEST_CODE"
    const val CUSTOM_PERMISSION = "CUSTOM_PERMISSION"
    const val IS_FIRST_TIME_ASK_LOCATION_PERMISSION = "isFirstTimeAskLocationPermission"

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}