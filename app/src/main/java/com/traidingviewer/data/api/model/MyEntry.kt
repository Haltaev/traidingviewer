package com.traidingviewer.data.api.model

import com.github.mikephil.charting.data.Entry


class MyEntry (val x: Int, val chart: Point) :
    Entry(x.toFloat(), chart.price)
