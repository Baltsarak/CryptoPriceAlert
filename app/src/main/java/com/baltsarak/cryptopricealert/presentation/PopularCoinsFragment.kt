package com.baltsarak.cryptopricealert.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.baltsarak.cryptopricealert.databinding.FragmentPopularCoinsBinding
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.presentation.adapter.CoinInfoAdapter
import kotlinx.coroutines.launch

class PopularCoinsFragment : Fragment() {

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
        viewModel = ViewModelProvider(this)[CoinViewModel::class.java]
        adapter = CoinInfoAdapter()
        _binding = FragmentPopularCoinsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.onCoinClickListener = object : CoinInfoAdapter.OnCoinClickListener {
            override fun onCoinClick(coinPriceInfo: CoinInfo) {
                (activity as? MainActivity)?.goToCoinDetailInfo(coinPriceInfo.fromSymbol)
            }
        }
        binding.recyclerViewPopularCoins.adapter = adapter
        lifecycleScope.launch {
            viewModel.popularCoinList().observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}