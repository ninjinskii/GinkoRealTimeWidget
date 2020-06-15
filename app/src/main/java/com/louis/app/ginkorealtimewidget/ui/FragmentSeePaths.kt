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
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.util.L
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class FragmentSeePaths : Fragment(R.layout.fragment_see_paths),
        PathRecyclerAdapter.OnSetPathForWidgetListener {
    private val pathViewModel: PathViewModel by activityViewModels()
    private lateinit var binding: FragmentSeePathsBinding
    private lateinit var listener: OnAddLineRequestListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        L.timestamp("FragSeePaths onViewCreated") {
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
    }

    private fun initRecyclerView() {
        val pathAdapter = PathRecyclerAdapter(this)

        binding.pathRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = pathAdapter
        }

        pathViewModel.getUserPaths().observe(viewLifecycleOwner, Observer {
            pathAdapter.submitList(it)
        })
    }

    private fun observeWidgetPath() {
        pathViewModel.getUserWidgetPath().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                pathViewModel.fetchBusTime(it)
                updateUI(it)
            } else {
                with(binding) {
                    noCurrentPathLayout.visibility = View.VISIBLE
                    currentPathLayout.visibility = View.GONE
                }
            }
        })

        pathViewModel.currentTimes.observe(viewLifecycleOwner, Observer {
            with(binding) {
                val textViewsFirst = listOf(times1, times2, times3)
                val textViewsSecond = listOf(times4, times5, times6)

                it.first?.forEachIndexed { index, time ->
                    textViewsFirst[index].text = time.remainingTime
                }

                it.second?.forEachIndexed { index, time ->
                    textViewsSecond[index].text = time.remainingTime
                }

                binding.timesLayout.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun updateUI(path: Path) {
        val backColor = Color.parseColor("#${path.line.backgroundColor}")
        val textColor = Color.parseColor("#${path.line.textColor}")
        with(binding) {
            currentPathLayout.visibility = View.VISIBLE
            noCurrentPathLayout.visibility = View.GONE
            widgetRequestedLine.text = path.line.publicName
            widgetRequestedLine.setBackgroundColor(backColor)
            widgetRequestedLine.setTextColor(textColor)
            currentBusStop1.text = path.startingPoint.startName
            currentBusStop2.text = path.endingPoint.endName
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as OnAddLineRequestListener
    }

    interface OnAddLineRequestListener {
        fun onAddLineResquest()
    }

    override fun onSetPathForWidget(path: Path) {
        binding.timesLayout.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
        pathViewModel.resetWidgetPath()
        pathViewModel.updatePath(path)
    }
}