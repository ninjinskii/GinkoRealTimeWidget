package com.louis.app.ginkorealtimewidget.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.FragmentConfigWidgetBinding
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class FragmentConfigWidget : Fragment(R.layout.fragment_config_widget) {
    private val pathViewModel: PathViewModel by activityViewModels()
    private lateinit var binding: FragmentConfigWidgetBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentConfigWidgetBinding.bind(view)

        pathViewModel.getAllPaths().observe(viewLifecycleOwner, Observer {
            val result = StringBuilder()

            if (it != null) {
                for (path in it) {
                    result.append("${path.startingPoint}, ${path.endingPoint}, ${path.isCurrentPath}\n")
                }

                binding.getAllPaths.text = result.toString()
            }
        })
    }
}