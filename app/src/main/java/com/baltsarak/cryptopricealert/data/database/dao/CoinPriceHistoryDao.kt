package com.baltsarak.cryptopricealert.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baltsarak.cryptopricealert.data.database.entities.DayPriceDbModel

@Dao
interface CoinPriceHistoryDao {
    @Query("SELECT * FROM coin_price_history WHERE fromSymbol = :fSym")
    fun getCoinsPriceHistoryList(fSym: String): LiveData<List<DayPriceDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinsPriceHistoryList(priceList: List<DayPriceDbModel>)
}