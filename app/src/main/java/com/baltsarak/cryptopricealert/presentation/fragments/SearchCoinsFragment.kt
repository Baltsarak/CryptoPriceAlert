package com.baltsarak.cryptopricealert.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.baltsarak.cryptopricealert.databinding.FragmentSearchCoinsBinding
import com.baltsarak.cryptopricealert.domain.entities.CoinName
import com.baltsarak.cryptopricealert.presentation.CryptoApp
import com.baltsarak.cryptopricealert.presentation.ViewModelFactory
import com.baltsarak.cryptopricealert.presentation.adapters.CoinNameAdapter
import com.baltsarak.cryptopricealert.presentation.contract.navigator
import com.baltsarak.cryptopricealert.presentation.models.CoinListsViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchCoinsFragment : Fragment() {

    private lateinit var viewModel: CoinListsViewModel
    private lateinit var adapter: CoinNameAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _binding: FragmentSearchCoinsBinding? = null
    private val binding: FragmentSearchCoinsBinding
        get() = _binding ?: throw RuntimeException("FragmentSearchCoinsBinding is null")

    private val component by lazy {
        (requireActivity().application as CryptoApp).component
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, viewModelFactory)[CoinListsViewModel::class.java]
        adapter = CoinNameAdapter()
        _binding = FragmentSearchCoinsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewAllCoins.adapter = adapter
        loadData()
        setClickListener()
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val coinList = viewModel.getListCoinNames()
            adapter.submitList(coinList)
            viewModel.coinListLiveData.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
        }
    }

    private fun setClickListener() {
        adapter.onCoinClickListener = object : CoinNameAdapter.OnCoinClickListener {
            override fun onCoinClick(name: CoinName) {
                navigator().closeSearchView()
                navigator().showCoinInfo(name.fromSymbol)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}