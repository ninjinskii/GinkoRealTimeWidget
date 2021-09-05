package com.louis.app.ginkorealtimewidget.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.ActivityAddPathBinding
import com.louis.app.ginkorealtimewidget.viewmodel.AddPathViewModel

class ActivityAddPath : AppCompatActivity() {
    private lateinit var binding: ActivityAddPathBinding
    private val addPathViewModel: AddPathViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPathBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        setupToolbar()
        observe()
    }

    private fun setupToolbar() {
        binding.toolbarLayout.toolbar.apply { setSupportActionBar(this) }
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun observe() {
        addPathViewModel.currentLine.observe(this) { line ->
            line.getContentIfNotHandled()?.let {
                replaceFragment(FragmentAddPath())
            }
        }

        addPathViewModel.currentPath.observe(this) { path ->
            path.getContentIfNotHandled()?.let {
                finish()
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
