package com.baltsarak.cryptopricealert.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.baltsarak.cryptopricealert.databinding.FragmentWatchlistBinding
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.presentation.adapter.CoinInfoAdapter
import kotlinx.coroutines.launch

class WatchListFragment : Fragment() {

    private lateinit var viewModel: CoinViewModel
    private lateinit var adapter: CoinInfoAdapter

    private var _binding: FragmentWatchlistBinding? = null
    private val binding: FragmentWatchlistBinding
        get() = _binding ?: throw RuntimeException("FragmentWatchlistBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CoinViewModel::class.java]
        adapter = CoinInfoAdapter()
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.onCoinClickListener = object : CoinInfoAdapter.OnCoinClickListener {
            override fun onCoinClick(coinPriceInfo: CoinInfo) {
                (activity as? MainActivity)?.goToCoinDetailInfo(coinPriceInfo.fromSymbol)
            }
        }
        binding.recyclerViewWatchList.adapter = adapter
        lifecycleScope.launch {
            viewModel.watchList().observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}