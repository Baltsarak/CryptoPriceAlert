package com.baltsarak.cryptopricealert.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.baltsarak.cryptopricealert.databinding.FragmentCoinDetailInfoBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch

class CoinDetailInfoFragment : Fragment() {

    private lateinit var viewModel: CoinViewModel

    private var _binding: FragmentCoinDetailInfoBinding? = null
    private val binding: FragmentCoinDetailInfoBinding
        get() = _binding ?: throw RuntimeException("FragmentCoinDetailInfoBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CoinViewModel::class.java]
        _binding = FragmentCoinDetailInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fromSymbol = getFromSymbol()
        lifecycleScope.launch {
            viewModel.loadCoinPriceHistoryInfo(fromSymbol)
            viewModel.getCoinDetailInfo(fromSymbol).observe(viewLifecycleOwner) {
                with(binding) {
                    textViewCoinName.text = it.fromSymbol
                    textViewPrice.text = it.price.toString()
                    val coinPriceChart = priceChart

                    val entries = ArrayList<Entry>()

                    lifecycleScope.launch {
                        viewModel.getCoinPriceHistory(fromSymbol).observe(viewLifecycleOwner) {
                            for (data in it) {
                                entries.add(Entry(data.key.toFloat(), data.value.toFloat()))
                            }
                        }
                    }
                    Log.d("onViewCreated", entries.toString())
                    val priceHistoryDataSet = LineDataSet(entries, "priceHistory")
                    coinPriceChart.data = LineData(priceHistoryDataSet)
                }
            }
        }
    }

    private fun getFromSymbol(): String {
        return requireArguments().getString(EXTRA_FROM_SYMBOL, EMPTY_SYMBOL)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {

        private const val EXTRA_FROM_SYMBOL = "fSym"
        private const val EMPTY_SYMBOL = ""

        fun newInstance(fSym: String) =
            CoinDetailInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_FROM_SYMBOL, fSym)
                }
            }
    }
}