package com.louis.app.ginkorealtimewidget.util

import android.util.Log

class L {
    companion object{
        fun v(message: String, clue: String = "Default") {
            Log.v("________$clue _______", message)
        }
    }
}
