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

class PathRecyclerAdapter(private val onSetPathForWidgetListener: OnSetPathForWidgetListener) :
    ListAdapter<Path, PathRecyclerAdapter.PathViewHolder>(PathItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PathViewHolder(
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_path, parent, false),
        onSetPathForWidgetListener
    )

    override fun onBindViewHolder(holder: PathViewHolder, position: Int) =
        holder.bind(getItem(position))

    class PathItemDiffCallback : DiffUtil.ItemCallback<Path>() {
        override fun areItemsTheSame(oldItem: Path, newItem: Path) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Path, newItem: Path) = oldItem == newItem
    }

    inner class PathViewHolder(itemView: View, private val listener: OnSetPathForWidgetListener) :
        RecyclerView.ViewHolder(itemView) {
        private val requestedLine: TextView = itemView.findViewById(R.id.requestedLine)
        private val pathEndPoints: TextView = itemView.findViewById(R.id.path)
        private val updateWidgetPath: Button = itemView.findViewById(R.id.updateCurrentPath)
        private val towards: TextView = itemView.findViewById(R.id.towards)

        fun bind(path: Path) {
            with(requestedLine) {
                text = path.line.publicName
                setTextColor(android.graphics.Color.parseColor("#${path.line.textColor}"))
                setBackgroundColor(
                    android.graphics.Color.parseColor("#${path.line.backgroundColor}")
                )
            }

            val startPointFinalDestination = path.line.variants.find {
                it.naturalWay == path.isStartPointNaturalWay
            }?.endPointName

            towards.text = String.format(
                itemView.context.getString(R.string.towardSimple),
                startPointFinalDestination
            )

            with(pathEndPoints) {
                text = path.getName()
                isSelected = true
            }

            updateWidgetPath.setOnClickListener {
                listener.onSetPathForWidget(path)
            }

            itemView.setOnLongClickListener {
                listener.onDeleteWidgetPath(path)
                true
            }
        }
    }

    interface OnSetPathForWidgetListener {
        fun onSetPathForWidget(path: Path)
        fun onDeleteWidgetPath(path: Path)
    }
}