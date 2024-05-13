package com.baltsarak.cryptopricealert.di

import android.app.Application
import com.baltsarak.cryptopricealert.presentation.CryptoApp
import com.baltsarak.cryptopricealert.presentation.LoginActivity
import com.baltsarak.cryptopricealert.presentation.MainActivity
import com.baltsarak.cryptopricealert.presentation.RegisterActivity
import com.baltsarak.cryptopricealert.presentation.fragments.AccountFragment
import com.baltsarak.cryptopricealert.presentation.fragments.CoinDetailInfoFragment
import com.baltsarak.cryptopricealert.presentation.fragments.NewsFragment
import com.baltsarak.cryptopricealert.presentation.fragments.PopularCoinsFragment
import com.baltsarak.cryptopricealert.presentation.fragments.SearchCoinsFragment
import com.baltsarak.cryptopricealert.presentation.fragments.WatchListFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        ViewModelModule::class,
        WorkerModule::class
    ]
)
interface ApplicationComponent {

    fun inject(activity: MainActivity)
    fun inject(activity: RegisterActivity)
    fun inject(activity: LoginActivity)

    fun inject(fragment: NewsFragment)
    fun inject(fragment: WatchListFragment)
    fun inject(fragment: PopularCoinsFragment)
    fun inject(fragment: CoinDetailInfoFragment)
    fun inject(fragment: AccountFragment)
    fun inject(fragment: SearchCoinsFragment)

    fun inject(application: CryptoApp)

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }
}