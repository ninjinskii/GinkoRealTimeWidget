package com.louis.app.ginkorealtimewidget.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.louis.app.ginkorealtimewidget.R

class BusPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> FragmentAddLine()
            1 -> FragmentAddPath()
            else -> Fragment()
        }
    }

    override fun getItemCount(): Int = 2
}