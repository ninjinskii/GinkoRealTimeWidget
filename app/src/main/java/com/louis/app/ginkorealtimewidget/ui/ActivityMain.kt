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

    companion object {
        const val KEY_SAVED_STATE = "com.louis.app.ginkorealtimewidget.KEY_SAVED_STATE"
    }

    private val pathViewModel: PathViewModel by viewModels()
    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        setViewPager()
        observe()
    }

    private fun restoreState() {

    }

    private fun setViewPager() {
        val tabTitles =
            arrayOf(resources.getString(R.string.tab1), resources.getString(R.string.tab2))
        val viewPager = binding.viewPager
        viewPager.adapter = BusPagerAdapter(this, FragmentAddLine())
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun observe() {
        pathViewModel.currentLine.observe(this, Observer { line ->
            if (line != null) {
                binding.viewPager.adapter = BusPagerAdapter(this, FragmentAddPath())
                binding.viewPager.currentItem = 1
            } else
                showSnackbar(resources.getString(R.string.CONFIG_lineError))
        })

        pathViewModel.currentPath.observe(this, Observer { path ->
            if (path != null)
                binding.viewPager.adapter = BusPagerAdapter(this, FragmentAddLine())
        })
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.coordinator, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        pathViewModel.purgeSoftDeletePaths()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(KEY_SAVED_STATE, binding.viewPager.currentItem)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        
        val tabPosition = savedInstanceState.getInt(KEY_SAVED_STATE)
        binding.viewPager.currentItem = tabPosition
    }
}
