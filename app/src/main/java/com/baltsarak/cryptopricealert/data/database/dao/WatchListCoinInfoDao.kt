package com.baltsarak.cryptopricealert.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baltsarak.cryptopricealert.data.database.entities.WatchListCoinDbModel

@Dao
interface WatchListCoinInfoDao {
    @Query("SELECT fromSymbol FROM watch_list_coins")
    fun getWatchListCoins(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoinToWatchList(coin: WatchListCoinDbModel)

    @Query("DELETE FROM watch_list_coins WHERE fromsymbol = :fSym")
    fun deleteCoinFromWatchList(fSym: String)
}