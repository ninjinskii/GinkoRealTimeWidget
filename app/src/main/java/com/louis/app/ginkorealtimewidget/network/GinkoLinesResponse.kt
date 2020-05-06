package com.louis.app.ginkorealtimewidget.network

import com.louis.app.ginkorealtimewidget.model.Line
import com.squareup.moshi.Json

// L'api Ginko renvoit systématiquement un objet contenant une booléen indiquant si la requete
// s'est bien déroulée ou non et un message, le tout accompagné d'un tableau d'objet, ce dernier
// étant l'information qui nous intéresse. Cette classe représente la racine de la réponse de l'API
// Original API response (URL: https://api.ginko.voyage/DR/getLignes.do
class GinkoLinesResponse(@Json(name = "ok") val isSuccessful: Boolean,
                         @Json(name = "objets") val lines: List<Line>)