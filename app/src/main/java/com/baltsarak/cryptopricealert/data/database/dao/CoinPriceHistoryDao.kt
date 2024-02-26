package com.baltsarak.cryptopricealert.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baltsarak.cryptopricealert.data.database.entities.DayPriceDbModel

@Dao
interface CoinPriceHistoryDao {
    @Query("SELECT * FROM coin_price_history WHERE fromSymbol = :fSym ORDER BY time")
    suspend fun getCoinsPriceHistoryList(fSym: String): List<DayPriceDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinsPriceHistoryList(priceList: List<DayPriceDbModel>)
}