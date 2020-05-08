package com.louis.app.ginkorealtimewidget.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.FragmentAddLineBinding
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class FragmentAddLine : Fragment(R.layout.fragment_add_line) {
    private val pathViewModel: PathViewModel by activityViewModels()
    private lateinit var binding: FragmentAddLineBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddLineBinding.bind(view)
        setListener()
        observe()
    }

    private fun observe() {
        pathViewModel.isFetchingData.observe(viewLifecycleOwner, Observer {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        })
    }

    private fun setListener() {
        binding.buttonNext.setOnClickListener {
            val requestedLine = binding.inputLine.text.toString()
            pathViewModel.fetchLine(requestedLine)
        }
    }
}
