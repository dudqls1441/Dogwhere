package com.capstone.dogwhere

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.TextView

class CustomDialog_Cancle_Check(context: Context) {
    private lateinit var onclickedListener: ButtonClickListener
    private val dialog = Dialog(context)


    fun mydialog() {
        dialog.setContentView(R.layout.cancle_check_matching_dialog)
        val title = dialog.findViewById<TextView>(R.id.text_title)
        val description = dialog.findViewById<TextView>(R.id.text_description)
        val btn_cancle = dialog.findViewById<Button>(R.id.btn_cancle)
        val btn_close = dialog.findViewById<Button>(R.id.btn_close)

        title.setText("매칭 참여")
        description.setText("매칭을 취소하시겠습니까?")
        btn_cancle.setText("매칭 취소")
        btn_close.setText("닫기")
        btn_cancle.setOnClickListener {
            onclickedListener.onclickCancle()
            dialog.dismiss()

        }
        btn_close.setOnClickListener {
            onclickedListener.onlcickClose()
            dialog.dismiss()
        }
        dialog.show()

    }

    interface ButtonClickListener {
        fun onclickCancle()
        fun onlcickClose()
    }

    fun setOnclickedListener(listener: ButtonClickListener) {
        onclickedListener = listener
    }


}
