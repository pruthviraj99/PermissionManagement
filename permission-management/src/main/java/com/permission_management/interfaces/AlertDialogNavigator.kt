package com.permission_management.interfaces

import android.app.AlertDialog

interface AlertDialogNavigator {
    fun onPositiveButtonClick(alertDialog: AlertDialog)
    fun onNegativeButtonClick(alertDialog: AlertDialog)
}