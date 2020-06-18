package com.louis.app.ginkorealtimewidget.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.ActivityMain2Binding
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class ActivityMain : FragmentActivity(), FragmentSeePaths.OnAddLineRequestListener {

    companion object {
        const val EXTRA_LINE = "com.louis.app.ginkorealtimewidget.EXTRA_LINE"
    }

    private val pathViewModel: PathViewModel by viewModels()
    lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        setViewPager()
        observe()

    }

    private fun setViewPager() {
        val tabTitles = arrayOf("Mes trajets", "Mon widget")
        val viewPager = binding.viewPager
        viewPager.adapter = BusPagerAdapter(this, FragmentSeePaths())
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun observe() {
        pathViewModel.currentLine.observe(this, Observer { line ->
            if (line != null)
                binding.viewPager.adapter = BusPagerAdapter(this, FragmentAddPath())
            else
                showSnackbar(resources.getString(R.string.CONFIG_lineError))
        })

        pathViewModel.currentPath.observe(this, Observer { path ->
            if (path != null)
                binding.viewPager.adapter = BusPagerAdapter(this, FragmentSeePaths())
        })
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.coordinator, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onAddLineResquest() {
        binding.viewPager.adapter = BusPagerAdapter(this, FragmentAddLine())
    }

    /*
    override fun onAddPathRequest() {
        // TODO: vérification ligne correcte
        binding.viewPager.adapter = BusPagerAdapter(this, FragmentAddPath())
    }*/

    //override fun onPathAdded(): vérifier validité du chemin, ajouter en bd, remettre le fragment initial
}
