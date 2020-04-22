package com.louis.app.ginkorealtimewidget.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.FragmentAddLineBinding
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class FragmentAddLine : Fragment(R.layout.fragment_add_line) {
    private val pathViewModel: PathViewModel by activityViewModels()
    private var listener: OnLineAddedListener? = null
    private lateinit var binding: FragmentAddLineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(activity,
                "Ma ligne: " + pathViewModel.currentLine.value.toString(),
                Toast.LENGTH_LONG).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as? OnLineAddedListener

        if (listener == null) {
            throw ClassCastException("$context must implement FragmentAddLine#OnLineAddedListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddLineBinding.bind(view)
        binding.buttonNext.setOnClickListener {
            val requestedLine = binding.inputLine.text.toString()
            pathViewModel.fetchLine(requestedLine)
            Toast.makeText(activity, "Click", Toast.LENGTH_LONG).show()
        }
    }

    interface OnLineAddedListener {
        fun onLineAdded(lineName: String)
    }

    override fun onPause() {
        super.onPause()

        Toast.makeText(activity,
                "Ma ligne: " + pathViewModel.currentLine.value.toString(),
                Toast.LENGTH_LONG).show()
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
