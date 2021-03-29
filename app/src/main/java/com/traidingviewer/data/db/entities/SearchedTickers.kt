package com.traidingviewer.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "searched_tickers")
class SearchedTickers(@PrimaryKey @ColumnInfo(name = "ticker") val name: String)