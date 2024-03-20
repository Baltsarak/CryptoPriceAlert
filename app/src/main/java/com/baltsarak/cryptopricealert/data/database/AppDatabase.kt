package com.baltsarak.cryptopricealert.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.baltsarak.cryptopricealert.data.database.dao.CoinInfoDao
import com.baltsarak.cryptopricealert.data.database.dao.CoinPriceHistoryDao
import com.baltsarak.cryptopricealert.data.database.dao.WatchListCoinInfoDao
import com.baltsarak.cryptopricealert.data.database.entities.CoinInfoDbModel
import com.baltsarak.cryptopricealert.data.database.entities.DayPriceDbModel
import com.baltsarak.cryptopricealert.data.database.entities.WatchListCoinDbModel

@Database(
    entities = [
        CoinInfoDbModel::class,
        WatchListCoinDbModel::class,
        DayPriceDbModel::class
    ], version = 16, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        private var db: AppDatabase? = null
        private const val DB_NAME = "main.db"
        private val LOCK = Any()

        fun getInstance(context: Context): AppDatabase {
            synchronized(LOCK) {
                db?.let { return it }
                val instance =
                    Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        DB_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                db = instance
                return instance
            }
        }
    }

    abstract fun coinInfoDao(): CoinInfoDao
    abstract fun watchListCoinInfoDao(): WatchListCoinInfoDao
    abstract fun coinPriceHistoryDao(): CoinPriceHistoryDao
}