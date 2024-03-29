package com.baltsarak.cryptopricealert.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baltsarak.cryptopricealert.data.database.entities.DayPriceDbModel
import com.baltsarak.cryptopricealert.data.database.entities.HourPriceDbModel

@Dao
interface CoinPriceHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinsPriceHistoryList(priceList: List<DayPriceDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinsPriceHourHistoryList(priceList: List<HourPriceDbModel>)

    @Query("SELECT * FROM coin_price_history WHERE fromSymbol = :fSym")
    suspend fun getAllPriceHistoryCoin(fSym: String): List<DayPriceDbModel>

    @Query(
        "SELECT * FROM coin_price_history\n" +
                "WHERE fromSymbol = :fSym " +
                "AND date >= strftime('%s', 'now', '-5 years') "
    )
    suspend fun getPriceHistoryForFiveYears(fSym: String): List<DayPriceDbModel>

    @Query(
        "SELECT * FROM coin_price_history\n" +
                "WHERE fromSymbol = :fSym " +
                "AND date >= strftime('%s', 'now', '-1 month') "
    )
    suspend fun getPriceHistoryForMonth(fSym: String): List<DayPriceDbModel>

    @Query(
        "SELECT * FROM coin_price_history\n" +
                "WHERE fromSymbol = :fSym " +
                "AND date >= strftime('%s', 'now', '-1 years') "
    )
    suspend fun getPriceHistoryForYear(fSym: String): List<DayPriceDbModel>

    @Query("SELECT * FROM coin_price_hour_history WHERE fromSymbol = :fSym")
    suspend fun getPriceHistoryForWeek(fSym: String): List<DayPriceDbModel>

    @Query(
        "SELECT * FROM coin_price_hour_history " +
                "WHERE fromSymbol = :fSym " +
                "AND date >= strftime('%s', 'now', '-1 day') "
    )
    suspend fun getPriceHistoryForDay(fSym: String): List<DayPriceDbModel>
}