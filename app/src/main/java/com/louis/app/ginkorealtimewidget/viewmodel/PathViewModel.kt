package com.louis.app.ginkorealtimewidget.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.louis.app.ginkorealtimewidget.database.PathDatabase
import com.louis.app.ginkorealtimewidget.model.*
import com.louis.app.ginkorealtimewidget.network.GinkoLinesResponse
import com.louis.app.ginkorealtimewidget.network.GinkoTimesResponse
import com.louis.app.ginkorealtimewidget.util.FetchTimeException
import com.louis.app.ginkorealtimewidget.util.L
import com.louis.app.ginkorealtimewidget.util.NoSuchLineException
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import java.lang.Exception
import kotlin.system.measureTimeMillis

class PathViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PathRepository

    init {
        // TODO: Fix Slow DB init
        // This is taking to much time to load when is restarted from scratch
        val pathDao = PathDatabase.getInstance(application).pathDao()
        repository = PathRepository(pathDao)
    }

    private var viewModelJob = Job()
    private val defaultScope = CoroutineScope(IO + viewModelJob)

    private val _isFetchingData = MutableLiveData<Boolean>()
    val isFetchingData: LiveData<Boolean>
        get() = _isFetchingData

    private val _currentLine = MutableLiveData<Line>()
    val currentLine: LiveData<Line>
        get() = _currentLine

    private val _currentTimes = MutableLiveData<Pair<List<Time>?, List<Time>?>>()
    val currentTimes: LiveData<Pair<List<Time>?, List<Time>?>>
        get() = _currentTimes

    private val _currentPath = MutableLiveData<Path>()
    val currentPath: LiveData<Path>
        get() = _currentPath

    fun fetchLine(requestedLineName: String) {
        _isFetchingData.postValue(true)

        defaultScope.launch {
            val linesResponse: GinkoLinesResponse? = repository.getLines()
            if (linesResponse?.isSuccessful == true) {
                val lines = linesResponse.lines
                val line = filterLines(lines, requestedLineName)
                line.let { _currentLine.postValue(it) }
            } else {
                L.e(NoSuchLineException("An error occured while fetching lines"))
            }

            _isFetchingData.postValue(false)
        }
    }

    private suspend fun filterLines(lines: List<Line>, lineName: String) = withContext(Default) {
        lines.find { it.publicName == lineName }
    }

    // TODO: refactor redundancy
    fun fetchBusTime(path: Path) {
        // Allows us to know if we have to update path with correct name
        var isBusStopNameMispelled = false

        fun mockTimes() = listOf(
                Time((1..10).shuffled().first().toString()),
                Time((11..20).shuffled().first().toString()),
                Time((21..35).shuffled().first().toString())
        )

        defaultScope.launch {
            val timesResponseStartPoint = repository.getTimes(
                    path.startingPoint.startName,
                    path.line.lineId,
                    path.isStartPointNaturalWay
            )?.data
            val startTimeAndName = timesResponseStartPoint?.firstOrNull()?.let {
                try {
                    it.timeList to it.verifiedBusStopName
                } catch (e: NoSuchElementException) {
                    L.v("Aucun bus prévu pour le moment")
                    L.e(e)
                    mockTimes() to it.verifiedBusStopName
                }
            }

            val timesResponseEndPoint = repository.getTimes(
                    path.endingPoint.endName,
                    path.line.lineId,
                    path.isStartPointNaturalWay
            )?.data
            val endTimeAndName = timesResponseEndPoint?.firstOrNull()?.let {
                try {
                    it.timeList to it.verifiedBusStopName
                } catch (e: NoSuchElementException) {
                    L.v("Aucun bus prévu pour le moment")
                    L.e(e)
                    mockTimes() to it.verifiedBusStopName
                }
            }


            if (path.startingPoint.startName != startTimeAndName?.second || path.endingPoint.endName != endTimeAndName?.second) {
                path.startingPoint.startName = startTimeAndName?.second.toString()
                path.endingPoint.endName = endTimeAndName?.second.toString()
                updatePath(path)
            }

            _currentTimes.postValue(startTimeAndName?.first to endTimeAndName?.first)
        }
    }

    /*private fun readGinkoTimesResponse(response: GinkoTimesResponse?, path: Path, point: Any): List<Time>? {
        var isBusStopNameMispelled = false

        fun mockTimes() = listOf(
                Time((1..10).shuffled().first().toString()),
                Time((11..20).shuffled().first().toString()),
                Time((21..35).shuffled().first().toString())
        )

        val result = if (response != null && response.isSuccessful) {
            if (response.data.isEmpty()) {
                mockTimes() //return
            } else {
                val times: TimeWrapper? = response.data.first()
                if (point is StartPoint) {
                    if (path.startingPoint.startName != times?.verifiedBusStopName) {
                        path.startingPoint.startName = times?.verifiedBusStopName
                                ?: path.startingPoint.startName
                        isBusStopNameMispelled = true
                    }
                } else if (point is EndPoint) {
                    if (path.endingPoint.endName != times?.verifiedBusStopName) {
                        path.endingPoint.endName = times?.verifiedBusStopName
                                ?: path.endingPoint.endName
                        isBusStopNameMispelled = true
                    }
                }
                times?.timeList
            }
        } else {
            L.e(FetchTimeException("An error occured while fetching times"))
            listOf(Time("error"), Time("error"), Time("error"))
        }

        if (isBusStopNameMispelled) updatePath(path)

        return result
    }*/

    fun getUserPaths(): LiveData<List<Path>> = runBlocking { repository.getAllPathsButCurrentPath() }

    fun getUserWidgetPath(): LiveData<Path> = runBlocking { repository.getWidgetPath() }

    fun savePath(path: Path) {
        _currentPath.postValue(path)
        defaultScope.launch {
            repository.insertPath(path)
        }
    }

    fun updatePath(path: Path) = defaultScope.launch { repository.updatePath(path) }

    fun resetWidgetPath() = defaultScope.launch { repository.resetWidgetPath() }

    override fun onCleared() {
        super.onCleared()

        _isFetchingData.postValue(false)
        viewModelJob.cancel()
    }
}