package com.baltsarak.cryptopricealert.presentation

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.ActivityMainBinding
import com.baltsarak.cryptopricealert.domain.entities.CoinName
import com.baltsarak.cryptopricealert.presentation.contract.CustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomTitle
import com.baltsarak.cryptopricealert.presentation.contract.Navigator
import com.baltsarak.cryptopricealert.presentation.fragments.AccountFragment
import com.baltsarak.cryptopricealert.presentation.fragments.CoinDetailInfoFragment
import com.baltsarak.cryptopricealert.presentation.fragments.NewsFragment
import com.baltsarak.cryptopricealert.presentation.fragments.PopularCoinsFragment
import com.baltsarak.cryptopricealert.presentation.fragments.SearchCoinsFragment
import com.baltsarak.cryptopricealert.presentation.fragments.WatchListFragment
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), Navigator {

    private var targetCoin = "BTC"

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: CoinViewModel

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
                if (currentFragment is NewsFragment) {
                    showExitConfirmationDialog()
                } else {
                    supportFragmentManager.popBackStack()
                }
            }
        }

    private val onItemSelectedListener = OnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_news -> {
                launchFragment(NewsFragment())
                true
            }

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
        viewModel = ViewModelProvider(this)[CoinViewModel::class.java]
        if (savedInstanceState == null) {
            viewModel.loadData()
            launchFragment(NewsFragment())
        }
        lifecycleScope.launch { setClickListeners() }
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

    override fun showSearchCoin() {
        launchFragment(SearchCoinsFragment())
    }

    override fun showAccount() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_profile
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
            is NewsFragment -> R.id.navigation_news
            is PopularCoinsFragment -> R.id.navigation_popular
            is CoinDetailInfoFragment -> R.id.navigation_cryptocurrency
            is AccountFragment -> R.id.navigation_profile
            is WatchListFragment -> R.id.navigation_watch_list
            else -> binding.bottomNavigation.selectedItemId
        }
        binding.bottomNavigation.setOnItemSelectedListener(onItemSelectedListener)

        if (fragment is HasCustomTitle) {
            binding.toolbar.title = getString(fragment.getTitleRes())
        } else {
            binding.toolbar.title = getString(R.string.nothing)
        }

        if (fragment is HasCustomAction) {
            binding.toolbar.menu.clear()
            createCustomToolbarAction(fragment.getCustomAction())
        } else {
            binding.toolbar.menu.clear()
        }

        if (supportFragmentManager.backStackEntryCount > 1 && fragment !is SearchCoinsFragment) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            binding.searchView.visibility = View.GONE
        } else if (supportFragmentManager.backStackEntryCount == 1) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
            binding.searchView.visibility = View.GONE
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
            binding.searchView.visibility = View.VISIBLE
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

    override fun openSearchView() {
        val searchView = binding.searchView
        searchView.isIconified = false
        searchView.setQuery("", false)
        val searchIcon =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_button) as ImageView
        var searchDrawable = searchIcon.drawable
        searchDrawable = DrawableCompat.wrap(searchDrawable!!)
        DrawableCompat.setTint(searchDrawable, Color.WHITE)
        searchIcon.setImageDrawable(searchDrawable)

        val searchText =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as TextView
        searchText.setTextColor(Color.WHITE)
        searchText.setHintTextColor(Color.LTGRAY)

        searchView.queryHint = getString(R.string.search_hint)

        val typeface =
            ResourcesCompat
                .getFont(this, R.font.work_sans_regular)
        searchText.typeface = typeface

        val closeButton =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_close_btn) as ImageView
        var closeDrawable = closeButton.drawable
        closeDrawable = DrawableCompat.wrap(closeDrawable!!)
        DrawableCompat.setTint(closeDrawable, Color.WHITE)
        closeButton.setImageDrawable(closeDrawable)

        val fadeIn = AlphaAnimation(0.0f, 1.0f)
        fadeIn.duration = 800
        searchView.startAnimation(fadeIn)
    }

    override fun closeSearchView() {
        val fadeOut = AlphaAnimation(1.0f, 0.0f)
        fadeOut.duration = 400
        binding.searchView.startAnimation(fadeOut)
        binding.searchView.visibility = View.GONE
        binding.searchView.setQuery("", false)
    }

    private suspend fun setClickListeners() {
        val listCoinNames = viewModel.getListCoinNames()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val inputMethodManager =
                    this@MainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(listCoinNames, newText)
                return true
            }
        })

        binding.searchView.setOnCloseListener {
            closeSearchView()
            supportFragmentManager.popBackStack()
            true
        }
    }

    fun filterList(list: List<CoinName>, text: String?) {
        val filteredList = list.filter { item ->
            item.fullName.contains(text ?: "", ignoreCase = true) ||
                    item.fromSymbol.contains(text ?: "", ignoreCase = true)
        }
        viewModel.updateCoinListLiveData(filteredList)
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