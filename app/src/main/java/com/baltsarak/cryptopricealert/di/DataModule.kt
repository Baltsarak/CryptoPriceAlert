package com.baltsarak.cryptopricealert.di

import android.app.Application
import com.baltsarak.cryptopricealert.data.database.AppDatabase
import com.baltsarak.cryptopricealert.data.database.dao.CoinInfoDao
import com.baltsarak.cryptopricealert.data.database.dao.CoinPriceHistoryDao
import com.baltsarak.cryptopricealert.data.database.dao.WatchListCoinInfoDao
import com.baltsarak.cryptopricealert.data.network.ApiFactory
import com.baltsarak.cryptopricealert.data.network.ApiService
import com.baltsarak.cryptopricealert.data.repository.CoinRepositoryImpl
import com.baltsarak.cryptopricealert.domain.CoinRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @Binds
    @ApplicationScope
    fun bindCoinRepository(impl: CoinRepositoryImpl): CoinRepository

    companion object {

        @Provides
        @ApplicationScope
        fun provideCoinInfoDao(
            application: Application
        ): CoinInfoDao {
            return AppDatabase.getInstance(application).coinInfoDao()
        }

        @Provides
        @ApplicationScope
        fun provideWatchListCoinInfoDao(
            application: Application
        ): WatchListCoinInfoDao {
            return AppDatabase.getInstance(application).watchListCoinInfoDao()
        }

        @Provides
        @ApplicationScope
        fun provideCoinPriceHistoryDao(
            application: Application
        ): CoinPriceHistoryDao {
            return AppDatabase.getInstance(application).coinPriceHistoryDao()
        }

        @Provides
        @ApplicationScope
        fun provideApiService(): ApiService {
            return ApiFactory.apiService
        }
    }
}