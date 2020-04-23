package com.louis.app.ginkorealtimewidget.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.louis.app.ginkorealtimewidget.model.Line

class BusPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentAddLine()
            else -> FragmentSeePaths()
        }
    }

    override fun getItemCount(): Int = 2
}