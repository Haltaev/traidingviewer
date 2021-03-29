package com.traidingviewer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.traidingviewer.data.db.dao.SearchedTickersDao
import com.traidingviewer.data.db.entities.SearchedTickers

@Database(entities = [SearchedTickers::class], version = 1, exportSchema = false)
abstract class SearchedTickersDatabase : RoomDatabase() {

    abstract fun searchedTickersDao(): SearchedTickersDao

    companion object {
        @Volatile
        private var INSTANCE: SearchedTickersDatabase? = null

        fun getDatabase(context: Context): SearchedTickersDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SearchedTickersDatabase::class.java,
                    "searched_tickers_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}