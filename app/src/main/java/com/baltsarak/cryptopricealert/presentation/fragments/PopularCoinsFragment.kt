package com.baltsarak.cryptopricealert.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.FragmentPopularCoinsBinding
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.presentation.CoinViewModel
import com.baltsarak.cryptopricealert.presentation.adapters.CoinInfoAdapter
import com.baltsarak.cryptopricealert.presentation.contract.CustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomTitle
import com.baltsarak.cryptopricealert.presentation.contract.navigator
import kotlinx.coroutines.launch

class PopularCoinsFragment : Fragment(), HasCustomTitle, HasCustomAction {

    private lateinit var viewModel: CoinViewModel
    private lateinit var adapter: CoinInfoAdapter

    private var _binding: FragmentPopularCoinsBinding? = null
    private val binding: FragmentPopularCoinsBinding
        get() = _binding ?: throw RuntimeException("FragmentPopularCoinsBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[CoinViewModel::class.java]
        adapter = CoinInfoAdapter()
        _binding = FragmentPopularCoinsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewPopularCoins.visibility = View.GONE
        binding.progressBarPopulars.visibility = View.VISIBLE
        binding.recyclerViewPopularCoins.adapter = adapter
        loadData()
        setClickListener()
        binding.progressBarPopulars.visibility = View.GONE
        binding.recyclerViewPopularCoins.visibility = View.VISIBLE
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.popularCoinList().observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
        }
    }

    private fun setClickListener() {
        adapter.onCoinClickListener = object : CoinInfoAdapter.OnCoinClickListener {
            override fun onCoinClick(coinPriceInfo: CoinInfo) {
                navigator().showCoinInfo(coinPriceInfo.fromSymbol)
            }
        }
    }

    override fun getCustomAction(): CustomAction {
        return CustomAction(
            iconRes = R.drawable.search,
            textRes = R.string.search,
            onCustomAction = {
                navigator().showSearchCoin()
                navigator().openSearchView()
            }
        )
    }

    override fun getTitleRes(): Int = R.string.popular

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}