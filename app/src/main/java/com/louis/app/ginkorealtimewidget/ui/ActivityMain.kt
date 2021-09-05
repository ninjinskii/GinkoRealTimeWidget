package com.louis.app.ginkorealtimewidget.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.ActivityMainBinding
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class ActivityMain : FragmentActivity() {

    private val pathViewModel: PathViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)
    }

    override fun onDestroy() {
        pathViewModel.purgeSoftDeletePaths()
        super.onDestroy()
    }
}
