package com.louis.app.ginkorealtimewidget.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.FragmentSeePathsBinding
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.util.L
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel
import kotlin.system.measureTimeMillis

class FragmentSeePaths : Fragment(R.layout.fragment_see_paths),
        PathRecyclerAdapter.OnSetPathForWidgetListener {
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
        val pathAdapter = PathRecyclerAdapter(this)

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
            val timestamp = measureTimeMillis {
                if (it != null) {
                    val backColor = Color.parseColor("#${it.line.backgroundColor}")
                    val textColor = Color.parseColor("#${it.line.textColor}")
                    val times = pathViewModel.fetchBusTime(it)

                    with(binding) {
                        currentPathLayout.visibility = View.VISIBLE
                        noCurrentPathLayout.visibility = View.GONE
                        widgetRequestedLine.text = it.line.publicName
                        widgetRequestedLine.setBackgroundColor(backColor)
                        widgetRequestedLine.setTextColor(textColor)
                        path.text = it.getName()

                        val textViews = listOf(times1, times2, times3)
                        times?.forEachIndexed { index, time ->
                            textViews[index].text = time.remainingTime
                        }
                    }


                } else {
                    with(binding) {
                        noCurrentPathLayout.visibility = View.VISIBLE
                        currentPathLayout.visibility = View.GONE
                    }
                }
            }
            L.v(timestamp.toString())
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as OnAddLineRequestListener
    }

    interface OnAddLineRequestListener {
        fun onAddLineResquest()
    }

    override fun onSetPathForWidget(path: Path) {
        pathViewModel.resetWidgetPath()
        pathViewModel.updatePath(path)
    }
}