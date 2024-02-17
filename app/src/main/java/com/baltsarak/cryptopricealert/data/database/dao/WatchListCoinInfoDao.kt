package com.baltsarak.cryptopricealert.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baltsarak.cryptopricealert.data.database.entities.WatchListCoinDbModel

@Dao
interface WatchListCoinInfoDao {
    @Query("SELECT * FROM watch_list_coins")
    fun getWatchListCoins(): LiveData<List<WatchListCoinDbModel>>

    @Query("SELECT * FROM watch_list_coins WHERE fromsymbol = :fSym LIMIT 1")
    fun getInfoAboutCoin(fSym: String): LiveData<WatchListCoinDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoinToWatchList(coin: WatchListCoinDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchLisCoins(coinList: List<WatchListCoinDbModel>)

    @Query("DELETE FROM watch_list_coins WHERE fromsymbol = :fSym")
    fun deleteCoinFromWatchList(fSym: String)

}