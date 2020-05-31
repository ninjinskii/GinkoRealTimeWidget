package com.louis.app.ginkorealtimewidget.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.louis.app.ginkorealtimewidget.database.PathDatabase
import com.louis.app.ginkorealtimewidget.model.Line
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.model.Time
import com.louis.app.ginkorealtimewidget.model.TimeWrapper
import com.louis.app.ginkorealtimewidget.network.GinkoLinesResponse
import com.louis.app.ginkorealtimewidget.network.GinkoTimesResponse
import com.louis.app.ginkorealtimewidget.util.FetchTimeException
import com.louis.app.ginkorealtimewidget.util.L
import com.louis.app.ginkorealtimewidget.util.NoSuchLineException
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlin.system.measureTimeMillis

class PathViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PathRepository

    init {
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

    private val _currentTimes = MutableLiveData<List<Time>>()
    val currentTimes: LiveData<List<Time>>
        get() = _currentTimes

    private val _currentPath = MutableLiveData<Path>()
    val currentPath: LiveData<Path>
        get() = _currentPath

    // Actualise la valeur du LiveData qui contient la ligne de bus voulue
    fun fetchLine(requestedLineName: String) {
        _isFetchingData.postValue(true)

        defaultScope.launch {
            val linesResponse: GinkoLinesResponse? = repository.getLines()
            if (linesResponse!!.isSuccessful) {
                val lines = linesResponse.lines
                val line = filterLines(lines, requestedLineName)
                if (line != null) {
                    _currentLine.postValue(line!!)
                }
            } else {
                L.e(NoSuchLineException("An error occured while fetching lines"))
            }

            _isFetchingData.postValue(false)
        }
    }

    // Filtre les lignes dans un background thread pour récupérer celle que l'utilisateur a choisi
    private suspend fun filterLines(lines: List<Line>, lineName: String): Line? {
        var line: Line? = null

        withContext(Default) {
            line = lines.find { it.publicName == lineName }
        }

        return line
    }

    // TODO: remove suspend modifier and use livedata to get result from coroutine
    suspend fun fetchBusTime(path: Path): Pair<List<Time>?, List<Time>?> {
        L.thread("fetchBusTimeUI")
        val timesResponseStartPoint: GinkoTimesResponse? = repository.getTimes(
                path.startingPoint.startName,
                path.line.lineId,
                path.isStartPointNaturalWay
        )

        val timesResponseEndPoint: GinkoTimesResponse? = repository.getTimes(
                path.endingPoint.endName,
                path.line.lineId,
                path.isStartPointNaturalWay
        )

        val resultStartPoint = if (timesResponseStartPoint != null && timesResponseStartPoint.isSuccessful) {
             if (timesResponseStartPoint.data.isEmpty()) {
                // Mock
                listOf(
                        Time((1..10).shuffled().first().toString()),
                        Time((11..20).shuffled().first().toString()),
                        Time((21..35).shuffled().first().toString())
                )
            } else {
                val response: TimeWrapper? = timesResponseStartPoint.data.first()
                path.startingPoint.startName = response?.verifiedBusStopName
                        ?: path.startingPoint.startName

                response?.timeList
            }
        } else {
            L.e(FetchTimeException("An error occured while fetching times"))
            listOf(Time("error"), Time("error"), Time("error"))
        }

        val resultEndPoint = if (timesResponseEndPoint != null && timesResponseEndPoint.isSuccessful) {
            if (timesResponseEndPoint.data.isEmpty()) {
                // Mock
                listOf(
                        Time((1..10).shuffled().first().toString()),
                        Time((11..20).shuffled().first().toString()),
                        Time((21..35).shuffled().first().toString())
                )
            } else {
                val response: TimeWrapper? = timesResponseEndPoint.data.first()
                path.endingPoint.endName = response?.verifiedBusStopName
                        ?: path.endingPoint.endName

                response?.timeList
            }
        } else {
            L.e(FetchTimeException("An error occured while fetching times"))
            listOf(Time("error"), Time("error"), Time("error"))
        }

        updatePath(path)

        return resultStartPoint to resultEndPoint
    }

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