package com.louis.app.ginkorealtimewidget.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.FragmentAddLineBinding
import com.louis.app.ginkorealtimewidget.util.showSnackbar
import com.louis.app.ginkorealtimewidget.viewmodel.AddPathViewModel

class FragmentAddLine : Fragment(R.layout.fragment_add_line) {
    private val addPathViewModel: AddPathViewModel by activityViewModels()
    private lateinit var binding: FragmentAddLineBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddLineBinding.bind(view)
        setListener()
        observe()
    }

    private fun observe() {
        addPathViewModel.isFetchingData.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        addPathViewModel.errorChannel.observe(viewLifecycleOwner) { error ->
            error.getContentIfNotHandled()?.let {
                binding.coordinator.showSnackbar(it)
            }
        }
    }

    private fun setListener() {
        binding.buttonNext.setOnClickListener {
            val requestedLine = binding.inputLine.text.toString()
            addPathViewModel.fetchLine(requestedLine)
        }
    }
}
