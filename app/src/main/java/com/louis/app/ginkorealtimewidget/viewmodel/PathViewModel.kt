package com.louis.app.ginkorealtimewidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.louis.app.ginkorealtimewidget.database.PathDatabase
import com.louis.app.ginkorealtimewidget.model.Line
import com.louis.app.ginkorealtimewidget.network.GinkoApiResponse
import com.louis.app.ginkorealtimewidget.util.L
import kotlinx.coroutines.*

class PathViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PathRepository

    init {
        val pathDao = PathDatabase.getInstance(application).pathDao()
        repository = PathRepository(pathDao)
    }

    // Coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // Détermine si on est en attente de réponse de l'API ou non. (MutableLiveData en interne,
    // mais on expose un LiveData reprenant la valeur du Mutable)
    private val _isFetchingData = MutableLiveData<Boolean>()
    val isFetchingData: LiveData<Boolean>
        get() = _isFetchingData

    private val _currentLine = MutableLiveData<Line>()
    val currentLine: LiveData<Line>
        get() = _currentLine

    // Actualise la valeur du LiveData qui contient la ligne de bus voulue
    fun fetchLine(lineName: String) {
        _isFetchingData.postValue(true)

        uiScope.launch {
            val apiResponse: GinkoApiResponse? = repository.getLines()
            val lines = apiResponse?.lines
            val line = lines?.let { filterLines(it, lineName) }

            if (line.isNullOrEmpty()) {
                _currentLine.postValue(null)
                _isFetchingData.postValue(false)
                return@launch
            }

            _isFetchingData.postValue(false)
            _currentLine.postValue(line[0])
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

    override fun onCleared() {
        super.onCleared()

        _isFetchingData.postValue(false)
        viewModelJob.cancel()
    }
}