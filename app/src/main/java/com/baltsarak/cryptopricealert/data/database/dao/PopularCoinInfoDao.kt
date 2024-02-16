package com.baltsarak.cryptopricealert.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baltsarak.cryptopricealert.data.database.entities.PopularCoinDbModel

@Dao
interface PopularCoinInfoDao {
    @Query("SELECT * FROM popular_coins")
    fun getListPopularCoins(): LiveData<List<PopularCoinDbModel>>

    @Query("SELECT * FROM popular_coins WHERE fromsymbol = :fSym LIMIT 1")
    fun getInfoAboutPopularCoin(fSym: String): LiveData<PopularCoinDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListPopularCoins(priceList: List<PopularCoinDbModel>)
}