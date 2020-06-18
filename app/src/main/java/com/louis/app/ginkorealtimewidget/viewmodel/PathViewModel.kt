package com.louis.app.ginkorealtimewidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.louis.app.ginkorealtimewidget.database.PathDatabase
import com.louis.app.ginkorealtimewidget.model.Line
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.model.Time
import com.louis.app.ginkorealtimewidget.model.TimeWrapper
import com.louis.app.ginkorealtimewidget.network.GinkoLinesResponse
import com.louis.app.ginkorealtimewidget.util.L
import com.louis.app.ginkorealtimewidget.util.NoSuchLineException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    // TODO: move fetching of busstops times into repository, and finish this method refactoring
    fun fetchBusTimes(path: Path) {
        _isFetchingData.postValue(true)

        val results = defaultScope.launch {
            val startPointTimes = repository.getTimes(path.startingPoint.getName(),
                    path.line.lineId,
                    path.isStartPointNaturalWay)

            val endPointTimes = repository.getTimes(path.endingPoint.getName(),
                    path.line.lineId,
                    !path.isStartPointNaturalWay)

            if (startPointTimes?.isSuccessful == true && endPointTimes?.isSuccessful == true) {
                startPointTimes.data.firstOrNull().let {

                }
            }

            L.v(startPointTimes.toString())


        }

        _isFetchingData.postValue(false)
        _currentTimes.postValue(null)
    }

    fun getUserPaths(): LiveData<List<Path>> = repository.getAllPathsButCurrentPath()

    fun getUserWidgetPath(): LiveData<Path> = repository.getWidgetPath()

    fun savePath(path: Path) {
        _currentPath.postValue(path)
        defaultScope.launch { repository.insertPath(path) }
    }

    fun updatePath(path: Path) = defaultScope.launch { repository.updatePath(path) }

    fun resetWidgetPath() = defaultScope.launch { repository.resetWidgetPath() }

    override fun onCleared() {
        super.onCleared()

        _isFetchingData.postValue(false)
        viewModelJob.cancel()
    }
}