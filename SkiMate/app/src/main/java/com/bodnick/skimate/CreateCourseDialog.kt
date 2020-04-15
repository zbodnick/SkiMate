package com.bodnick.skimate

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Layout
import android.view.Window

class CreateCourseDialog(context: Context) : Dialog(context) {

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_create_course)




    }
}