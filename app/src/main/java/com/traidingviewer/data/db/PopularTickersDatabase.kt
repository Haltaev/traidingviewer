package com.traidingviewer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.traidingviewer.data.db.dao.PopularTickerDao
import com.traidingviewer.data.db.dao.SymbolDao
import com.traidingviewer.data.db.entities.PopularTickers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [PopularTickers::class], version = 1, exportSchema = false)
public abstract class PopularTickersDatabase : RoomDatabase() {

    abstract fun popularTickerDao(): PopularTickerDao

    companion object {
        @Volatile
        private var INSTANCE: PopularTickersDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PopularTickersDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PopularTickersDatabase::class.java,
                    "popular_ticker_database"
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { stockDatabase ->
                            scope.launch {
                                populatePopularTickers(stockDatabase.popularTickerDao())
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populatePopularTickers(dao: PopularTickerDao) {
            listOf(
                "Apple",
                "Ibm",
                "MasterCard",
                "Google",
                "Amazon",
                "Facebook",
                "Alibaba",
                "Microsoft",
                "Yandex",
                "Visa",
                "Intel",
                "Nvidia",
                "Cisco"
            )
                .map { PopularTickers(it) }
                .forEach { ticker ->
                    dao.insert(ticker)
                }
        }
    }
}