package com.baltsarak.cryptopricealert.presentation.fragments

import android.content.Context
import android.widget.TextView
import com.baltsarak.cryptopricealert.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InfoMarkerView(context: Context, layoutResource: Int, private val chart: LineChart) :
    MarkerView(context, layoutResource) {

    private val textView: TextView = findViewById(R.id.tvContent)
    private var adjustToLeft = false
    private val dateFormat = SimpleDateFormat("dMMMyyyy", Locale.getDefault())

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        "Date:${convertTimestampToDate(e?.x?.toLong())}\nPrice:${e?.y}".also {
            textView.text = it
        }
        super.refreshContent(e, highlight)
        adjustToLeft = highlight?.let {
            chart.width - it.drawX < width
        } ?: false
    }

    override fun getOffset(): MPPointF {
        return if (adjustToLeft) {
            MPPointF(-width.toFloat(), -height.toFloat() * 2)
        } else {
            MPPointF(0f, -height.toFloat() * 2)
        }
    }

    private fun convertTimestampToDate(timestamp: Long?): String {
        if (timestamp == null) return ""
        return dateFormat.format(Date(timestamp * 1000))
    }
}