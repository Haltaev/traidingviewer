package com.traidingviewer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.traidingviewer.data.api.model.FavoriteStock
import com.traidingviewer.data.db.dao.SymbolDao
import kotlinx.coroutines.CoroutineScope

@Database(entities = [FavoriteStock::class], version = 1, exportSchema = false)
abstract class StockDatabase : RoomDatabase() {

    abstract fun symbolDao(): SymbolDao

    companion object {
        @Volatile
        private var INSTANCE: StockDatabase? = null

        fun getDatabase(context: Context): StockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockDatabase::class.java,
                    "stock_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}