package com.louis.app.ginkorealtimewidget.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.FragmentAddPathBinding
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.util.showSnackbar
import com.louis.app.ginkorealtimewidget.viewmodel.AddPathViewModel

class FragmentAddPath : Fragment(R.layout.fragment_add_path) {
    private val addPathViewModel: AddPathViewModel by activityViewModels()
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
        val line = addPathViewModel.currentLine.value?.peekContent()
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
            val startingPoint = binding.inputBusStop1.text.toString()
            val endingPoint = binding.inputBusStop2.text.toString()
            val line = addPathViewModel.currentLine.value?.getContentIfNotHandled()
            val isNaturalWay = binding.chooseEndpoint.isChecked

            if (line != null)
                addPathViewModel.savePath(Path(startingPoint, endingPoint, line, isNaturalWay))
            else binding.coordinator.showSnackbar(R.string.appError)
        }
    }

    private fun observe() {
        addPathViewModel.isFetchingData.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }
}
