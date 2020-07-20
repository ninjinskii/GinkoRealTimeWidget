package com.louis.app.ginkorealtimewidget.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class BusPagerAdapter(fragmentActivity: FragmentActivity, private val fragmentToShow: Fragment) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentSeePaths()
            else -> fragmentToShow
        }
    }

    override fun getItemCount(): Int = 2
}