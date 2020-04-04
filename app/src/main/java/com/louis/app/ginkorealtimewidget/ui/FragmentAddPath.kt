package com.louis.app.ginkorealtimewidget.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentAddPath.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentAddPath : Fragment(R.layout.fragment_add_path) {
    // TODO: Rename and change types of parameters
    private lateinit var pathViewModel: PathViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pathViewModel = ViewModelProvider(this).get(PathViewModel::class.java)
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
