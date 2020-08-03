package com.louis.app.ginkorealtimewidget.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.ActivityAddPathBinding
import com.louis.app.ginkorealtimewidget.util.showSnackbar
import com.louis.app.ginkorealtimewidget.viewmodel.AddPathViewModel
import com.louis.app.ginkorealtimewidget.viewmodel.PathViewModel

class ActivityAddPath : AppCompatActivity() {

    private val addPathViewModel: AddPathViewModel by viewModels()
    private lateinit var binding: ActivityAddPathBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPathBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        observe()

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, FragmentAddLine())
//                .commitNow()
//        }
    }

    private fun observe() {
        addPathViewModel.currentLine.observe(this, Observer { line ->
            if (line != null) {

            } else
                binding.root.showSnackbar(R.string.CONFIG_lineError)
        })

        addPathViewModel.currentPath.observe(this, Observer { path ->
            //if (path != null)
            // TODO: avoid livedata used to manage fragment state in addPathViewModel
        })
    }
}