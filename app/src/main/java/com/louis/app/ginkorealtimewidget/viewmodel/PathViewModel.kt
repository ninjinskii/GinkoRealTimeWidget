package com.louis.app.ginkorealtimewidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.database.PathDatabase
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.model.Time
import com.louis.app.ginkorealtimewidget.network.GinkoTimesResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class PathViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PathRepository

    init {
        // On some device, this is taking too much time to load when app is restarted from scratch
        val pathDao = PathDatabase.getInstance(application).pathDao()
        repository = PathRepository(pathDao)
    }

    private val _currentTimes = MutableLiveData<Pair<List<Time>, List<Time>>>()
    val currentTimes: LiveData<Pair<List<Time>, List<Time>>>
        get() = _currentTimes

    fun fetchBusTimes(path: Path) {
        viewModelScope.launch(IO) {
            val startResponse = repository.getTimes(
                path.startingPoint,
                path.line.lineId,
                path.isStartPointNaturalWay
            )

            val endResponse = repository.getTimes(
                path.endingPoint,
                path.line.lineId,
                !path.isStartPointNaturalWay
            )

            val startTimes = handleBusTimesResponse(startResponse, path.startingPoint)
            val endTimes = handleBusTimesResponse(endResponse, path.endingPoint)

            with(path) {
                if (startingPoint != startTimes.first || endingPoint != endTimes.first) {
                    startingPoint = startTimes.first
                    endingPoint = endTimes.first
                    repository.updatePath(this)
                }
            }

            _currentTimes.postValue(startTimes.second to endTimes.second)
        }
    }

    // [rawName] is the user input concerning the busStop
    private fun handleBusTimesResponse(
        response: GinkoTimesResponse?,
        rawName: String
    ): Pair<String, List<Time>> {
        if (response != null && response.isSuccessful) {
            response.data.firstOrNull()?.let {
                return it.verifiedBusStopName to it.timeList
            }
        }

        val label = getApplication<Application>().resources.getString(R.string.noBuses)
        return rawName to listOf(Time(label), Time(""), Time(""))
    }

    fun getUserPaths(): LiveData<List<Path>> = repository.getAllPathsButCurrentPath()

    fun getUserWidgetPath(): LiveData<Path> = repository.getWidgetPath()

    fun setNewWidgetPath(path: Path) =
        viewModelScope.launch(IO) { repository.setNewWidgetPath(path) }

    fun toggleSoftDeletePath(path: Path) =
        viewModelScope.launch(IO) { repository.toggleSoftDeletePath(path) }

    fun purgeSoftDeletePaths() =
        viewModelScope.launch(IO) { repository.purgeSoftDeletedPaths() }
}
