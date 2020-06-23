package com.louis.app.ginkorealtimewidget.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PathViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PathViewModel::class.java)) {
            return PathViewModel(application = Application()) as T
        }

        throw IllegalArgumentException("ViewModel inconnu !")
    }
}