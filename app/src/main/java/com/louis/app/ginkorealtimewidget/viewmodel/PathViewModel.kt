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
    private val defaultScope = CoroutineScope(Dispatchers.Default + viewModelJob)

    // Détermine si on est en attente de réponse de l'API ou non. (MutableLiveData en interne,
    // mais on expose un LiveData reprenant la valeur du Mutable)
    private val _isFetchingData = MutableLiveData<Boolean>()
    val isFetchingData: LiveData<Boolean>
        get() = _isFetchingData

    private val _currentLine = MutableLiveData<Line>()
    val currentLine: LiveData<Line>
        get() = _currentLine

    // Actualise la valeur du LiveData qui contient la ligne de bus voulue
    fun fetchLine(requestedLineName: String) {
        _isFetchingData.postValue(true)

        // TODO: Une des opérations ne doit pas etre faite sur le threadUI
        defaultScope.launch {
            val apiResponse: GinkoApiResponse? = repository.getLines()

            if (apiResponse?.isSuccessful!!) {
                val lines = apiResponse.lines
                val line = filterLines(lines, requestedLineName)

                if (!line.isNullOrEmpty())
                    _currentLine.postValue(line[0])
                else
                    _currentLine.postValue(null)
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

    override fun onCleared() {
        super.onCleared()

        _isFetchingData.postValue(false)
        viewModelJob.cancel()
    }
}