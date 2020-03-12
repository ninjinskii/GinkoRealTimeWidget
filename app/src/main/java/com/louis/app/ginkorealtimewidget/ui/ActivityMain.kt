package com.louis.app.ginkorealtimewidget.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.ActivityMain2Binding
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

const val EXTRA_LINE = "com.louis.app.ginkorealtimewidget.EXTRA_LINE"

class ActivityMain : AppCompatActivity() {

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

        setListeners()
        observe()
    }

    private fun observe() {
        pathViewModel.currentLine.observe(this, Observer { line ->
            if(line != null) {
                val intent = Intent(this, ActivityConfig::class.java)
                intent.putExtra(EXTRA_LINE, line.publicName)

                startActivity(intent)
            }
            else
                showError()
        })

        pathViewModel.isFetchingData.observe(this, Observer {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        })
    }

    private fun setListeners() {
        binding.buttonNext.setOnClickListener {
            val lineName = binding.inputLine.text.toString()
            pathViewModel.fetchLine(lineName)
        }
    }

    private fun showError() {
        val message = "Aucune ligne trouvée pour la recherche"
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
}
