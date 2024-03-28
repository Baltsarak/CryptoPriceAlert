package com.baltsarak.cryptopricealert.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baltsarak.cryptopricealert.data.database.entities.DayPriceDbModel

@Dao
interface CoinPriceHistoryDao {
    @Query("SELECT * FROM coin_price_history WHERE fromSymbol = :fSym ORDER BY date")
    suspend fun getAllPriceHistoryCoin(fSym: String): List<DayPriceDbModel>

    @Query(
        "SELECT * FROM coin_price_history\n" +
                "WHERE fromSymbol = :fSym " +
                "AND date >= strftime('%s', 'now', '-5 years') " +
                "ORDER BY date"
    )
    suspend fun getPriceHistoryForFiveYears(fSym: String): List<DayPriceDbModel>

    @Query(
        "SELECT * FROM coin_price_history\n" +
                "WHERE fromSymbol = :fSym " +
                "AND date >= strftime('%s', 'now', '-1 month') " +
                "ORDER BY date"
    )
    suspend fun getPriceHistoryForMonth(fSym: String): List<DayPriceDbModel>

    @Query(
        "SELECT * FROM coin_price_history\n" +
                "WHERE fromSymbol = :fSym " +
                "AND date >= strftime('%s', 'now', '-1 years') " +
                "ORDER BY date"
    )
    suspend fun getPriceHistoryForYear(fSym: String): List<DayPriceDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinsPriceHistoryList(priceList: List<DayPriceDbModel>)
}