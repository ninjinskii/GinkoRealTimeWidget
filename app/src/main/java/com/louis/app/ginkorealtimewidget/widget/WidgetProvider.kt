package com.louis.app.ginkorealtimewidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.Toast
import com.louis.app.ginkorealtimewidget.R
import com.louis.app.ginkorealtimewidget.database.PathDatabase
import com.louis.app.ginkorealtimewidget.model.Path
import com.louis.app.ginkorealtimewidget.model.Time
import com.louis.app.ginkorealtimewidget.model.TimeWrapper
import com.louis.app.ginkorealtimewidget.network.GinkoTimesResponse
import com.louis.app.ginkorealtimewidget.viewmodel.PathRepository
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class WidgetProvider : AppWidgetProvider() {

    companion object {
        private const val ACTION_REVERSE = "com.louis.app.ginkorealtimewidget.REVERSE"
    }

    private var widgetJob = Job()
    private val defaultScope = CoroutineScope(Dispatchers.IO + widgetJob)

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        val database = PathDatabase.getInstance(context!!)
        val repository = PathRepository(database.pathDao())

        defaultScope.launch {
            val path = repository.getWidgetPathNotLive()
            val times = fetchBusTimes(repository, path, context)
            updateWidgets(path, times, context, appWidgetManager, appWidgetIds)
            showToast(context, R.string.refreshed)
        }
    }

    private fun updateWidgets(
        path: Path, times: List<Time>?, context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        for (appWidgetId in appWidgetIds!!) {
            val views = RemoteViews(context.packageName, R.layout.ginko_widget)
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)
            val refreshTime = simpleDateFormat.format(Calendar.getInstance().time)

            updateViews(context, views, refreshTime, path, times)

            val intentUpdate = Intent(context, WidgetProvider::class.java)
            intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)

            val intentReverse = Intent(context, WidgetProvider::class.java)
            intentReverse.action = ACTION_REVERSE
            intentReverse.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)

            val pIntentUpdate = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val pIntentReverse = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                intentReverse,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            views.setOnClickPendingIntent(R.id.refresh, pIntentUpdate)
            views.setOnClickPendingIntent(R.id.reverse, pIntentReverse)
            appWidgetManager?.updateAppWidget(appWidgetId, views)

        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action != null) {
            if (intent.action == ACTION_REVERSE) {
                val database = PathDatabase.getInstance(context!!)
                val repository = PathRepository(database.pathDao())

                defaultScope.launch {
                    val currentWidgetPath = repository.getWidgetPathNotLive()
                    currentWidgetPath.isStartPointUsedForWidget =
                        currentWidgetPath.isStartPointUsedForWidget.toggleBoolean()

                    repository.updatePath(currentWidgetPath)
                    fetchBusTimes(repository, currentWidgetPath, context)
                }

                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
        }

        super.onReceive(context, intent)
    }

    private fun parseColor(color: String) = Color.parseColor(color)

    private suspend fun fetchBusTimes(
        repository: PathRepository,
        path: Path,
        context: Context
    ): List<Time>? {
        val useStartPoint = path.isStartPointUsedForWidget.translateBoolean()
        val busStop = if (useStartPoint) path.startingPoint else path.endingPoint
        val timesResponse: GinkoTimesResponse? = repository.getTimes(
            busStop,
            path.line.lineId,
            path.isStartPointNaturalWay
        )

        return if (timesResponse != null && timesResponse.isSuccessful) {
            if (timesResponse.data.isEmpty()) {
                listOf(Time(context.resources.getString(R.string.noBuses)))
            } else {
                val response: TimeWrapper? = timesResponse.data.first()
                response?.timeList
            }
        } else {
            showToast(context, R.string.appError)
            return listOf(Time(context.resources.getString(R.string.noBuses)))
        }
    }

    private fun updateViews(
        context: Context,
        views: RemoteViews,
        refreshTime: String,
        path: Path,
        times: List<Time>?
    ) {
        val destinationName = if (path.isStartPointUsedForWidget.translateBoolean()) {
            path.startingPoint
        } else {
            path.endingPoint
        }

        with(views) {
            setTextColor(R.id.lineImage, parseColor("#${path.line.textColor}"))
            setTextViewText(R.id.lineImage, path.line.publicName)
            setTextViewText(R.id.lineText, destinationName)
            setTextViewText(
                R.id.refreshTime, String.format(
                    context.getString(R.string.refreshTime),
                    refreshTime
                )
            )
            setInt(
                R.id.lineImage,
                "setBackgroundColor",
                parseColor("#${path.line.backgroundColor}")
            )
            val textViews = listOf(R.id.busTime1, R.id.busTime2, R.id.busTime3)
            times?.forEachIndexed { index, time ->
                setTextViewText(textViews[index], time.remainingTime)
            }
        }
    }

    // Helpers for numeric boolean
    private fun Int.translateBoolean() = this == 1
    private fun Int.toggleBoolean() = if (this == 1) 0 else 1

    private suspend fun showToast(context: Context, resId: Int) =
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                context.resources.getString(resId),
                Toast.LENGTH_SHORT
            ).show()
        }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        widgetJob.cancel()
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        widgetJob.cancel()
    }
}
