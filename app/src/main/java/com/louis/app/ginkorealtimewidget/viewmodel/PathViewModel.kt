package com.louis.app.ginkorealtimewidget.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
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

class PathViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PathRepository

    init {
        val pathDao = PathDatabase.getInstance(application).pathDao()
        repository = PathRepository(pathDao)
    }

    // Coroutines
    private var viewModelJob = Job()
    private val defaultScope = CoroutineScope(Dispatchers.Default + viewModelJob)

    // Détermine si on est en attente de réponse de l'API ou non. (MutableLiveData en interne,
    // mais on expose un LiveData reprenant la valeur du Mutable)
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

                if (!line.isNullOrEmpty())
                    _currentLine.postValue(line[0])
                else
                    _currentLine.postValue(null)
            } else {
                L.e(NoSuchLineException("An error occured while fetching lines"))
            }

            _isFetchingData.postValue(false)
        }
    }

    // Filtre les lignes dans un background thread pour récupérer celle que l'utilisateur a choisi
    private suspend fun filterLines(lines: List<Line>, lineName: String): List<Line> {
        var line = emptyList<Line>()

        withContext(Dispatchers.Default) {
            line = lines.filter { it.publicName == lineName }
        }

        return line
    }

    fun fetchBusTime(line: Line, busStopName: String, naturalWay: Boolean): List<Time>? {
        /*runBlocking {
            val timesResponse: GinkoTimesResponse? = repository.getTimes(
                    busStopName,
                    line.lineId,
                    naturalWay
            )

            if (timesResponse!!.isSuccessful) {
                if (timesResponse.data.isEmpty()) {
                    listOf(Time("9999"), Time("9999"), Time("9999"))
                } else {
                    val response: TimeWrapper? = timesResponse.data.first()
                    val verifiedBusStopName = response?.verifiedBusStopName
                    listOf(Time("10"), Time("10"), Time("10"))
                }
            } else {
                L.e(FetchTimeException("An error occured while fetching times"))
                listOf(Time("0"), Time("0"), Time("0"))
            }
        }

        return null*/

        return runBlocking {
            val timesResponse: GinkoTimesResponse? = repository.getTimes(
                    busStopName,
                    line.lineId,
                    naturalWay
            )

            if (timesResponse != null && timesResponse.isSuccessful) {
                if (timesResponse.data.isEmpty()) {
                    listOf(Time("9999"), Time("9999"), Time("9999"))
                } else {
                    val response: TimeWrapper? = timesResponse.data.first()
                    val verifiedBusStopName = response?.verifiedBusStopName
                    response!!.timeList
                }
            } else {
                L.e(FetchTimeException("An error occured while fetching times"))
                listOf(Time("0"), Time("0"), Time("0"))
            }
        }
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