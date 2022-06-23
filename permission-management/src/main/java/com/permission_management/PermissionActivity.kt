package com.permission_management

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class PermissionActivity : AppCompatActivity() {

    private val mBluetoothAdapter: BluetoothAdapter by lazy {
        (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        checkStoragePermission(PermissionUtils.LOCATION_PERMISSION)
    }

    /**
     * Check All Permission
     */
    private fun checkStoragePermission(permissions: Array<String>) {
        PermissionUtils.requestedAppSettingsPermission = arrayOf()
        if (PermissionUtils.isAllPermissionGranted(this, permissions)) {
            Toast.makeText(this, "All Permission is granted", Toast.LENGTH_LONG).show()
            finish()
        } else {
            PermissionUtils.requestAllPermission(this,
                permissions,
                appSettingActivityResult,
                requestPermissionResult,
                gpsSettingActivityResult,
                bluetoothSettingActivityResult)
        }
    }

    private var requestPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val grantResults = ArrayList<Int>()
        permissions.entries.forEach {
            // check whether each permission is granted or not
            if (it.value as Boolean)
                grantResults.add(0)
            else
                grantResults.add(1)
        }

        permissionData(grantResults, permissions)
    }

    private fun permissionData(
        grantResults: ArrayList<Int>,
        permissions: MutableMap<String, Boolean>,
    ) {
        if (PermissionUtils.verifyPermissions(grantResults.toIntArray())) {
            for (permission in permissions.entries) {
                PermissionUtils.storeFirstTimeAskPermissionValue(permission.key, true)
                break
            }

            finish()
        } else {
            for (permission in permissions.entries) {
                PermissionUtils.storeFirstTimeAskPermissionValue(permission.key, false)
                break
            }

            PermissionUtils.requestAllPermission(this,
                permissions.keys.toTypedArray(),
                appSettingActivityResult,
                requestPermissionResult,
                gpsSettingActivityResult,
                bluetoothSettingActivityResult)
        }
    }

    /**
     * Get Activity Result of Create New Home result
     */
    private val appSettingActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {

        if (PermissionUtils.isAllPermissionGranted(this,
                PermissionUtils.requestedAppSettingsPermission)
        ) {
            checkStoragePermission(PermissionUtils.requestedAppSettingsPermission)
        } else {
            Toast.makeText(this@PermissionActivity, "Some Permission is blocked", Toast.LENGTH_LONG)
                .show()
            finish()
        }
    }

    /**
     * Get Activity Result of Create New Home result
     */
    private val gpsSettingActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (PermissionUtils.isGPSEnabled(this)) {
            checkStoragePermission(PermissionUtils.requestedAppSettingsPermission)
        } else {
            Toast.makeText(this,
                getString(R.string.str_location_resolution_message),
                Toast.LENGTH_LONG).show()
        }
    }


    /**
     * Get Activity Result of Create New Home result
     */
    private val bluetoothSettingActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (mBluetoothAdapter.isEnabled) {
            checkStoragePermission(PermissionUtils.requestedAppSettingsPermission)
        } else {
            Toast.makeText(this,
                getString(R.string.str_bluetooth_turn_on_message),
                Toast.LENGTH_LONG).show()
        }
    }
}