package com.baltsarak.cryptopricealert.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var targetCoin = "BTC"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_watch_list -> {
                    supportFragmentManager.popBackStack()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.main_screen_fragment_container,
                            WatchListFragment(),
                            "WatchList"
                        )
                        .commit()
                    true
                }

                R.id.navigation_popular -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.main_screen_fragment_container,
                            PopularCoinsFragment(),
                            "PopularCoins"
                        )
                        .commit()
                    true
                }

                R.id.navigation_cryptocurrency -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.main_screen_fragment_container,
                            CoinDetailInfoFragment.newInstance(targetCoin),
                            "DetailInfo"
                        )
                        .addToBackStack(null)
                        .commit()
                    true
                }

                R.id.navigation_profile -> {
                    TODO()
                }

                else -> false
            }
        }
    }

    fun goToCoinDetailInfo(fromSymbol: String) {
        targetCoin = fromSymbol
        binding.bottomNavigation.selectedItemId = R.id.navigation_cryptocurrency
    }

    fun returnByBackStack() {
        val previousFragment = supportFragmentManager.findFragmentByTag(
            "PopularCoins"
        )
        if (previousFragment != null) {
            binding.bottomNavigation.selectedItemId = R.id.navigation_popular
        } else {
            binding.bottomNavigation.selectedItemId = R.id.navigation_watch_list
        }
    }
}