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
import com.louis.app.ginkorealtimewidget.model.Time
import com.louis.app.ginkorealtimewidget.model.TimeWrapper
import com.louis.app.ginkorealtimewidget.network.GinkoTimesResponse
import com.louis.app.ginkorealtimewidget.util.FetchTimeException
import com.louis.app.ginkorealtimewidget.util.L
import com.louis.app.ginkorealtimewidget.viewmodel.PathRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class WidgetProvider : AppWidgetProvider() {
    private val ACTION_REVERSE = "com.louis.app.ginkorealtimewidget.REVERSE"

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        Toast.makeText(context, "OnUpdate", Toast.LENGTH_LONG).show()
        L.v("Onupdate", "widget")
        val database = PathDatabase.getInstance(context!!)
        val repository = PathRepository(database.pathDao())
        val path = runBlocking { database.pathDao().getWidgetPathNotLive() }
        val times = runBlocking {
            val timesResponse: GinkoTimesResponse? = repository.getTimes(
                    path.startingPoint.startName,
                    path.line.lineId,
                    path.isStartPointNaturalWay
            )

            if (timesResponse != null && timesResponse.isSuccessful) {
                if (timesResponse.data.isEmpty()) {
                    listOf(Time("9999"), Time("9999"), Time("9999"))
                } else {
                    val response: TimeWrapper? = timesResponse.data.first()
                    val verifiedBusStopName = response?.verifiedBusStopName
                    response!!.timeList
                }
            } else {
                L.e(FetchTimeException("An error occured while fetching times"))
                listOf(Time("0"), Time("0"), Time("0"))
            }
        }

        for (appWidgetId in appWidgetIds!!) {
            val views = RemoteViews(context.packageName, R.layout.ginko_widget)
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)
            val refreshTime = simpleDateFormat.format(Calendar.getInstance().time)

            with(views) {
                setTextColor(R.id.imgLigne, parseColor("#${path.line.textColor}"))
                setTextViewText(R.id.tempsBus1, times[0].remainingTime)
                setTextViewText(R.id.tempsBus2, times[1].remainingTime)
//                setTextViewText(R.id.tempsBus3, times[2].remainingTime)
                setTextViewText(R.id.tempsRefresh, refreshTime)
                setInt(
                        R.id.imgLigne,
                        "setBackgroundColor",
                        parseColor("#${path.line.backgroundColor}")
                )
            }

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

            views.setOnClickPendingIntent(R.id.boutonRefresh, pIntentUpdate)
            views.setOnClickPendingIntent(R.id.boutonReverse, pIntentReverse)
            appWidgetManager!!.updateAppWidget(appWidgetId, views)

        }
        Toast.makeText(context, R.string.refreshed, Toast.LENGTH_SHORT).show()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Toast.makeText(context, "Action received", Toast.LENGTH_LONG).show()
        if (intent!!.action != null) {
            if (intent.action == ACTION_REVERSE) {
                //Changer ligne
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
        }
    }

    private fun parseColor(color: String): Int {
        L.v(color, "color")
        return Color.parseColor(color)
    }

}