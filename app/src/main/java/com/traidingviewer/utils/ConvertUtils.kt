package com.traidingviewer.utils

import android.content.Context
import android.util.DisplayMetrics

object ConvertUtils {
    fun dpToPx(dp: Int, context: Context): Int {
        return dp * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }
}
