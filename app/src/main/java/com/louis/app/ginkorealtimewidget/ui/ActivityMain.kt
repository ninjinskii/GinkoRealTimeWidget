package com.louis.app.ginkorealtimewidget.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.louis.app.ginkorealtimewidget.databinding.ActivityMain2Binding
import com.louis.app.ginkorealtimewidget.model.Line
import com.louis.app.ginkorealtimewidget.util.L
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModelFactory

class ActivityMain : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    private val viewModelFactory = PathViewModelFactory()
    private val viewModel: PathViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(PathViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        setListeners()
        observe()
    }

    private fun observe() {
        viewModel.currentLine.observe(this, Observer {
            Toast.makeText(this, "Trajet de la ligne ${it.publicWayInfo}", Toast.LENGTH_LONG).show()
        })
    }

    private fun setListeners() {
        binding.buttonNext.setOnClickListener {
            viewModel.fetchLine(binding.inputLine.text.toString())
        }
    }
}
