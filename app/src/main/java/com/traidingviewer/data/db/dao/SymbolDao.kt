package com.traidingviewer.data.db.dao

import androidx.room.*
import com.traidingviewer.data.api.model.FavoriteStock

@Dao
interface SymbolDao {
    @Query("SELECT * FROM favorites")
    suspend fun loadAllSymbols(): List<FavoriteStock>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(symbol: FavoriteStock)

    @Delete
    suspend fun deleteTicker(symbol: FavoriteStock)
}