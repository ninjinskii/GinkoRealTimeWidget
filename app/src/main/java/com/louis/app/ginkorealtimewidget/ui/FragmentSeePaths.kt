package com.louis.app.ginkorealtimewidget.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.FragmentSeePathsBinding
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.util.showSnackbar
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class FragmentSeePaths : Fragment(R.layout.fragment_see_paths),
    PathRecyclerAdapter.OnSetPathForWidgetListener {
    private lateinit var binding: FragmentSeePathsBinding
    private val pathViewModel: PathViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSeePathsBinding.bind(view)

        initRecyclerView()
        setListeners()
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

        pathViewModel.getUserPaths().observe(viewLifecycleOwner) {
            pathAdapter.submitList(it)
        }
    }

    private fun setListeners() {
        binding.buttonAdd.setOnClickListener {
            val intent = Intent(activity, ActivityAddPath::class.java)
            startActivity(intent)
        }
    }

    private fun observe() {
        pathViewModel.getUserWidgetPath().observe(viewLifecycleOwner) {
            if (it != null) {
                pathViewModel.fetchBusTimes(it)
                updateUI(it)
            } else {
                with(binding) {
                    noCurrentPathLayout.visibility = View.VISIBLE
                    currentPathLayout.visibility = View.GONE
                }
            }
        }

        pathViewModel.currentTimes.observe(viewLifecycleOwner) {
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
        }
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

    override fun onResume() {
        pathViewModel.getUserWidgetPath().value?.let {
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
        binding.root.showSnackbar(R.string.pathDeleted, R.string.cancel) {
            pathViewModel.toggleSoftDeletePath(path)
        }
    }
}
