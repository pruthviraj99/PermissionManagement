package com.permission_management

import android.app.AlertDialog

interface AlertDialogNavigator {
    fun onPositiveButtonClick(alertDialog: AlertDialog)
    fun onNegativeButtonClick(alertDialog: AlertDialog)
}