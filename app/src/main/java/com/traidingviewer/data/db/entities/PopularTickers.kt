package com.traidingviewer.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "popular_tickers")
class PopularTickers(@PrimaryKey @ColumnInfo(name = "ticker") val name: String)