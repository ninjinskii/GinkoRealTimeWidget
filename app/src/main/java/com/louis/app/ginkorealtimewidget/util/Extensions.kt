package com.louis.app.ginkorealtimewidget.util

import android.view.View
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

fun CoordinatorLayout.showSnackbar(
    @StringRes stringRes: Int,
    @StringRes actionStringRes: Int? = null,
    action: (View) -> Unit = { }
) {
    Snackbar.make(this, stringRes, Snackbar.LENGTH_LONG).apply {
        actionStringRes?.let { setAction(it, action).duration = 8000 }
    }.show()
}
