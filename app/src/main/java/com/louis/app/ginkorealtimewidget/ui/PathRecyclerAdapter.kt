package com.louis.app.ginkorealtimewidget.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.util.L

class PathRecyclerAdapter :
        ListAdapter<Path, PathRecyclerAdapter.PathViewHolder>(PathItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PathViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_path, parent, false)
    )

    override fun onBindViewHolder(holder: PathViewHolder, position: Int) =
            holder.bind(getItem(position))

    class PathItemDiffCallback : DiffUtil.ItemCallback<Path>() {
        override fun areItemsTheSame(oldItem: Path, newItem: Path) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Path, newItem: Path) = oldItem == newItem
    }

    inner class PathViewHolder
    constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val requestedLine: TextView = itemView.findViewById(R.id.requestedLine)
        private val pathEndPoints: TextView = itemView.findViewById(R.id.path)
        private val updateWidgetPath: Button = itemView.findViewById(R.id.updateCurrentPath)

        fun bind(path: Path) {
            with(requestedLine) {
                text = path.line.publicName
                setTextColor(android.graphics.Color.parseColor("#${path.line.textColor}"))
                setBackgroundColor(
                        android.graphics.Color.parseColor("#${path.line.backgroundColor}")
                )
            }

            pathEndPoints.text = path.getName()

            updateWidgetPath.setOnClickListener {
                L.v("Clicked on recycler view")
            }
        }
    }
}