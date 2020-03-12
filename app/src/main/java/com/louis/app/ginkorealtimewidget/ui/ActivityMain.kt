package com.louis.app.ginkorealtimewidget.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.louis.app.ginkorealtimewidget.databinding.ActivityMain2Binding
import com.louis.app.ginkorealtimewidget.util.L
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel
import java.lang.IllegalArgumentException

class ActivityMain : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    /*private val viewModelFactory = PathViewModelFactory()
    private val viewModel: PathViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(PathViewModel::class.java)
    }*/
    private lateinit var pathViewModel: PathViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        pathViewModel = ViewModelProvider(this).get(PathViewModel::class.java)

        setListeners()
        observe()
    }

    private fun observe() {
        pathViewModel.currentLine.observe(this, Observer {
            val message = it?.publicWayInfo ?: "Aucune ligne trouvée pour la recherche"
            showSnackbar(message)
        })

        pathViewModel.isFetchingData.observe(this, Observer {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        })
    }

    private fun setListeners() {
        binding.buttonNext.setOnClickListener {
            val lineName = binding.inputLine.text.toString()
            pathViewModel.fetchLine(lineName)
        }
    }

    private fun lineNotFound(lineName: String) {
        showSnackbar("Aucune ligne trouvée pour la recherche $lineName")
    }

    private fun showSnackbar(message: String){
        Snackbar.make(binding.coordinator, message, Snackbar.LENGTH_LONG).show()
    }
}
