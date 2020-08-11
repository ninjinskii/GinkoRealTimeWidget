package com.louis.app.ginkorealtimewidget.util

open class Event<out T>(private val content: T) {
    var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}
