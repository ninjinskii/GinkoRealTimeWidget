package com.louis.app.ginkorealtimewidget.network

class ApiConstants {
    companion object{
        const val BASE_URL = "https://api.ginko.voyage"
        const val URL_GET_LINES = "/DR/getLignes.do"
        const val URL_GET_TIMES = "/TR/getListeTemps.do?"
        const val URL_GET_TIMES_ARG_BUS_STOP_NAME = "listeNoms="
        const val URL_GET_TIMES_ARG_LINE_ID = "listeIdLignes="
        const val URL_GET_TIMES_ARG_LINE_WAY = "listeSensAller="
    }
}