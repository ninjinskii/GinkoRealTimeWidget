package com.louis.app.ginkorealtimewidget.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(ApiConstants.BASE_URL)
        .build()

interface GinkoApiService {
    @GET(ApiConstants.URL_GET_LINES)
    fun getLinesAsync(): Deferred<GinkoApiResponse>
}

object GinkoApi {
    val retrofitService: GinkoApiService by lazy {
        retrofit.create(GinkoApiService::class.java)
    }
}
