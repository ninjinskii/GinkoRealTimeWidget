package com.louis.app.ginkorealtimewidget.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.ActivityMain2Binding
import com.louis.app.ginkorealtimewidget.model.Line
import com.louis.app.ginkorealtimewidget.util.L
import com.louis.app.ginkorealtimewidget.util.NoSuchLineException
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

const val EXTRA_LINE = "com.louis.app.ginkorealtimewidget.EXTRA_LINE"

class ActivityMain : AppCompatActivity(), FragmentAddLine.OnLineAddedListener {

    /*private val viewModelFactory = PathViewModelFactory()
    private val viewModel: PathViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(PathViewModel::class.java)
    }*/
    private lateinit var pathViewModel: PathViewModel
    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        pathViewModel = ViewModelProvider(this).get(PathViewModel::class.java)

        setViewPager()
        observe()
    }

    private fun setViewPager() {
        val tabTitles = arrayOf("Mes trajets", "Ajouter un trajet")
        val viewPager = binding.viewPager
        viewPager.adapter = BusPagerAdapter(this)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(binding.tabLayout, binding.viewPager) {
            tab, position -> tab.text = tabTitles[position]
        }.attach()
    }

    private fun observe() {
        pathViewModel.currentLine.observe(this, Observer { line ->
            if (line != null) {
                val intent = Intent(this, ActivityConfig::class.java)
                intent.putExtra(EXTRA_LINE, line.publicName)

                //startActivity(intent)
                /*val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val fragment = FragmentAddPath()
                fragmentTransaction.replace(R.id., fragment)
                fragmentTransaction.commit()*/
            } else
                showError(resources.getString(R.string.CONFIG_lineError))
        })

        pathViewModel.isFetchingData.observe(this, Observer {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        })
    }

    private fun showError(message: String) {
        Snackbar.make(binding.coordinator, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.managePath) {
            //val intent = Intent(this, )
        }

        return true
    }

    override fun onLineAdded(lineName: String) {
        pathViewModel.fetchLine(lineName)
    }
}
