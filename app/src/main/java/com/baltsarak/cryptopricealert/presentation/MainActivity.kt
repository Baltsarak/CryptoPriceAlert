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
                R.id.navigation_watch_list  -> {
                    if (savedInstanceState == null) {
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.main_screen_fragment_container, WatchListFragment())
                            .commit()
                    }
                    true
                }
                R.id.navigation_popular -> {
                    if (savedInstanceState == null) {
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.main_screen_fragment_container, PopularCoinsFragment())
                            .commit()
                    }
                    true
                }
                R.id.navigation_cryptocurrency -> {
                    if (savedInstanceState == null) {
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.main_screen_fragment_container, CoinDetailInfoFragment.newInstance(targetCoin))
                            .addToBackStack(null)
                            .commit()
                    }
                    true
                }
                R.id.navigation_profile  -> {
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
}