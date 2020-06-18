package com.louis.app.ginkorealtimewidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.louis.app.ginkorealtimewidget.database.PathDatabase
import com.louis.app.ginkorealtimewidget.model.Line
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.model.Time
import com.louis.app.ginkorealtimewidget.model.VariantWithoutLine
import com.louis.app.ginkorealtimewidget.network.GinkoLinesResponse
import com.louis.app.ginkorealtimewidget.util.L
import com.louis.app.ginkorealtimewidget.util.NoSuchLineException
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO

class PathViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PathRepository

    init {
        // On some device, this is taking too much time to load when app is restarted from scratch
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

    // TODO: refactor
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

    // TODO: avoid runBlocking
    fun getUserPaths(): LiveData<List<Path>> = runBlocking { repository.getAllPathsButCurrentPath() }

    fun getUserWidgetPath(): LiveData<Path> = runBlocking { repository.getWidgetPath() }

    fun savePath(path: Path) {
        defaultScope.launch {
            if (validatePath(path)) {
                repository.insertPath(path)
                _currentPath.postValue(path)
            } else {
                _currentPath.postValue(null)
            }
        }
    }

    // TODO: move into Path class
    private suspend fun validatePath(path: Path): Boolean {
        return defaultScope.async {
            path.line.variants.forEach {
                val variantDetails = repository.getVariantDetails(path.line.lineId, it.idVariant)
                // Looking for one variant with startpoint then endpoint into it.
                // Each variant is sort by geographical order by the API.
                if (variantDetails != null) {
                    val startName = path.startingPoint.startName
                    val endName = path.endingPoint.endName
                    var foundedVariant: VariantWithoutLine? = null

                    for (variant in variantDetails.variants) {
                        if (variant.name == startName && foundedVariant == null) {
                            foundedVariant = variant
                        } else if (variant.name == endName && foundedVariant != null) {
                            // We found first array then second array, path is valid
                            return@async true
                        }
                    }
                }
            }

            return@async false
        }.await()
    }

    fun updatePath(path: Path) = defaultScope.launch { repository.updatePath(path) }

    fun resetWidgetPath() = defaultScope.launch { repository.resetWidgetPath() }

    override fun onCleared() {
        super.onCleared()

        _isFetchingData.postValue(false)
        viewModelJob.cancel()
    }
}