package com.traidingviewer.data.db.dao

import androidx.room.*
import com.traidingviewer.data.api.model.FavoriteStock

@Dao
interface SymbolDao {
    @Query("SELECT * FROM favorites")
    suspend fun getAllFavoriteSymbols(): List<FavoriteStock>

    @Query("SELECT * FROM favorites WHERE favorites.symbol = :symbol")
    suspend fun isFavorite(symbol: String): FavoriteStock?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(ticker: FavoriteStock)

    @Delete
    suspend fun deleteTicker(ticker: FavoriteStock)
}