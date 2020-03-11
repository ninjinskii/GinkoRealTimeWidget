package com.louis.app.ginkorealtimewidget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class PathViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PathViewModel::class.java)){
            return PathViewModel() as T
        }

        throw IllegalArgumentException("ViewModel inconnu !")
    }
}