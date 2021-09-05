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
import com.louis.app.ginkorealtimewidget.network.GinkoLinesResponse
import com.louis.app.ginkorealtimewidget.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private var _currentLine = MutableLiveData<Event<Line>>()
    val currentLine: LiveData<Event<Line>>
        get() = _currentLine

    private val _currentPath = MutableLiveData<Event<Path>>()
    val currentPath: LiveData<Event<Path>>
        get() = _currentPath

    private val _errorChannel = MutableLiveData<Event<Int>>()
    val errorChannel: LiveData<Event<Int>>
        get() = _errorChannel

    fun fetchLine(requestedLineName: String) {
        _isFetchingData.postValue(true)

        viewModelScope.launch(IO) {
            val linesResponse: GinkoLinesResponse? = repository.getLines()
            if (linesResponse?.isSuccessful == true) {
                val lines = linesResponse.lines
                val line = filterLines(lines, requestedLineName)

                if (line != null) _currentLine.postValue(Event(line))
                else _errorChannel.postValue(Event(R.string.CONFIG_lineError))
            } else {
                _errorChannel.postValue(Event(R.string.appError))
            }

            _isFetchingData.postValue(false)
        }
    }

    private suspend fun filterLines(lines: List<Line>, lineName: String) =
        withContext(Dispatchers.Default) { lines.find { it.publicName == lineName } }

    fun savePath(path: Path) {
        _currentPath.postValue(Event(path))
        viewModelScope.launch { repository.insertPath(path) }
    }

    override fun onCleared() {
        super.onCleared()

        _isFetchingData.postValue(false)
    }
}
