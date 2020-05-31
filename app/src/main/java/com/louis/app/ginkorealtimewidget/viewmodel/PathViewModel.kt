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
import kotlin.system.measureTimeMillis

class PathViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PathRepository
    private lateinit var repositoryFake: PathRepository

    init {
        L.timestamp("ViewModel getDatabase") {
            val pathDaoFake = PathDatabase.getInstance(application).pathDao()
            repositoryFake = PathRepository(pathDaoFake)
        }

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

        withContext(Dispatchers.Default) {
            line = lines.find { it.publicName == lineName }
        }

        return line
    }

    suspend fun fetchBusTime(path: Path): List<Time>? {
        L.thread("fetchBusTimeUI")
        val timesResponse: GinkoTimesResponse? = repository.getTimes(
                path.startingPoint.startName,
                path.line.lineId,
                path.isStartPointNaturalWay
        )

        if (timesResponse != null && timesResponse.isSuccessful) {
            return if (timesResponse.data.isEmpty()) {
                // Mock
                listOf(
                        Time((1..10).shuffled().first().toString()),
                        Time((11..20).shuffled().first().toString()),
                        Time((21..35).shuffled().first().toString())
                )
            } else {
                val response: TimeWrapper? = timesResponse.data.first()
                val verifiedBusStopName = response?.verifiedBusStopName
                response?.timeList
            }
        } else {
            L.e(FetchTimeException("An error occured while fetching times"))
            return listOf(Time("error"), Time("error"), Time("error"))
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