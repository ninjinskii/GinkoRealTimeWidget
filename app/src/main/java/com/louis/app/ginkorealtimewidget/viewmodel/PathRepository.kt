package com.louis.app.ginkorealtimewidget.viewmodel

import android.app.Application
import com.louis.app.ginkorealtimewidget.database.PathDao
import com.louis.app.ginkorealtimewidget.database.PathDatabase
import com.louis.app.ginkorealtimewidget.network.GinkoApi
import com.louis.app.ginkorealtimewidget.network.GinkoApiResponse
import com.louis.app.ginkorealtimewidget.util.L
import kotlinx.coroutines.Deferred

class PathRepository (pathDao: PathDao){

    suspend fun getLines(): GinkoApiResponse? {
        val apiResponse : Deferred<GinkoApiResponse> = GinkoApi.retrofitService.getLinesAsync()

        return try {
            apiResponse.await()
        } catch (e: Exception){
            L.e(e)
            null
        }
    }
}