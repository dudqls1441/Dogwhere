package com.capstone.dogwhere

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.TextView
import java.util.zip.Inflater

class CustomDialog(context : Context)  {
    private lateinit var onclickedListener: ButtonClickListener
    private val dialog = Dialog(context)





    fun mydialog(){
        dialog.setContentView(R.layout.register_matching_dialog)
        val title = dialog.findViewById<TextView>(R.id.text_title)
        val description = dialog.findViewById<TextView>(R.id.text_description)
        val btn_matchingList = dialog.findViewById<Button>(R.id.btn_go_matchingList)
        val btn_close = dialog.findViewById<Button>(R.id.btn_close)

        title.setText("매칭 참여 완료")
        description.setText("매칭 참여 신청이 완료되었습니다")
        btn_matchingList.setText("매칭 리스트로")
        btn_close.setText("닫기")
        btn_matchingList.setOnClickListener {
            onclickedListener.onclickMyMatchingList()
            dialog.dismiss()

        }
        btn_close.setOnClickListener {
            onclickedListener.onlcickClose()
            dialog.dismiss()
        }
        dialog.show()

    }
    interface ButtonClickListener{
        fun onclickMyMatchingList()
        fun onlcickClose()
    }

    fun setOnclickedListener(listener: ButtonClickListener){
        onclickedListener = listener
    }


}
