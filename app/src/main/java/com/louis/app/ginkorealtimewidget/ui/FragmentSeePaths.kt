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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.FragmentSeePathsBinding
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.model.Time
import com.louis.app.ginkorealtimewidget.util.L
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

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
        var id = -1L
        pathViewModel.getUserWidgetPath().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                lifecycleScope.launch {
                    // Prevent infinite update / notify loop
                    if (id != it.id) {
                        id = it.id
                        val times = pathViewModel.fetchBusTime(it)
                        updateUI(it, times)
                    }
                }
            } else {
                with(binding) {
                    noCurrentPathLayout.visibility = View.VISIBLE
                    currentPathLayout.visibility = View.GONE
                }
            }
        })
    }

    private suspend fun updateUI(path: Path, times: Pair<List<Time>?, List<Time>?>) {
        val backColor = Color.parseColor("#${path.line.backgroundColor}")
        val textColor = Color.parseColor("#${path.line.textColor}")

        withContext(Main) {
            with(binding) {
                currentPathLayout.visibility = View.VISIBLE
                noCurrentPathLayout.visibility = View.GONE
                widgetRequestedLine.text = path.line.publicName
                widgetRequestedLine.setBackgroundColor(backColor)
                widgetRequestedLine.setTextColor(textColor)
                currentPath.text = path.getName()

                val textViewsFirst = listOf(times1, times2, times3)
                val textViewsSecond = listOf(times4, times5, times6)
                times.first?.forEachIndexed { index, time ->
                    textViewsFirst[index].text = time.remainingTime
                }

                times.second?.forEachIndexed { index, time ->
                    textViewsSecond[index].text = time.remainingTime
                }
            }
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
        pathViewModel.resetWidgetPath()
        pathViewModel.updatePath(path)
    }
}