package com.louis.app.ginkorealtimewidget.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.ActivityAddPathBinding
import com.louis.app.ginkorealtimewidget.util.showSnackbar
import com.louis.app.ginkorealtimewidget.viewmodel.AddPathViewModel

class ActivityAddPath : AppCompatActivity() {

    private val addPathViewModel: AddPathViewModel by viewModels()
    private lateinit var binding: ActivityAddPathBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPathBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        observe()
    }

    private fun observe() {
        addPathViewModel.currentLine.observe(this, Observer { line ->
            if (!line.hasBeenHandled) replaceFragment(FragmentAddPath())
        })

        addPathViewModel.currentPath.observe(this, Observer { path ->
            if (!path.hasBeenHandled) finish()
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
