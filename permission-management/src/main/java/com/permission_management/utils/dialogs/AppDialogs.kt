package com.permission_management.utils.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.permission_management.interfaces.AlertDialogNavigator
import com.permission_management.R
import timber.log.Timber

object AppDialogs {

    private var alertDialog: AlertDialog? = null

    /**
     *  showAlertDialog will be called when needs to show alert dialog with positive and negative buttons
     *  and also which redirects the button click back to calling view
     *  this function has,
     *  @param _title to show the title of the dialog
     *  @param _message to show message of the dialog
     *  @param _positiveText to show positive button text
     *  @param _negativeText to show negative button text
     *  @param _onPositiveClick redirects back to calling activity/fragment positive button click
     *  @param _onNegativeClick redirects back to calling activity/fragment negative button click
     */
    fun showAlertDialog(
        context: Context,
        title: String?,
        message: String,
        positiveText: String,
        negativeText: String,
        navigator: AlertDialogNavigator? = null
    ) {
        try {
            context as Activity

            alertDialog = AlertDialog.Builder(context).create()
            alertDialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
            alertDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            alertDialog!!.setCancelable(false)

            val customView = context.layoutInflater.inflate(R.layout.dialog_custom_alert, null)
            alertDialog!!.setView(customView)

            val tvTitle = customView.findViewById<AppCompatTextView>(R.id.tvTitle)
            val tvErrorMsg = customView.findViewById<AppCompatTextView>(R.id.tvErrorMsg)
            val lnButtons = customView.findViewById<LinearLayout>(R.id.lnButtons)
            val tvNegative = customView.findViewById<AppCompatTextView>(R.id.tvNegative)
            val tvPositive = customView.findViewById<AppCompatTextView>(R.id.tvPositive)
            val tvOk = customView.findViewById<AppCompatTextView>(R.id.tvOk)

            lnButtons.visibility = View.VISIBLE
            tvOk.visibility = View.GONE

            if (!title.isNullOrEmpty()) {
                tvTitle.visibility = View.VISIBLE
                tvTitle.text = title
            } else {
                tvTitle.visibility = View.GONE
            }

            tvErrorMsg.text = message
            tvNegative.text = negativeText
            tvPositive.text = positiveText

            tvNegative.setOnClickListener {
                navigator?.onNegativeButtonClick(alertDialog!!)
            }

            tvPositive.setOnClickListener {
                navigator?.onPositiveButtonClick(alertDialog!!)
            }

            alertDialog!!.takeIf { !context.isFinishing }?.show()
        } catch (e: Exception) {
            Timber.e("showAlertDialog error $e")
        }
    }

}