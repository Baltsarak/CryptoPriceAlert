package com.baltsarak.cryptopricealert.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baltsarak.cryptopricealert.data.database.entities.CoinNameDbModel

@Dao
interface CoinNamesDao {

    @Query("SELECT * FROM coin_names ORDER BY fullName")
    fun getListCoinNames(): LiveData<List<CoinNameDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListCoinsInfo(priceList: List<CoinNameDbModel>)
}