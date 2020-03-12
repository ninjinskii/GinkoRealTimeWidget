package com.louis.app.ginkorealtimewidget.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.louis.app.ginkorealtimewidget.databinding.ActivityConfig2Binding
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class ActivityConfig : AppCompatActivity() {

    private lateinit var binding: ActivityConfig2Binding
    private lateinit var pathViewModel: PathViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfig2Binding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        pathViewModel = ViewModelProvider(this).get(PathViewModel::class.java)

    }
}
