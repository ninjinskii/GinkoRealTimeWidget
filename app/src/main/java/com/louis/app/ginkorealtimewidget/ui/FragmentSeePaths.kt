package com.louis.app.ginkorealtimewidget.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.FragmentSeePathsBinding
import com.louis.app.ginkorealtimewidget.util.L
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class FragmentSeePaths : Fragment(R.layout.fragment_see_paths) {
    private val pathViewModel: PathViewModel by activityViewModels()
    private lateinit var binding: FragmentSeePathsBinding
    private lateinit var listener: OnAddLineRequestListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSeePathsBinding.bind(view)

        with(binding) {
            buttonAdd.setOnClickListener {
                listener.onAddLineResquest()
            }
        }

        initRecyclerView()
        observeWidgetPath()
    }

    private fun initRecyclerView() {
        val pathAdapter = PathRecyclerAdapter()

        binding.pathRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = pathAdapter
        }

        pathViewModel.getUserPaths().observe(viewLifecycleOwner, Observer {
            it.forEach { path ->
                L.v("path $path")
            }
            pathAdapter.submitList(it)
        })
    }

    private fun observeWidgetPath() {
        pathViewModel.getUserWidgetPath().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                val backColor = Color.parseColor("#${it.line.backgroundColor}")
                val textColor = Color.parseColor("#${it.line.textColor}")
                binding.widgetRequestedLine.text = it.line.publicName
                binding.widgetRequestedLine.setBackgroundColor(backColor)
                binding.widgetRequestedLine.setTextColor(textColor)
                binding.path.text = it.getName()
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as OnAddLineRequestListener
    }

    interface OnAddLineRequestListener {
        fun onAddLineResquest()
    }
}