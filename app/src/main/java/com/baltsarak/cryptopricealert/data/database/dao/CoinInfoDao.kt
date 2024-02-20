package com.baltsarak.cryptopricealert.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baltsarak.cryptopricealert.data.database.entities.CoinInfoDbModel

@Dao
interface CoinInfoDao {

    @Query("SELECT * FROM coins_info WHERE fromSymbol IN (:fSym)")
    fun getListCoinsInfo(fSym: List<String?>): LiveData<List<CoinInfoDbModel>>


    @Query("SELECT * FROM coins_info WHERE fromsymbol = :fSym LIMIT 1")
    fun getInfoAboutCoin(fSym: String): LiveData<CoinInfoDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListCoinsInfo(priceList: List<CoinInfoDbModel>)

    @Query("DELETE FROM coins_info WHERE fromSymbol IN (:fSym)")
    suspend fun deleteCoins(fSym: String)
}