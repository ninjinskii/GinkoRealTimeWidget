package com.louis.app.ginkorealtimewidget.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider

import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.model.Line
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel
import java.lang.ClassCastException

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentAddPath.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentAddLine : Fragment(R.layout.fragment_add_line) {
    private val pathViewModel: PathViewModel by activityViewModels()
    private var listener: OnLineAddedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(activity, "Ma ligne: " + pathViewModel.currentLine.value.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as? OnLineAddedListener

        if(listener == null) {
            throw ClassCastException("$context must implement OnLineAdded")
        }
    }

    interface OnLineAddedListener {
        fun onLineAdded(lineName: String)
    }

    override fun onPause() {
        super.onPause()

        Toast.makeText(activity, "Ma ligne: " + pathViewModel.currentLine.value.toString(), Toast.LENGTH_LONG).show()
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
