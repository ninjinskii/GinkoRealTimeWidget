package com.louis.app.ginkorealtimewidget.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.FragmentSeePathsBinding
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class FragmentSeePaths : Fragment(R.layout.fragment_see_paths),
    PathRecyclerAdapter.OnSetPathForWidgetListener {
    private val pathViewModel: PathViewModel by activityViewModels()
    private lateinit var binding: FragmentSeePathsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSeePathsBinding.bind(view)

        initRecyclerView()
        observe()
    }

    private fun initRecyclerView() {
        val pathAdapter = PathRecyclerAdapter(this)

        binding.pathRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = pathAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    // Show button no matter what if RV can't be scrolled
                    if (
                        !recyclerView.canScrollVertically(1) &&
                        !recyclerView.canScrollVertically(-1)
                    ) binding.buttonAdd.extend()
                    else if (dy > 0 && binding.buttonAdd.isExtended) binding.buttonAdd.shrink()
                    else if (dy < 0 && !binding.buttonAdd.isExtended) binding.buttonAdd.extend()
                }
            })
        }

        pathViewModel.getUserPaths().observe(viewLifecycleOwner, Observer {
            pathAdapter.submitList(it)
        })
    }

    private fun observe() {
        pathViewModel.getUserWidgetPath().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                pathViewModel.fetchBusTimes(it)
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

                if (it != null) {
                    it.first.forEachIndexed { index, time ->
                        textViewsFirst[index].text = time.remainingTime
                    }

                    it.second.forEachIndexed { index, time ->
                        textViewsSecond[index].text = time.remainingTime
                    }

                    binding.progressBar.visibility = View.GONE
                }

            }
        })

        pathViewModel.errorChannel.observe(viewLifecycleOwner, Observer { showSnackbar(it, null) })
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
            currentBusStop1.text = path.startingPoint
            currentBusStop2.text = path.endingPoint
        }
    }

    private fun showSnackbar(stringRes: Int, actionStringRes: Int?, action: (View) -> Unit = { }) {
        Snackbar.make(binding.coordinator, stringRes, Snackbar.LENGTH_LONG).apply {
            actionStringRes?.let { setAction(it, action).duration = 8000 }
        }.show()
    }

    override fun onResume() {
        pathViewModel.currentPath.value?.let {
            pathViewModel.fetchBusTimes(it)
        }

        super.onResume()
    }

    override fun onSetPathForWidget(path: Path) {
        binding.progressBar.visibility = View.VISIBLE
        path.isCurrentPath = 1
        pathViewModel.setNewWidgetPath(path)
    }

    override fun onDeleteWidgetPath(path: Path) {
        pathViewModel.toggleSoftDeletePath(path)
        showSnackbar(R.string.pathDeleted, R.string.cancel) {
            pathViewModel.toggleSoftDeletePath(path)
        }
    }
}
