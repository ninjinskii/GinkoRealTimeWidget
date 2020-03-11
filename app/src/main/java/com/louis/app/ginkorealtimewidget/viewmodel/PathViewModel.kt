package com.louis.app.ginkorealtimewidget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.louis.app.ginkorealtimewidget.model.Line
import com.louis.app.ginkorealtimewidget.network.GinkoApi
import com.louis.app.ginkorealtimewidget.network.GinkoApiResponse
import com.louis.app.ginkorealtimewidget.util.L
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PathViewModel : ViewModel() { // PathViewModel(val currentLine: Line) ?

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

        CoroutineScope(viewModelJob + Dispatchers.Main).launch {
            val apiResponse : Deferred<GinkoApiResponse> = GinkoApi.retrofitService.getLines()

            try {
                val response: GinkoApiResponse = apiResponse.await()

                _currentLine.postValue(response.lines.first {
                    it.publicName == lineName
                })

                _isFetchingData.postValue(false)
            } catch (e: Exception){
                L.e(e)
                _isFetchingData.postValue(false)
            }
        }
    }

    private fun onLineFetched(lines: List<Line?>?){
        _currentLine.postValue(lines?.get(0))
        _isFetchingData.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()

        viewModelJob.cancel()
    }
}