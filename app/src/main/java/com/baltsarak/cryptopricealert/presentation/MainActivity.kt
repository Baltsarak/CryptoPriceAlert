package com.baltsarak.cryptopricealert.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.ActivityMainBinding
import com.baltsarak.cryptopricealert.presentation.contract.CustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomTitle
import com.baltsarak.cryptopricealert.presentation.contract.Navigator
import com.baltsarak.cryptopricealert.presentation.fragments.AccountFragment
import com.baltsarak.cryptopricealert.presentation.fragments.CoinDetailInfoFragment
import com.baltsarak.cryptopricealert.presentation.fragments.PopularCoinsFragment
import com.baltsarak.cryptopricealert.presentation.fragments.SearchCoinsFragment
import com.baltsarak.cryptopricealert.presentation.fragments.WatchListFragment
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener

class MainActivity : AppCompatActivity(), Navigator {

    private var targetCoin = "BTC"

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val fragmentListener =
        object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(
                fm: FragmentManager,
                f: Fragment,
                v: View,
                savedInstanceState: Bundle?
            ) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                updateUi(f)
            }
        }

    private var callbackOnBackPressed =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.main_screen_fragment_container)
                if (currentFragment is WatchListFragment) {
                    showExitConfirmationDialog()
                } else {
                    supportFragmentManager.popBackStack()
                }
            }
        }

    private val onItemSelectedListener = OnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_watch_list -> {
                launchFragment(WatchListFragment())
                true
            }

            R.id.navigation_popular -> {
                launchFragment(PopularCoinsFragment())
                true
            }

            R.id.navigation_cryptocurrency -> {
                launchFragment(CoinDetailInfoFragment.newInstance(targetCoin))
                true
            }

            R.id.navigation_profile -> {
                launchFragment(AccountFragment())
                true
            }

            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.bottomNavigation.setOnItemSelectedListener(onItemSelectedListener)
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, false)
        onBackPressedDispatcher.addCallback(this, callbackOnBackPressed)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun showWatchList() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_watch_list
    }

    override fun showPopularCoinsList() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_popular
    }

    override fun showCoinInfo(fromSymbol: String) {
        targetCoin = fromSymbol
        binding.bottomNavigation.selectedItemId = R.id.navigation_cryptocurrency
    }

    override fun showAccount() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_profile
    }

    override fun showCoinSearch() {
        launchFragment(SearchCoinsFragment())
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.main_screen_fragment_container, fragment)
            .commit()
    }

    private fun updateUi(fragment: Fragment) {
        binding.bottomNavigation.setOnItemSelectedListener(null)
        binding.bottomNavigation.selectedItemId = when (fragment) {
            is PopularCoinsFragment -> R.id.navigation_popular
            is CoinDetailInfoFragment -> R.id.navigation_cryptocurrency
            is AccountFragment -> R.id.navigation_profile
            else -> R.id.navigation_watch_list
        }
        binding.bottomNavigation.setOnItemSelectedListener(onItemSelectedListener)

        if (fragment is HasCustomTitle) {
            binding.toolbar.title = getString(fragment.getTitleRes())
        } else {
            binding.toolbar.title = getString(R.string.app_name)
        }

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        }

        if (fragment is HasCustomAction) {
            binding.toolbar.menu.clear()
            createCustomToolbarAction(fragment.getCustomAction())
        } else {
            binding.toolbar.menu.clear()
        }
    }

    private fun createCustomToolbarAction(action: CustomAction) {
        val iconDrawable =
            DrawableCompat.wrap(ContextCompat.getDrawable(this, action.iconRes)!!)
        iconDrawable.setTint(Color.WHITE)

        val menuItem = binding.toolbar.menu.add(action.textRes)
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menuItem.icon = iconDrawable
        menuItem.setOnMenuItemClickListener {
            action.onCustomAction.run()
            return@setOnMenuItemClickListener true
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Выход")
            .setMessage("Вы уверены, что хотите выйти?")
            .setPositiveButton("Да") { _, _ ->
                finish()
            }
            .setNegativeButton("Нет", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        callbackOnBackPressed.isEnabled = false
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
    }
}