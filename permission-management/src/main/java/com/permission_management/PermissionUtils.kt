package com.permission_management

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import timber.log.Timber

object PermissionUtils {

    val STORAGE_PERMISSION = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

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

    var requestedAppSettingsPermission: Array<String> = arrayOf()

    /**
     * Permission
     *
     * @param context
     * @param permissions
     * @return
     */
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
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

    fun isAllPermissionGranted(context: Context, permissions: Array<String>): Boolean {
        return if (permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Timber.e("Check Location Permission")
            if (!hasPermissions(context, LOCATION_PERMISSION)) {
                Timber.e("Check Location Permission")

                false
            } else isGPSEnabled(context)
        } else {
            Timber.e("Check Other Permission")
            hasPermissions(context, permissions)
        }
    }

    fun requestAllPermission(
        context: Context,
        permissions: Array<String>,
        appSettingActivityResult: ActivityResultLauncher<Intent>,
        requestPermissionResult: ActivityResultLauncher<Array<String>>,
        gpsSettingActivityResult: ActivityResultLauncher<Intent>,
        bluetoothSettingActivityResult: ActivityResultLauncher<Intent>,
    ) {
        if (!hasPermissions(context, permissions)) {
            if (permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (!hasPermissions(context, LOCATION_PERMISSION)) {
                    requestStoragePermission(context,
                        permissions,
                        appSettingActivityResult,
                        requestPermissionResult)
                } else if (!isGPSEnabled(context)) {
                    openLocationDialog(context, permissions, gpsSettingActivityResult)
                } else {
                    //Open Ble Setting
//                    val settingsIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                    bluetoothSettingActivityResult.launch(settingsIntent)
                }
            } else {
                requestStoragePermission(context,
                    permissions,
                    appSettingActivityResult,
                    requestPermissionResult)
            }
        }
    }

    private fun requestStoragePermission(
        context: Context,
        permissions: Array<String>,
        appSettingActivityResult: ActivityResultLauncher<Intent>,
        requestPermissionResult: ActivityResultLauncher<Array<String>>,
    ) {
        if (isFirstTimeAskPermission(permissions[0])) {
            requestPermissionResult.launch(permissions)
        } else {
            openSettingDialog(
                context,
                context.getString(R.string.str_app_name),
                context.getString(R.string.str_storage_permission_message),
                appSettingActivityResult,
                permissions
            )
        }
    }

    /**
     * Open GPS Alert Dialog and Pass intent with result
     */
    fun openLocationDialog(
        context: Context,
        permissions: Array<String>,
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
                    requestedAppSettingsPermission = permissions
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    activityResult.launch(intent)
                }

                override fun onNegativeButtonClick(alertDialog: AlertDialog) {
                    alertDialog.dismiss()
                }
            })
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


    /**
     * Open Setting Alert Dialog
     */
    private fun openSettingDialog(
        context: Context,
        title: String,
        msg: String,
        activityResult: ActivityResultLauncher<Intent>,
        permissions: Array<String>,
    ) {
        AppDialogs.showAlertDialog(context,
            title,
            msg,
            context.getString(R.string.str_open_settings),
            context.getString(R.string.str_cancel),
            object : AlertDialogNavigator {
                override fun onPositiveButtonClick(alertDialog: AlertDialog) {
                    alertDialog.dismiss()
                    openSetting(context as Activity, activityResult, permissions)
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
        permissions: Array<String>,
    ) {
        requestedAppSettingsPermission = permissions
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