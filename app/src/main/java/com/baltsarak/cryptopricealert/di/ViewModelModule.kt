package com.baltsarak.cryptopricealert.di

import androidx.lifecycle.ViewModel
import com.baltsarak.cryptopricealert.presentation.models.CoinDetailsViewModel
import com.baltsarak.cryptopricealert.presentation.models.CoinListsViewModel
import com.baltsarak.cryptopricealert.presentation.models.NewsViewModel
import com.baltsarak.cryptopricealert.presentation.models.WatchListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CoinDetailsViewModel::class)
    fun bindCoinDetailsViewModel(viewModel: CoinDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CoinListsViewModel::class)
    fun bindCoinListsViewModel(viewModel: CoinListsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewsViewModel::class)
    fun bindNewsViewModel(viewModel: NewsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WatchListViewModel::class)
    fun bindWatchListViewModel(viewModel: WatchListViewModel): ViewModel
}