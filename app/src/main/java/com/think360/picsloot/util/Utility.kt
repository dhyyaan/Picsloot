package com.think360.picsloot.util

import android.content.Context


/**
 * Created by think360 on 20/12/17.
 */
object Utility {
    fun calculateNoOfColumns(context: Context): Int {
        val displayMetrics = context.getResources().getDisplayMetrics()
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return (dpWidth / 80).toInt()
    }
}

