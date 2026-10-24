package ru.xrtz.whichstate.feature.results

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

internal tailrec fun Context.findActivity(): Activity = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> error("no Activity in the Context chain")
}
