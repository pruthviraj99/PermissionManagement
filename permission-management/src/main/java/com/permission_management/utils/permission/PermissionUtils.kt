package com.permission_management.utils.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.permission_management.*
import com.permission_management.base.AppApplication
import com.permission_management.interfaces.AlertDialogNavigator
import com.permission_management.utils.dialogs.AppDialogs
import com.permission_management.utils.preference.AppPref
import timber.log.Timber

object PermissionUtils {

    const val LOCATION_AND_BLUETOOTH_PERMISSION_CODE = 1001
    const val LOCATION_PERMISSION_CODE = 1002
    const val STORAGE_PERMISSION_CODE = 1003

    var CURRENT_REQUEST_CODE = 0

    val LOCATION_BLUETOOTH_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val LOCATION_PERMISSION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    val WIFI_STATE_PERMISSION = arrayOf(Manifest.permission.ACCESS_WIFI_STATE)

    val STORAGE_PERMISSION = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    var customPermissionList: Array<String> = arrayOf()

    /**
     * Permission
     *
     * @param context
     * @param permissions
     * @return
     */
    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }

        return true
    }

    fun verifyPermissions(grantResults: IntArray): Boolean {
        if (grantResults.isEmpty()) {
            return false
        }

        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    /**
     * Check Ble And Location Permission
     */
    fun checkAllPermissionGranted(
        context: Context,
        permissionRequestCode: Int,
        mBluetoothAdapter: BluetoothAdapter? = null,
    ): Boolean {
        return when (permissionRequestCode) {
            LOCATION_AND_BLUETOOTH_PERMISSION_CODE -> {
                if (!hasPermissions(context, LOCATION_BLUETOOTH_PERMISSION)) {
                    false
                } else if (!isGPSEnabled(context)) {
                    false
                } else !(mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled)
            }
            LOCATION_PERMISSION_CODE -> {
                if (!hasPermissions(context, LOCATION_BLUETOOTH_PERMISSION)) {
                    false
                } else isGPSEnabled(context)
            }
            STORAGE_PERMISSION_CODE -> {
                hasPermissions(context, STORAGE_PERMISSION)
            }
            else -> {
                true
            }
        }
    }

    /**
     * Check Gps Location
     */
    fun isGPSEnabled(context: Context): Boolean {

        try {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val networkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            return isGpsEnabled || networkEnabled
        } catch (e: Exception) {
            Timber.e("getGpsStatus Error :: $e")
        }

        return false
    }

    fun requestPermissions(
        context: Context,
        requestedCode: Int,
        gpsSettingActivityResult: ActivityResultLauncher<Intent>,
        bluetoothSettingActivityResult: ActivityResultLauncher<Intent>,
        appSettingActivityResult: ActivityResultLauncher<Intent>,
        requestPermissionResult: ActivityResultLauncher<Array<String>>,
    ) {
        when (requestedCode) {
            LOCATION_AND_BLUETOOTH_PERMISSION_CODE -> {
                if (!hasPermissions(context, LOCATION_BLUETOOTH_PERMISSION)) {
                    requestPermission(context,
                        appSettingActivityResult,
                        requestPermissionResult,
                        LOCATION_AND_BLUETOOTH_PERMISSION_CODE,
                        LOCATION_BLUETOOTH_PERMISSION,
                        context.getString(R.string.str_permission_blocked_title),
                        context.getString(R.string.str_permission_blocked_message))
                } else if (!isGPSEnabled(context)) {
                    openLocationDialog(context, gpsSettingActivityResult)
                } else {
                    val settingsIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    bluetoothSettingActivityResult.launch(settingsIntent)
                }
            }
            LOCATION_PERMISSION_CODE -> {
                if (!hasPermissions(context, LOCATION_BLUETOOTH_PERMISSION)) {
                    requestPermission(context,
                        appSettingActivityResult,
                        requestPermissionResult,
                        LOCATION_PERMISSION_CODE,
                        LOCATION_PERMISSION,
                        context.getString(R.string.str_permission_blocked_title),
                        context.getString(R.string.str_permission_blocked_message))
                } else if (!isGPSEnabled(context)) {
                    openLocationDialog(context, gpsSettingActivityResult)
                }
            }
            STORAGE_PERMISSION_CODE -> {
                if (!hasPermissions(context, STORAGE_PERMISSION)) {
                    requestPermission(context,
                        appSettingActivityResult,
                        requestPermissionResult,
                        STORAGE_PERMISSION_CODE,
                        STORAGE_PERMISSION,
                        context.getString(R.string.str_permission_blocked_title),
                        context.getString(R.string.str_permission_blocked_message))
                }
            }
        }
    }

    fun requestCustomPermissions(
        context: Context,
        requestedCode: Int,
        permissionList: Array<String>,
        appSettingActivityResult: ActivityResultLauncher<Intent>,
        requestPermissionResult: ActivityResultLauncher<Array<String>>,
    ) {
        if (!hasPermissions(context, permissionList)) {
            requestPermission(context,
                appSettingActivityResult,
                requestPermissionResult,
                requestedCode,
                permissionList,
                context.getString(R.string.str_permission_blocked_title),
                context.getString(R.string.str_permission_blocked_message))
        }
    }

    private fun requestPermission(
        context: Context,
        appSettingActivityResult: ActivityResultLauncher<Intent>,
        requestPermissionResult: ActivityResultLauncher<Array<String>>,
        permissionRequestCode: Int,
        permission: Array<String>,
        title: String,
        message: String,
    ) {
        context as Activity
        if (isFirstTimeAskPermission(permissionRequestCode.toString())) {
            requestPermissionResult.launch(permission)
        } else {
            openSettingDialog(context, title, message, appSettingActivityResult)
        }
    }

    /**
     * Open GPS Alert Dialog and Pass intent with result
     */
    private fun openLocationDialog(
        context: Context,
        activityResult: ActivityResultLauncher<Intent>,
    ) {
        AppDialogs.showAlertDialog(
            context,
            context.getString(R.string.str_location_resolution_title),
            context.getString(R.string.str_location_resolution_message),
            context.getString(R.string.str_enable),
            context.getString(R.string.str_cancel),
            object : AlertDialogNavigator {
                override fun onPositiveButtonClick(alertDialog: AlertDialog) {
                    alertDialog.dismiss()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    activityResult.launch(intent)
                }

                override fun onNegativeButtonClick(alertDialog: AlertDialog) {
                    alertDialog.dismiss()
                    (context as Activity).finish()
                }
            })
    }

    /**
     * Open Setting Alert Dialog
     */
    private fun openSettingDialog(
        context: Context,
        title: String,
        msg: String,
        activityResult: ActivityResultLauncher<Intent>,
    ) {
        AppDialogs.showAlertDialog(context,
            title,
            msg,
            context.getString(R.string.str_open_settings),
            context.getString(R.string.str_cancel),
            object : AlertDialogNavigator {
                override fun onPositiveButtonClick(alertDialog: AlertDialog) {
                    alertDialog.dismiss()
                    openSetting(context as Activity, activityResult)
                }

                override fun onNegativeButtonClick(alertDialog: AlertDialog) {
                    alertDialog.dismiss()
                    (context as Activity).finish()
                }
            })
    }

    /**
     * Open Setting Intent With Activity Result
     */
    private fun openSetting(
        context: Activity,
        activityResult: ActivityResultLauncher<Intent>,
    ) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        activityResult.launch(intent)
    }

    private fun isFirstTimeAskPermission(key: String): Boolean {
        val appPref = AppPref(AppApplication.pref)
        return appPref.getPreference(key, true)
    }

    fun storeFirstTimeAskPermissionValue(key: String, value: Boolean) {
        val appPref = AppPref(AppApplication.pref)
        appPref.storePreference(key, value)
    }
}