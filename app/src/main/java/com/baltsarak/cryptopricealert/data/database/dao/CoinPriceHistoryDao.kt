package com.baltsarak.cryptopricealert.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baltsarak.cryptopricealert.data.database.entities.DayPriceDbModel

@Dao
interface CoinPriceHistoryDao {
    @Query("SELECT * FROM coin_price_history WHERE fromSymbol = :fSym ORDER BY date")
    fun getAllPriceHistoryCoin(fSym: String): LiveData<List<DayPriceDbModel>>

    @Query("SELECT * FROM coin_price_history\n" +
            "WHERE fromSymbol = :fSym " +
            "AND date >= strftime('%s', 'now', '-5 years') " +
            "ORDER BY date")
    fun getPriceHistoryForFiveYears(fSym: String): LiveData<List<DayPriceDbModel>>

    @Query("SELECT * FROM coin_price_history\n" +
            "WHERE fromSymbol = :fSym " +
            "AND date >= strftime('%s', 'now', '-1 month') " +
            "ORDER BY date")
    fun getPriceHistoryForMonth(fSym: String): LiveData<List<DayPriceDbModel>>

    @Query("SELECT * FROM coin_price_history\n" +
            "WHERE fromSymbol = :fSym " +
            "AND date >= strftime('%s', 'now', '-1 years') " +
            "ORDER BY date")
    fun getPriceHistoryForYear(fSym: String): LiveData<List<DayPriceDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinsPriceHistoryList(priceList: List<DayPriceDbModel>)
}