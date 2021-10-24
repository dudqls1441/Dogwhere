package com.capstone.dogwhere

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.TextView

class CustomDialog_bbs_delete_check(context: Context) {
    private lateinit var onclickedListener: ButtonClickListener
    private val dialog = Dialog(context)


    fun mydialog(tit:String,desr: String,act:String) {
        dialog.setContentView(R.layout.bbs_cancle_check_dialog)
        val title = dialog.findViewById<TextView>(R.id.text_title)
        val description = dialog.findViewById<TextView>(R.id.text_description)
        val btn_action = dialog.findViewById<Button>(R.id.btn_action)
        val btn_close = dialog.findViewById<Button>(R.id.btn_close)

        title.setText(tit)
        description.setText(desr)
        btn_action.setText(act)
        btn_close.setText("취소")
        btn_action.setOnClickListener {
            onclickedListener.onclickAction()
            dialog.dismiss()

        }
        btn_close.setOnClickListener {
            onclickedListener.onlcickClose()
            dialog.dismiss()
        }
        dialog.show()

    }

    interface ButtonClickListener {
        fun onclickAction()
        fun onlcickClose()
    }

    fun setOnclickedListener(listener: ButtonClickListener) {
        onclickedListener = listener
    }


}
