package com.permission_management.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.permission_management.R
import com.permission_management.cofig.PermissionConfig
import com.permission_management.utils.Global
import com.permission_management.utils.permission.PermissionUtils
import timber.log.Timber

class PermissionActivity : AppCompatActivity() {

    private val mBluetoothAdapter: BluetoothAdapter by lazy {
        (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        getIntentData(intent)
    }

    private fun getIntentData(intent: Intent?) {
        if (intent != null) {
            if (intent.hasExtra(Global.REQUEST_CODE)) {
                checkPermissions(intent.getIntExtra(Global.REQUEST_CODE, 0))
            } else if (intent.hasExtra(Global.CUSTOM_PERMISSION)) {
                val permissionList =
                    intent.getStringArrayExtra(Global.CUSTOM_PERMISSION) as Array<String>
                checkCustomPermission(permissionList)
            }
        }
    }

    private fun checkPermissions(permissionCode: Int) {
        PermissionUtils.CURRENT_REQUEST_CODE = 0
        when (permissionCode) {
            PermissionUtils.LOCATION_AND_BLUETOOTH_PERMISSION_CODE -> checkLocationAndBLEPermission()
            PermissionUtils.LOCATION_PERMISSION_CODE -> checkLocationPermission()
            PermissionUtils.STORAGE_PERMISSION_CODE -> checkStoragePermission()
        }
    }

    private fun checkLocationAndBLEPermission() {
        PermissionUtils.CURRENT_REQUEST_CODE =
            PermissionUtils.LOCATION_AND_BLUETOOTH_PERMISSION_CODE
        if (PermissionUtils.checkAllPermissionGranted(this,
                PermissionUtils.LOCATION_AND_BLUETOOTH_PERMISSION_CODE,
                mBluetoothAdapter)
        ) {
            Timber.e("Location and BLE Permission is granted.")
            Global.showToast(this, "Location and BLE Permission are granted.")
            publishResult(true, PermissionUtils.LOCATION_AND_BLUETOOTH_PERMISSION_CODE)
        } else {
            requestPermission(PermissionUtils.LOCATION_AND_BLUETOOTH_PERMISSION_CODE)
        }
    }

    private fun checkLocationPermission() {
        PermissionUtils.CURRENT_REQUEST_CODE =
            PermissionUtils.LOCATION_PERMISSION_CODE
        if (PermissionUtils.checkAllPermissionGranted(
                this,
                PermissionUtils.LOCATION_PERMISSION_CODE,
            )
        ) {
            Timber.e("Location Permission is granted.")
            Global.showToast(this, "Location Permission is granted.")
            publishResult(true, PermissionUtils.LOCATION_PERMISSION_CODE)
        } else {
            requestPermission(PermissionUtils.LOCATION_PERMISSION_CODE)
        }
    }

    private fun checkStoragePermission() {
        PermissionUtils.CURRENT_REQUEST_CODE =
            PermissionUtils.STORAGE_PERMISSION_CODE
        if (PermissionUtils.checkAllPermissionGranted(
                this,
                PermissionUtils.STORAGE_PERMISSION_CODE,
            )
        ) {
            Timber.e("Storage Permission is granted.")
            Global.showToast(this, "Storage Permission is granted.")
            publishResult(true, PermissionUtils.STORAGE_PERMISSION_CODE)
        } else {
            requestPermission(PermissionUtils.STORAGE_PERMISSION_CODE)
        }
    }

    private fun checkCustomPermission(permissionList: Array<String>) {
        PermissionUtils.CURRENT_REQUEST_CODE = permissionList.size
        PermissionUtils.customPermissionList = permissionList

        if (PermissionUtils.hasPermissions(this, permissionList)) {
            Timber.e("Custom Permission is granted.")
            Global.showToast(this, "Custom Permission is granted.")
            publishResult(true, PermissionUtils.CURRENT_REQUEST_CODE)
        } else {
            PermissionUtils.requestCustomPermissions(this,
                PermissionUtils.CURRENT_REQUEST_CODE,
                permissionList,
                appSettingActivityResult,
                requestPermissionResult)
        }
    }

    private fun requestPermission(requestCode: Int) {
        PermissionUtils.requestPermissions(this,
            requestCode,
            gpsSettingActivityResult,
            bluetoothSettingActivityResult,
            appSettingActivityResult,
            requestPermissionResult)
    }

    /**
     * Get Activity Result of Create New Home result
     */
    private val gpsSettingActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (PermissionUtils.isGPSEnabled(this)) {
            checkPermissions(PermissionUtils.CURRENT_REQUEST_CODE)
        } else {
            Global.showToast(this, getString(R.string.str_location_resolution_message))
            publishResult(false, PermissionUtils.CURRENT_REQUEST_CODE)
        }
    }

    /**
     * Get Activity Result of Create New Home result
     */
    private val bluetoothSettingActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (mBluetoothAdapter.isEnabled) {
            checkPermissions(PermissionUtils.CURRENT_REQUEST_CODE)
            finish()
        } else {
            Global.showToast(this, getString(R.string.str_bluetooth_turn_on_message))
            publishResult(false, PermissionUtils.CURRENT_REQUEST_CODE)
        }
    }

    /**
     * Get Activity Result of Create New Home result
     */
    private val appSettingActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        when (PermissionUtils.CURRENT_REQUEST_CODE) {
            PermissionUtils.LOCATION_AND_BLUETOOTH_PERMISSION_CODE -> {
                if (PermissionUtils.hasPermissions(this,
                        PermissionUtils.LOCATION_BLUETOOTH_PERMISSION)
                ) {
                    checkPermissions(PermissionUtils.LOCATION_AND_BLUETOOTH_PERMISSION_CODE)
                } else {
                    Global.showToast(this, getString(R.string.str_permission_blocked_message))
                    publishResult(false, PermissionUtils.CURRENT_REQUEST_CODE)
                }
            }
            PermissionUtils.LOCATION_PERMISSION_CODE -> {
                if (PermissionUtils.hasPermissions(this, PermissionUtils.LOCATION_PERMISSION)) {
                    checkPermissions(PermissionUtils.LOCATION_PERMISSION_CODE)
                } else {
                    Global.showToast(this, getString(R.string.str_permission_blocked_message))
                    publishResult(false, PermissionUtils.CURRENT_REQUEST_CODE)
                }
            }

            PermissionUtils.STORAGE_PERMISSION_CODE -> {
                if (PermissionUtils.hasPermissions(this, PermissionUtils.STORAGE_PERMISSION)) {
                    checkPermissions(PermissionUtils.STORAGE_PERMISSION_CODE)
                } else {
                    Global.showToast(this, getString(R.string.str_permission_blocked_message))
                    publishResult(false, PermissionUtils.CURRENT_REQUEST_CODE)
                }
            }

            PermissionUtils.customPermissionList.size -> {
                if (PermissionUtils.hasPermissions(this, PermissionUtils.customPermissionList)) {
                    checkCustomPermission(PermissionUtils.customPermissionList)
                } else {
                    Global.showToast(this, getString(R.string.str_permission_blocked_message))
                    publishResult(false, PermissionUtils.CURRENT_REQUEST_CODE)
                }
            }
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

        permissionData(grantResults)
    }

    private fun permissionData(grantResults: ArrayList<Int>) {
        if (PermissionUtils.verifyPermissions(grantResults.toIntArray())) {
            PermissionUtils.storeFirstTimeAskPermissionValue(PermissionUtils.CURRENT_REQUEST_CODE.toString(),
                true)
            if (PermissionUtils.customPermissionList.size == PermissionUtils.CURRENT_REQUEST_CODE) {
                checkCustomPermission(PermissionUtils.customPermissionList)
            } else {
                checkPermissions(PermissionUtils.CURRENT_REQUEST_CODE)
            }
        } else {
            PermissionUtils.storeFirstTimeAskPermissionValue(PermissionUtils.CURRENT_REQUEST_CODE.toString(),
                false)
            if (PermissionUtils.customPermissionList.size == PermissionUtils.CURRENT_REQUEST_CODE) {
                PermissionUtils.requestCustomPermissions(this,
                    PermissionUtils.CURRENT_REQUEST_CODE,
                    PermissionUtils.customPermissionList,
                    appSettingActivityResult,
                    requestPermissionResult)
            } else {
                requestPermission(PermissionUtils.CURRENT_REQUEST_CODE)
            }
        }
    }

    private fun publishResult(isGranted: Boolean, requestCode: Int) {
        PermissionConfig.onPermissionStatus.onNext(Pair(isGranted, requestCode))
        finish()
    }
}