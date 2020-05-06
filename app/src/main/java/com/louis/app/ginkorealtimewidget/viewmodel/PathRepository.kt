package com.louis.app.ginkorealtimewidget.viewmodel

import com.louis.app.ginkorealtimewidget.database.PathDao
import com.louis.app.ginkorealtimewidget.network.GinkoApi
import com.louis.app.ginkorealtimewidget.network.GinkoLinesResponse
import com.louis.app.ginkorealtimewidget.network.GinkoTimesResponse
import com.louis.app.ginkorealtimewidget.util.L
import kotlinx.coroutines.Deferred

class PathRepository(pathDao: PathDao) {

    suspend fun getLines(): GinkoLinesResponse? {
        val linesResponse: Deferred<GinkoLinesResponse> = GinkoApi.retrofitService.getLinesAsync()

        return try {
            linesResponse.await()
        } catch (e: Exception) {
            L.e(e)
            null
        }
    }

    suspend fun getTimes(busStop: String,
                         idLine: String?,
                         naturalWay: Boolean) : GinkoTimesResponse? {
        idLine ?: return null

        val timesResponse: Deferred<GinkoTimesResponse> = GinkoApi.retrofitService.getTimesAsync(
                busStop,
                idLine,
                naturalWay
        )

        return try {
            timesResponse.await()
        } catch (e: Exception) {
            L.e(e)
            return null
        }
    }
}