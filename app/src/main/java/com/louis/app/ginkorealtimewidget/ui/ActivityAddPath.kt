package com.louis.app.ginkorealtimewidget.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.databinding.ActivityAddPathBinding

class ActivityAddPath : AppCompatActivity() {

    private lateinit var binding: ActivityAddPathBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPathBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FragmentAddLine())
                .commitNow()
        }
    }
}