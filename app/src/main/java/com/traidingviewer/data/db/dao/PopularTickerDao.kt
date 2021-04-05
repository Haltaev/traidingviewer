package com.traidingviewer.data.db.dao

import androidx.room.*
import com.traidingviewer.data.db.entities.PopularTickers

@Dao
interface PopularTickerDao {
    @Query("SELECT * FROM popular_tickers")
    suspend fun loadAllPopularTickers(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(name: PopularTickers)
}