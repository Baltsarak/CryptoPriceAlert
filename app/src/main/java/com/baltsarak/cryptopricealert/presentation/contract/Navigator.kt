package com.baltsarak.cryptopricealert.presentation.contract

import androidx.fragment.app.Fragment

fun Fragment.navigator(): Navigator {
    return requireActivity() as Navigator
}

interface Navigator {

    fun showWatchList()

    fun showPopularCoinsList()

    fun showCoinInfo(fromSymbol: String)

    fun showSearchCoin()

    fun openSearchView()

    fun closeSearchView()

    fun showAccount()

    fun showCoinSearch()
}