package com.louis.app.ginkorealtimewidget.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.louis.app.ginkorealtimewidget.network.ApiConstants.Companion.ARG_BUS_STOP_NAME
import com.louis.app.ginkorealtimewidget.network.ApiConstants.Companion.ARG_LINE_ID
import com.louis.app.ginkorealtimewidget.network.ApiConstants.Companion.ARG_LINE_WAY
import com.louis.app.ginkorealtimewidget.network.ApiConstants.Companion.BASE_URL
import com.louis.app.ginkorealtimewidget.network.ApiConstants.Companion.URL_GET_LINES
import com.louis.app.ginkorealtimewidget.network.ApiConstants.Companion.URL_GET_TIMES
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .build()

interface GinkoApiService {
    @GET(URL_GET_LINES)
    fun getLinesAsync(): Deferred<GinkoLinesResponse>

    @GET(URL_GET_TIMES)
    fun getTimesAsync(@Query(ARG_BUS_STOP_NAME) busStop: String,
                      @Query(ARG_LINE_ID) idLine: String,
                      @Query(ARG_LINE_WAY) naturalWay: Boolean): Deferred<GinkoTimesResponse>
}

object GinkoApi {
    val retrofitService: GinkoApiService by lazy {
        retrofit.create(GinkoApiService::class.java)
    }
}
