package com.louis.app.ginkorealtimewidget.ui

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.ActivityMain2Binding
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class ActivityMain : FragmentActivity() {

    private val pathViewModel: PathViewModel by viewModels()
    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        observe()
    }

    private fun restoreState() {

    }

    private fun observe() {
        pathViewModel.currentLine.observe(this, Observer { line ->
            if (line != null) {

            } else
                showSnackbar(resources.getString(R.string.CONFIG_lineError))
        })

        pathViewModel.currentPath.observe(this, Observer { path ->
            //if (path != null)

        })
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.coordinator, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        pathViewModel.purgeSoftDeletePaths()
        super.onDestroy()
    }
}
