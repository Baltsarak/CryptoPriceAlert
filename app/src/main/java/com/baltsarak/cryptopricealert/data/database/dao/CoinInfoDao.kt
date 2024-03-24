package com.baltsarak.cryptopricealert.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baltsarak.cryptopricealert.data.database.entities.CoinInfoDbModel
import com.baltsarak.cryptopricealert.domain.CoinName

@Dao
interface CoinInfoDao {

    @Query("SELECT * FROM coins_info WHERE fromSymbol IN (:fSym)")
    fun getListCoinsInfo(fSym: List<String?>): LiveData<List<CoinInfoDbModel>>

    @Query("SELECT fromSymbol, fullName FROM coins_info")
    suspend fun getListCoins(): List<CoinName>

    @Query("SELECT fromSymbol FROM coins_info")
    suspend fun getListSymbols(): List<String>

    @Query("SELECT * FROM coins_info WHERE fromsymbol = :fSym LIMIT 1")
    fun getLiveDataInfoAboutCoin(fSym: String): LiveData<CoinInfoDbModel>

    @Query("SELECT * FROM coins_info WHERE fromsymbol = :fSym LIMIT 1")
    suspend fun getInfoAboutCoin(fSym: String): CoinInfoDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListCoinsInfo(priceList: List<CoinInfoDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinInfo(coinInfo: CoinInfoDbModel): Long

    @Query("UPDATE coins_info SET price = :newPrice WHERE fromSymbol = :fromSymbol")
    suspend fun updatePrice(fromSymbol: String, newPrice: Double)

    @Query("DELETE FROM coins_info WHERE fromSymbol IN (:fSym)")
    suspend fun deleteCoins(fSym: String)
}