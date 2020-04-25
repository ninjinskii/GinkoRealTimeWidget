package com.louis.app.ginkorealtimewidget.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
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
            Toast.makeText(activity, "Click", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAddPath.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentAddPath().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
