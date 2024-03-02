package com.baltsarak.cryptopricealert.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baltsarak.cryptopricealert.data.database.entities.WatchListCoinDbModel
import com.baltsarak.cryptopricealert.domain.usecases.TargetPrice

@Dao
interface WatchListCoinInfoDao {
    @Query("SELECT fromSymbol FROM watch_list_coins GROUP BY fromSymbol")
    fun getWatchListCoins(): List<String>

    @Query("SELECT fromSymbol, targetPrice FROM watch_list_coins")
    fun getTargetPrices(): List<TargetPrice>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinToWatchList(coin: WatchListCoinDbModel)

    @Query("DELETE FROM watch_list_coins WHERE fromsymbol = :fSym")
    fun deleteCoinFromWatchList(fSym: String)
}