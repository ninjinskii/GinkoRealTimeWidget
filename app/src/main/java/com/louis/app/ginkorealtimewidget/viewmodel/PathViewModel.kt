package com.louis.app.ginkorealtimewidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.louis.app.ginkorealtimewidget.database.PathDatabase
import com.louis.app.ginkorealtimewidget.model.Line
import com.louis.app.ginkorealtimewidget.network.GinkoApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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
            val lines = apiResponse.let {
                it?.lines
            }

            _currentLine.postValue(lines?.first { it.publicName == lineName })
            _isFetchingData.postValue(false)
        }
    }

    private fun onLineFetched(lines: List<Line?>?) {
        _currentLine.postValue(lines?.get(0))
        _isFetchingData.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()

        viewModelJob.cancel()
    }
}