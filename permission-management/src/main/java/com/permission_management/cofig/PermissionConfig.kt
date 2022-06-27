package com.permission_management.cofig

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.permission_management.R
import com.permission_management.ui.PermissionActivity
import com.permission_management.utils.Global
import com.permission_management.utils.permission.PermissionUtils
import io.reactivex.subjects.PublishSubject

object PermissionConfig {

    var onPermissionStatus: PublishSubject<Pair<Boolean, Int>> = PublishSubject.create()
    var customPermissionList: Array<String> = arrayOf()

    fun checkLocationAndBLEPermission(context: Context) {
        openPermissionScreen(context, PermissionUtils.LOCATION_AND_BLUETOOTH_PERMISSION_CODE)
    }

    fun checkLocationPermission(context: Context) {
        openPermissionScreen(context, PermissionUtils.LOCATION_PERMISSION_CODE)
    }

    fun checkStoragePermission(context: Context) {
        openPermissionScreen(context, PermissionUtils.STORAGE_PERMISSION_CODE)
    }

    fun checkCustomPermission(context: Context) {
        if (!customPermissionList.isNullOrEmpty()) {
            val intent = Intent(context, PermissionActivity::class.java)
            intent.putExtra(Global.CUSTOM_PERMISSION, PermissionUtils.CAMERA_PERMISSION)
            (context as Activity).startActivity(intent)
        } else {
            Global.showToast(context, context.getString(R.string.str_custom_permission_message))
        }
    }

    private fun openPermissionScreen(context: Context, requestCode: Int) {
        val intent = Intent(context, PermissionActivity::class.java)
        intent.putExtra(Global.REQUEST_CODE, requestCode)
        (context as Activity).startActivity(intent)
    }

    fun addCustomPermissionList(permissionList: Array<String>) {
        customPermissionList = permissionList
    }
}