package com.louis.app.ginkorealtimewidget.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.FragmentAddPathBinding
import com.louis.app.ginkorealtimewidget.model.EndPoint
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.model.StartPoint
import com.louis.app.ginkorealtimewidget.util.L
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class FragmentAddPath : Fragment(R.layout.fragment_add_path) {
    private val pathViewModel: PathViewModel by activityViewModels()
    private lateinit var binding: FragmentAddPathBinding
    private var naturalWay: Boolean? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddPathBinding.bind(view)
        updateUI()
        setListeners()
        observe()

    }

    private fun updateUI() {
        val line = pathViewModel.currentLine.value
        with(binding.requestedLine) {
            text = line?.publicName
            setTextColor(Color.parseColor("#${line?.textColor}"))
            setBackgroundColor(Color.parseColor("#${line?.backgroundColor}"))
        }

        // Display the one way line to let the user choose the direction
        val firstOneWayLine = line?.variants?.firstOrNull()
        binding.chooseEndpoint.text =
                resources.getString(R.string.toward, firstOneWayLine?.endPointName)
        naturalWay = firstOneWayLine?.naturalWay
    }

    private fun setListeners() {
        binding.buttonNext.setOnClickListener {
            val busStop1 = StartPoint(binding.inputBusStop1.text.toString())
            val busStop2 = EndPoint(binding.inputBusStop2.text.toString())
            val line = pathViewModel.currentLine.value
            val isNaturalWay = binding.chooseEndpoint.isChecked

            if (line != null) {
                pathViewModel.savePath(Path(busStop1, busStop2, line,  isNaturalWay))
            } else {
                // user feedback
            }
        }
    }

    private fun observe() {
//        pathViewModel.currentTimes.observe(viewLifecycleOwner, Observer {
//            Toast.makeText(activity, "New times! : ${it?.get(0)}, ${it?.get(0)}", Toast.LENGTH_LONG).show()
//        })

        pathViewModel.isFetchingData.observe(viewLifecycleOwner, Observer {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        })
    }
}
