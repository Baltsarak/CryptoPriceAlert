package com.baltsarak.cryptopricealert.presentation.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.FragmentCoinDetailInfoBinding
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.presentation.CoinViewModel
import com.baltsarak.cryptopricealert.presentation.contract.CustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomTitle
import com.baltsarak.cryptopricealert.presentation.contract.navigator
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch

class CoinDetailInfoFragment : Fragment(), HasCustomTitle, HasCustomAction {

    private lateinit var viewModel: CoinViewModel

    private var _binding: FragmentCoinDetailInfoBinding? = null
    private val binding: FragmentCoinDetailInfoBinding
        get() = _binding ?: throw RuntimeException("FragmentCoinDetailInfoBinding is null")

    lateinit var fromSymbol: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[CoinViewModel::class.java]
        _binding = FragmentCoinDetailInfoBinding.inflate(inflater, container, false)
        fromSymbol = requireArguments().getString(EXTRA_FROM_SYMBOL, EMPTY_SYMBOL)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.priceChart.visibility = View.GONE
        binding.progressBarPriceChart.visibility = View.VISIBLE
        loadDataAndFillingView(fromSymbol)
        binding.progressBarPriceChart.visibility = View.GONE
        binding.priceChart.visibility = View.VISIBLE
        setOnClickListener()
    }

    private fun loadDataAndFillingView(fromSymbol: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadCoinPriceHistoryInfo(fromSymbol)
            viewModel.getCoinDetailInfo(fromSymbol).observe(viewLifecycleOwner) {
                with(binding) {
                    textViewCoinName.text = it.fromSymbol
                    textViewPrice.text = it.price.toString()
                    val coinPriceChart = priceChart
                    settingPriceChart(coinPriceChart, it)
                }
            }
        }
    }

    private fun settingPriceChart(coinPriceChart: LineChart, coinInfo: CoinInfo) {
        val entries = ArrayList<Entry>()
        viewLifecycleOwner.lifecycleScope.launch {
            val coinMap = viewModel.getCoinPriceHistory(fromSymbol)
            for (data in coinMap) {
                entries.add(Entry(data.key, data.value))
            }
            val priceHistoryDataSet = LineDataSet(entries, coinInfo.fromSymbol)
            with(priceHistoryDataSet) {
                mode = LineDataSet.Mode.CUBIC_BEZIER
                color = Color.WHITE
                lineWidth = 5F
                setDrawValues(false)
                setDrawCircles(false)
                setDrawFilled(true)
                fillColor = Color.WHITE
                fillDrawable = ContextCompat.getDrawable(
                    requireContext(), R.drawable.chart_gradient_fill
                )
            }
            with(coinPriceChart) {
                axisRight.isEnabled = false
                xAxis.isEnabled = false
                axisLeft.textColor = Color.WHITE
                data = LineData(priceHistoryDataSet)
            }
            coinPriceChart.invalidate()
        }
    }

    private fun setOnClickListener() {
        binding.buttonAdd.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this.requireActivity(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this.requireActivity(),
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        REQUEST_CODE_POST_NOTIFICATIONS
                    )
                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        addCoinToWatchList().await()
                    }
                    viewModel.startWorker()
                }
            }
        }
    }

    private suspend fun addCoinToWatchList(): Deferred<Unit> {
        val targetPrice = binding.targetPrice.text.toString()
        return viewModel.addCoinToWatchList(fromSymbol, targetPrice)
    }

    override fun getCustomAction(): CustomAction {
        return CustomAction(
            iconRes = R.drawable.add,
            textRes = R.string.add
        ) {
            viewLifecycleOwner.lifecycleScope.launch {
                addCoinToWatchList().await()
                navigator().showWatchList()
            }
        }
    }

    override fun getTitleRes(): Int = R.string.cryptocurrency

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {

        private const val REQUEST_CODE_POST_NOTIFICATIONS = 11
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