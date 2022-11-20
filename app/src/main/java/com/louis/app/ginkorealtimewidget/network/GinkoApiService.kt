package com.louis.app.ginkorealtimewidget.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.louis.app.ginkorealtimewidget.network.ApiConstants.Companion.ARG_API_KEY
import com.louis.app.ginkorealtimewidget.network.ApiConstants.Companion.ARG_BUS_STOP_NAME
import com.louis.app.ginkorealtimewidget.network.ApiConstants.Companion.ARG_LINES_ID
import com.louis.app.ginkorealtimewidget.network.ApiConstants.Companion.ARG_LINE_WAY
import com.louis.app.ginkorealtimewidget.network.ApiConstants.Companion.BASE_URL
import com.louis.app.ginkorealtimewidget.network.ApiConstants.Companion.URL_GET_LINES
import com.louis.app.ginkorealtimewidget.network.ApiConstants.Companion.URL_GET_TIMES
import com.louis.app.ginkorealtimewidget.network.GinkoApi.API_KEY
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
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
    @GET("$URL_GET_LINES?$ARG_API_KEY$API_KEY")
    fun getLinesAsync(): Deferred<GinkoLinesResponse>

    @GET("$URL_GET_TIMES?$ARG_API_KEY$API_KEY")
    fun getTimesAsync(
        @Query(ARG_BUS_STOP_NAME) busStop: String,
        @Query(ARG_LINES_ID) idLine: String,
        @Query(ARG_LINE_WAY) naturalWay: Boolean
    ): Deferred<GinkoTimesResponse>
}

object GinkoApi {
    const val API_KEY = "bD522KnXjK3SU5q7t7nWwUVmRm2g93"

    val retrofitService: GinkoApiService by lazy {
        retrofit.create(GinkoApiService::class.java)
    }
}
