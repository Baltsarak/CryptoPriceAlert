package com.baltsarak.cryptopricealert.presentation.fragments

import android.content.Context
import android.widget.TextView
import com.baltsarak.cryptopricealert.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class InfoMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val textView: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        "Date:${convertTimestampToDate(e?.x?.toLong())}\nPrice:${e?.y}".also { textView.text = it }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(0f, -height.toFloat())
    }

    private fun convertTimestampToDate(timestamp: Long?): String {
        if (timestamp == null) return ""
        val stamp = Timestamp(timestamp * 1000)
        val date = Date(stamp.time)
        val pattern = "dMMMyyyy"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }
}