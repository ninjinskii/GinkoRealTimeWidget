package com.louis.app.ginkorealtimewidget.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider

import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.FragmentAddPathBinding
import com.louis.app.ginkorealtimewidget.databinding.FragmentSeePathsBinding
import com.louis.app.ginkorealtimewidget.util.L
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class FragmentAddPath : Fragment(R.layout.fragment_add_path) {
    private val pathViewModel: PathViewModel by activityViewModels()
    private lateinit var binding: FragmentAddPathBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddPathBinding.bind(view)
        val line = pathViewModel.currentLine.value
        with(binding.requestedLine) {
            text = line?.publicName
            line?.backgroundColor?.let { L.v(it, "color") }
            setTextColor(Color.parseColor("#${line?.textColor}"))
            setBackgroundColor(Color.parseColor("#${line?.backgroundColor}"))
        }

    }
}
