package com.louis.app.ginkorealtimewidget.database

import androidx.room.TypeConverter
import com.louis.app.ginkorealtimewidget.model.Variant
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


class Converters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val dataList = Types.newParameterizedType(List::class.java, Variant::class.java)
    private val jsonAdapter: JsonAdapter<List<Variant>> = moshi.adapter(dataList)

    @TypeConverter
    fun listToJson(value: List<Variant>?): String = jsonAdapter.toJson(value)

    @TypeConverter
    fun jsonToList(value: String): List<Variant>? = jsonAdapter.fromJson(value)
}