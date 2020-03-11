package com.louis.app.ginkorealtimewidget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.louis.app.ginkorealtimewidget.model.Line
import com.louis.app.ginkorealtimewidget.network.GinkoApi
import com.louis.app.ginkorealtimewidget.network.GinkoApiResponse
import com.louis.app.ginkorealtimewidget.util.L
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PathViewModel() : ViewModel() { // PathViewModel(val currentLine: Line) ?

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
        getAllLines()
    }

    private fun getAllLines(): List<Line?>? {
        var lines: List<Line?>? = emptyList()

        // Requete à l'API
        GinkoApi.retrofitService.getLines().enqueue(
                object : Callback<GinkoApiResponse> {
                    override fun onFailure(call: Call<GinkoApiResponse>, t: Throwable) {
                        L.v("La requete à l'API a échouée", "____________")
                        lines = emptyList()
                        _isFetchingData.postValue(false)
                    }

                    override fun onResponse(call: Call<GinkoApiResponse>,
                                            response: Response<GinkoApiResponse>) {
                        L.v("La requete à l'API a réussie", "_______________")
                        onLineFetched(response.body()?.lines)
                    }
                })

        return lines
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