package com.baltsarak.cryptopricealert.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.baltsarak.cryptopricealert.data.database.dao.PopularCoinInfoDao
import com.baltsarak.cryptopricealert.data.database.dao.WatchListCoinInfoDao
import com.baltsarak.cryptopricealert.data.database.entities.PopularCoinDbModel
import com.baltsarak.cryptopricealert.data.database.entities.WatchListCoinDbModel

@Database(entities = [
    PopularCoinDbModel::class,
    WatchListCoinDbModel::class
                     ], version = 2, exportSchema = false)
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

    abstract fun popularCoinInfoDao(): PopularCoinInfoDao
    abstract fun watchListCoinInfoDao(): WatchListCoinInfoDao
}