package com.traidingviewer.data.api.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
class FavoriteStock(
    @PrimaryKey
    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "name")
    val name: String
)