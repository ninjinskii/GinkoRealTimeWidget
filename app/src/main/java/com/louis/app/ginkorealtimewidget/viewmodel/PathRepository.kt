package com.louis.app.ginkorealtimewidget.viewmodel

import androidx.lifecycle.LiveData
import com.louis.app.ginkorealtimewidget.database.PathDao
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.network.GinkoApi
import com.louis.app.ginkorealtimewidget.network.GinkoLinesResponse
import com.louis.app.ginkorealtimewidget.network.GinkoTimesResponse
import com.louis.app.ginkorealtimewidget.util.L
import kotlinx.coroutines.Deferred

class PathRepository(private val pathDao: PathDao) {

    suspend fun getLines(): GinkoLinesResponse? {
        val linesResponse: Deferred<GinkoLinesResponse> = GinkoApi.retrofitService.getLinesAsync()

        return try {
            linesResponse.await()
        } catch (e: Exception) {
            L.e(e)
            null
        }
    }

    suspend fun getTimes(
        busStop: String,
        idLine: String?,
        naturalWay: Boolean
    ): GinkoTimesResponse? {
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

    suspend fun insertPath(path: Path) = pathDao.insertPath(path)

    suspend fun updatePath(path: Path) = pathDao.updatePath(path)

    suspend fun setNewWidgetPath(path: Path) = pathDao.setNewWidgetPath(path)

    fun getAllPathsButCurrentPath(): LiveData<List<Path>> = pathDao.getAllPathsButCurrentPath()

    fun getWidgetPath() = pathDao.getWidgetPath()

    suspend fun getWidgetPathNotLive() = pathDao.getWidgetPathNotLive()

    fun getAllPaths() = pathDao.getAllPaths()

    suspend fun toggleSoftDeletePath(path: Path) = pathDao.toggleSoftDeletePath(path.id)

    suspend fun purgeSoftDeletedPaths() = pathDao.purgeSoftDeletedPaths()
}