package com.louis.app.ginkorealtimewidget.network

class ApiConstants {
    companion object{
        const val BASE_URL = "https://api.ginko.voyage"
        const val URL_GET_LINES = "/DR/getLignes.do"
        const val URL_GET_TIMES = "/TR/getListeTemps.do"
        const val URL_GET_VARIANT_DETAILS = "/DR/getDetailsVariante.do?idLigne=\"101\"&idVariante=\"15-0\""
        const val ARG_BUS_STOP_NAME = "listeNoms"
        const val ARG_LINES_ID = "listeIdLignes"
        const val ARG_LINE_WAY = "listeSensAller"
        const val ARG_LINE_ID = "idLigne"
        const val ARG_VARIANT_ID = "idLigne"
    }
}