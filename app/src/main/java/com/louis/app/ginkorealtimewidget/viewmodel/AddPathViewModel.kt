package com.louis.app.ginkorealtimewidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.database.PathDatabase
import com.louis.app.ginkorealtimewidget.model.Line
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.model.Time
import com.louis.app.ginkorealtimewidget.network.GinkoLinesResponse
import com.louis.app.ginkorealtimewidget.network.GinkoTimesResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class AddPathViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PathRepository

    init {
        // On some device, this is taking too much time to load when app is restarted from scratch
        val pathDao = PathDatabase.getInstance(application).pathDao()
        repository = PathRepository(pathDao)
    }

    private val _isFetchingData = MutableLiveData<Boolean>()
    val isFetchingData: LiveData<Boolean>
        get() = _isFetchingData

    private var _currentLine = MutableLiveData<Line>()
    val currentLine: LiveData<Line>
        get() = _currentLine

    private val _currentTimes = MutableLiveData<Pair<List<Time>, List<Time>>>()
    val currentTimes: LiveData<Pair<List<Time>, List<Time>>>
        get() = _currentTimes

    private val _currentPath = MutableLiveData<Path>()
    val currentPath: LiveData<Path>
        get() = _currentPath

    private val _errorChannel = MutableLiveData<Int>()
    val errorChannel: LiveData<Int>
        get() = _errorChannel

    fun fetchLine(requestedLineName: String) {
        _isFetchingData.postValue(true)

        viewModelScope.launch(IO) {
            val linesResponse: GinkoLinesResponse? = repository.getLines()
            if (linesResponse?.isSuccessful == true) {
                val lines = linesResponse.lines
                val line = filterLines(lines, requestedLineName)
                line.let { _currentLine.postValue(it) }
            } else {
                _errorChannel.postValue(R.string.appError)
            }

            _isFetchingData.postValue(false)
        }
    }

    private suspend fun filterLines(lines: List<Line>, lineName: String) =
        withContext(Dispatchers.Default) { lines.find { it.publicName == lineName } }

    fun savePath(path: Path) {
        _currentPath.postValue(path)
        _currentLine = MutableLiveData()    // Provisional fix
        viewModelScope.launch { repository.insertPath(path) }
    }

    override fun onCleared() {
        super.onCleared()

        _isFetchingData.postValue(false)
    }
}