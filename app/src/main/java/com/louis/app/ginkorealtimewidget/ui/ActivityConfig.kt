package com.louis.app.ginkorealtimewidget.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.ActivityConfig2Binding
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class ActivityConfig : FragmentActivity() {

    private lateinit var binding: ActivityConfig2Binding
    private lateinit var pathViewModel: PathViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfig2Binding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        pathViewModel = ViewModelProvider(this).get(PathViewModel::class.java)

        updateUI()
        setListeners()
    }

    // Updates the UI with information about path
    private fun updateUI() {
        pathViewModel.currentLine.observe(this, Observer { line ->
            if (line != null) {
                val oneWayLine = line.variants.find { it.naturalWay }

                with(line) {
                    binding.requestedLine.setBackgroundColor(Color.parseColor(backgroundColor))
                    binding.requestedLine.setTextColor(Color.parseColor(textColor))
                    binding.chooseEndpoint.text = String.format(
                        resources.getString(R.string.toward),
                        oneWayLine?.endPointName
                    )

                    Snackbar.make(binding.coordinator, "with", Snackbar.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "line is null", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setListeners() {
        binding.buttonNext.setOnClickListener {
            Snackbar.make(binding.coordinator, "Not implemented yet", Snackbar.LENGTH_LONG)
                .show()
        }
    }
}
