package com.traidingviewer.ui.info.chart

import android.annotation.SuppressLint
import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.traidingviewer.R
import com.traidingviewer.data.api.model.MyEntry
import kotlinx.android.synthetic.main.view_marker.view.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ViewConstructor")
class MyMarkerView(context: Context) :
    MarkerView(context, R.layout.view_marker) {
    override fun refreshContent(
        e: Entry,
        highlight: Highlight
    ) {
        marker_date.text = (e as? MyEntry)?.chart?.date?.toDate()
        marker_price.text = (e as? MyEntry)?.chart?.price.toString()

        super.refreshContent(e, highlight)
    }

    private fun String?.toDate(): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("EN"))
        val inputDateFormatWithoutTime = SimpleDateFormat("yyyy-MM-dd", Locale("EN"))
        val outputDateFormat = SimpleDateFormat("d MMM yy HH:mm", Locale("EN"))
        val outputDateFormatWithoutTime = SimpleDateFormat("d MMM yy HH:mm", Locale("EN"))

        return try {
            val date = inputDateFormat.parse(this ?: "")
            if (date != null) outputDateFormat.format(date) else ""
        } catch (e: Exception) {
            val date = inputDateFormatWithoutTime.parse(this ?: "")
            if (date != null) outputDateFormatWithoutTime.format(date) else ""
        }
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}