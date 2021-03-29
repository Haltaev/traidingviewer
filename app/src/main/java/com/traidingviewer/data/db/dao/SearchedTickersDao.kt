package com.traidingviewer.data.db.dao

import androidx.room.*
import com.traidingviewer.data.db.entities.SearchedTickers

@Dao
interface SearchedTickersDao {
    @Query("SELECT * FROM searched_tickers")
    suspend fun loadAllSearchedTickers(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(name: SearchedTickers)

    @Delete
    suspend fun deleteTicker(name: SearchedTickers)
}