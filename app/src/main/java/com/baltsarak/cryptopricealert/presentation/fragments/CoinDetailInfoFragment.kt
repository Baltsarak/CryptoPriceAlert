package com.baltsarak.cryptopricealert.presentation.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.FragmentCoinDetailInfoBinding
import com.baltsarak.cryptopricealert.domain.TargetPrice
import com.baltsarak.cryptopricealert.presentation.CoinViewModel
import com.baltsarak.cryptopricealert.presentation.contract.CustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomTitle
import com.baltsarak.cryptopricealert.presentation.contract.navigator
import com.bumptech.glide.Glide
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

    private lateinit var fromSymbol: String

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
        setOnCheckedChangeListener()
        binding.radioButtonOption3.isChecked = true
    }

    private fun loadDataAndFillingView(fromSymbol: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadCoinPriceHistoryInfo(fromSymbol)
            viewModel.getCoinDetailInfo(fromSymbol).observe(viewLifecycleOwner) {
                with(binding) {
                    textViewCoinName.text = it.fullName
                    textViewCoinSymbol.text = it.fromSymbol
                    Glide.with(this@CoinDetailInfoFragment)
                        .load(it.imageUrl)
                        .into(coinLogo)
                    "$${it.price.toString()}".also { textViewPrice.text = it }
                    it.fullName?.let { name -> showTargetPrices(it.targetPrice, name) }
                }
            }
        }
    }

    private fun animateButton(button: View) {
        button.scaleX = 0.7f
        button.animate()
            .scaleX(1f)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun setOnCheckedChangeListener() {
        val buttonMap = mapOf(
            R.id.radioButtonOption1 to DAY,
            R.id.radioButtonOption2 to WEEK,
            R.id.radioButtonOption3 to MONTH,
            R.id.radioButtonOption4 to YEAR,
            R.id.radioButtonOption5 to FIVE_YEARS,
            R.id.radioButtonOption6 to ALL
        )

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            buttonMap[checkedId]?.let { period ->
                binding.radioGroup.findViewById<View>(checkedId)?.let { animateButton(it) }
                settingPriceChart(period)
            }
        }
    }

    private fun settingPriceChart(period: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val entries = ArrayList<Entry>()
            val map = viewModel.getCoinPriceHistory(fromSymbol, period)
            map.entries.sortedBy { it.key }.forEach { (key, value) ->
                entries.add(Entry(key, value))
            }
            val priceHistoryDataSet = LineDataSet(entries, fromSymbol)
            with(priceHistoryDataSet) {
                mode = LineDataSet.Mode.CUBIC_BEZIER
                color = Color.WHITE
                lineWidth = 3F
                setDrawValues(false)
                setDrawCircles(false)
                setDrawFilled(true)
                fillColor = Color.WHITE
                fillDrawable = ContextCompat.getDrawable(
                    requireContext(), R.drawable.chart_gradient_fill
                )
            }
            with(binding.priceChart) {
                axisRight.isEnabled = false
                xAxis.isEnabled = false
                axisLeft.textColor = Color.WHITE
                data = LineData(priceHistoryDataSet)
            }
            binding.priceChart.invalidate()
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
                    animateButton(binding.buttonAdd)
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

    private fun showTargetPrices(list: List<TargetPrice?>, name: String) {
        val (pricesIncrease, pricesDecrease) = list.filterNotNull()
            .partition { it.higherThenCurrent }

        val increaseString = pricesIncrease
            .filter { it.targetPrice != 0.0 }
            .joinToString(separator = ", ") { it.targetPrice.toString() }
            .let { if (it.isNotEmpty()) "$$it" else it }

        val decreaseString = pricesDecrease
            .filter { it.targetPrice != 0.0 }
            .joinToString(separator = ", ") { it.targetPrice.toString() }
            .let { if (it.isNotEmpty()) "$$it" else it }

        with(binding) {
            targetPriceNotificationMessage.visibility =
                if (increaseString.isNotBlank() || decreaseString.isNotBlank()) View.VISIBLE else View.GONE
            targetPriceName.text = name

            targetPriceName.visibility = targetPriceNotificationMessage.visibility
            targetPriceIncrease.visibility =
                if (increaseString.isNotBlank()) View.VISIBLE else View.GONE
            targetPriceIncreaseValue.visibility = targetPriceIncrease.visibility
            targetPriceIncreaseValue.text = increaseString
            targetPriceOr.visibility =
                if (increaseString.isNotBlank() && decreaseString.isNotBlank()) View.VISIBLE else View.GONE
            targetPriceDecrease.visibility =
                if (decreaseString.isNotBlank()) View.VISIBLE else View.GONE
            targetPriceDecreaseValue.visibility = targetPriceDecrease.visibility
            targetPriceDecreaseValue.text = decreaseString

            targetPriceIncreaseValue.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPriceHigher
                )
            )
            targetPriceDecreaseValue.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPriceLower
                )
            )
        }
    }

    override fun getCustomAction(): CustomAction {
        return CustomAction(
            iconRes = R.drawable.add,
            textRes = R.string.add_coin
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
        private const val DAY = 24
        private const val WEEK = 7
        private const val MONTH = 30
        private const val YEAR = 365
        private const val FIVE_YEARS = 5
        private const val ALL = 111

        fun newInstance(fSym: String) =
            CoinDetailInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_FROM_SYMBOL, fSym)
                }
            }
    }
}