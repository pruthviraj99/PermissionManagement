package com.permissionmanagement

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.permission_management.cofig.PermissionConfig
import com.permission_management.utils.permission.PermissionUtils

class MainActivity : AppCompatActivity() {
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionConfig.onPermissionStatus.subscribe {
            Log.e("Permission Status :", "${it.first} :: Permission Request Code : ${it.second}")
        }

        val btnLocationBlePermission =
            findViewById<AppCompatButton>(R.id.btnLocationBlePermission)
        val btnLocationPermission =
            findViewById<AppCompatButton>(R.id.btnLocationPermission)
        val btnStoragePermission =
            findViewById<AppCompatButton>(R.id.btnStoragePermission)
        val btnCustomPermission =
            findViewById<AppCompatButton>(R.id.btnCustomPermission)

        btnLocationBlePermission.setOnClickListener {
            PermissionConfig.checkLocationAndBLEPermission(this)
        }

        btnLocationPermission.setOnClickListener {
            PermissionConfig.checkLocationPermission(this)
        }

        btnStoragePermission.setOnClickListener {
            PermissionConfig.checkStoragePermission(this)
        }

        btnCustomPermission.setOnClickListener {
            PermissionConfig.addCustomPermissionList(PermissionUtils.CAMERA_PERMISSION)
            PermissionConfig.addCustomPermissionList(PermissionUtils.WIFI_STATE_PERMISSION)
            PermissionConfig.checkCustomPermission(this)
        }
    }
}