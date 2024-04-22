package com.baltsarak.cryptopricealert.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baltsarak.cryptopricealert.data.database.entities.WatchListCoinDbModel
import com.baltsarak.cryptopricealert.domain.entities.TargetPrice

@Dao
interface WatchListCoinInfoDao {
    @Query("SELECT fromSymbol FROM watch_list_coins GROUP BY fromSymbol")
    fun getWatchListCoins(): List<String>

    @Query(
        "SELECT id, fromSymbol, targetPrice, higherThenCurrent, position " +
                "FROM watch_list_coins " +
                "ORDER BY position"
    )
    fun getTargetPrices(): List<TargetPrice>

    @Query("SELECT COUNT(*) FROM watch_list_coins WHERE fromSymbol = :fSym")
    fun countTargetPrices(fSym: String): Int

    @Query(
        "SELECT id, fromSymbol, targetPrice, higherThenCurrent, position " +
                "FROM watch_list_coins " +
                "WHERE fromSymbol = :fSym"
    )
    fun getTargetPrice(fSym: String): LiveData<List<TargetPrice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinToWatchList(coin: WatchListCoinDbModel): Long

    @Query("SELECT COUNT(DISTINCT fromSymbol) FROM watch_list_coins")
    suspend fun getWatchListSize(): Int

    @Query(
        "UPDATE watch_list_coins " +
                "SET targetPrice = null AND higherThenCurrent = null " +
                "WHERE fromSymbol = :fSym AND targetPrice = :tPrice"
    )
    suspend fun zeroingTargetPrice(fSym: String, tPrice: Double)

    @Query("DELETE FROM watch_list_coins WHERE fromSymbol = :fSym AND targetPrice = :tPrice")
    suspend fun deleteTargetPrice(fSym: String, tPrice: Double)

    @Query("DELETE FROM watch_list_coins WHERE fromsymbol = :fSym")
    suspend fun deleteCoinFromWatchList(fSym: String)

    @Query("DELETE FROM watch_list_coins")
    suspend fun deleteAllFromWatchList()
}