package com.louis.app.ginkorealtimewidget.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.louis.app.ginkorealtimewidget.databinding.ActivityMain2Binding

class ActivityMain : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        setListeners();
    }

    private fun setListeners() {
        binding.inputLine.setOnClickListener {
            // Passer au viewModel
        }
    }
}
