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
import com.louis.app.ginkorealtimewidget.util.FetchTimeException
import com.louis.app.ginkorealtimewidget.util.L
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
        L.thread("onUpdate")
        val database = PathDatabase.getInstance(context!!)
        val repository = PathRepository(database.pathDao())

        defaultScope.launch {
            val path = repository.getWidgetPathNotLive()
            val times = fetchBusTimes(repository, path)
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
            L.v("In for loop appWidgetIds", "loop")
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
        L.thread("onReceive")
        if (intent!!.action != null) {
            if (intent.action == ACTION_REVERSE) {
                val database = PathDatabase.getInstance(context!!)
                val repository = PathRepository(database.pathDao())
                val currentWidgetPath = runBlocking { repository.getWidgetPathNotLive() }

                val startPoint = translateBoolean(currentWidgetPath.isStartPointUsedForWidget)
                if (startPoint)
                    currentWidgetPath.isStartPointUsedForWidget = 0
                else
                    currentWidgetPath.isStartPointUsedForWidget = 1

                GlobalScope.launch { repository.updatePath(currentWidgetPath) }

                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
        }

        super.onReceive(context, intent)
    }

    private fun parseColor(color: String) = Color.parseColor(color)

    private fun fetchBusTimes(repository: PathRepository, path: Path) = runBlocking {
        L.thread("fetchBusTimeWidget")
        val useStartPoint = translateBoolean(path.isStartPointUsedForWidget)
        val busStop = if (useStartPoint) path.startingPoint else path.endingPoint

        val timesResponse: GinkoTimesResponse? = repository.getTimes(
            busStop,
            path.line.lineId,
            path.isStartPointNaturalWay
        )

        if (timesResponse != null && timesResponse.isSuccessful) {
            if (timesResponse.data.isEmpty()) {
                listOf(
                    Time((1..10).shuffled().first().toString()),
                    Time((11..20).shuffled().first().toString()),
                    Time((21..35).shuffled().first().toString())
                )
            } else {
                val response: TimeWrapper? = timesResponse.data.first()
                response?.timeList
            }
        } else {
            L.e(FetchTimeException("An error occured while fetching times"))
            listOf(Time("error"), Time("error"), Time("error"))
        }
    }

    private fun updateViews(
        context: Context,
        views: RemoteViews,
        refreshTime: String,
        path: Path,
        times: List<Time>?
    ) {
        val destinationName = if (translateBoolean(path.isStartPointUsedForWidget))
            path.startingPoint
        else path.endingPoint

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

    private fun translateBoolean(boolean: Int) = boolean == 1

    private suspend fun showToast(context: Context, resId: Int) =
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                context.resources.getString(resId),
                Toast.LENGTH_SHORT
            ).show()
        }
}