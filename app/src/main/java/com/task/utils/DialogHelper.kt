package com.task.utils

import android.app.Activity
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.task.R
import kotlinx.android.synthetic.main.dialog_alert_helper.view.*
import javax.inject.Inject

class DialogHelper @Inject constructor(val activity: Activity) {

    private var alertDialog: AlertDialog? = null


    fun showAlertDialog(
        lister: DialogPickListener,
        title: String,
        msg: String,
        positive: String,
        negative: String, showCommentBox: Boolean = false
    ) {

        val dialogBuilder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)
        val alertView = inflater.inflate(R.layout.dialog_alert_helper, null)
        dialogBuilder.setView(alertView)

        alertDialog = dialogBuilder.create()
        alertDialog?.setCancelable(false)
        alertDialog?.setCanceledOnTouchOutside(false)

        if (showCommentBox) {
            alertView.textDahMessage.visibility = View.GONE
            alertView.editDahComments.visibility = View.VISIBLE
        }

        alertView.textDahMessage.apply {
            text = msg
        }

        alertView.textDahHeader.apply {
            if (!title.isNullOrEmpty()) {
                text = title
            }
        }

        alertView.textDahNegativeAction.apply {
            text = negative
            setOnClickListener {
                alertDialog?.dismiss()
                lister.onNegativeClicked()
            }
        }

        alertView.textDahPositiveAction.apply {
            text = positive
            setOnClickListener {
                alertDialog?.dismiss()
                lister.onPositiveClicked()
            }
        }

        alertDialog?.show()
    }

    interface DialogPickListener {
        fun onPositiveClicked()
        fun onNegativeClicked()
    }

}