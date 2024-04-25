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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.FragmentCoinDetailInfoBinding
import com.baltsarak.cryptopricealert.domain.entities.TargetPrice
import com.baltsarak.cryptopricealert.presentation.CoinViewModel
import com.baltsarak.cryptopricealert.presentation.adapters.TargetPriceAdapter
import com.baltsarak.cryptopricealert.presentation.contract.CustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomTitle
import com.baltsarak.cryptopricealert.presentation.contract.navigator
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.LimitLine
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
    private lateinit var targetPrices: List<TargetPrice?>
    private var checkedRadioButton: Int = MONTH

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
        binding.priceChart.visibility = View.INVISIBLE
        binding.progressBarPriceChart.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            fillingView(fromSymbol)
            viewModel.loadCoinPriceHistoryInfo(fromSymbol)
            settingPriceChart(MONTH)
            binding.radioButtonOption3.isChecked = true
            binding.progressBarPriceChart.visibility = View.GONE
            binding.priceChart.visibility = View.VISIBLE
        }
        setOnCheckedChangeListener()
        setOnClickListener()
        setupSwipeListener()
    }

    private suspend fun fillingView(fromSymbol: String) {
        viewModel.getCoinDetailInfo(fromSymbol).observe(viewLifecycleOwner) {
            targetPrices = it.targetPrice
            with(binding) {
                textViewCoinName.text = it.fullName
                textViewCoinSymbol.text = it.fromSymbol
                Glide.with(this@CoinDetailInfoFragment)
                    .load(it.imageUrl)
                    .into(coinLogo)
                "$${it.price.toString()}".also { price -> textViewPrice.text = price }
                it.fullName?.let { name -> showTargetPrices(name) }
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
            checkedRadioButton = checkedId
            buttonMap[checkedId]?.let { period ->
                binding.radioGroup.findViewById<View>(checkedId)?.let { animateButton(it) }
                settingPriceChart(period)
            }
        }
    }

    private fun settingPriceChart(period: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val priceHistory = viewModel.getCoinPriceHistory(fromSymbol, period)
            val entries = priceHistory.entries.sortedBy { it.key }.map { Entry(it.key, it.value) }
            val priceHistoryDataSet = LineDataSet(entries, fromSymbol).apply {
                mode = LineDataSet.Mode.CUBIC_BEZIER
                color = Color.WHITE
                lineWidth = 3F
                setDrawValues(false)
                setDrawCircles(false)
                setDrawFilled(true)
                fillColor = Color.WHITE
                fillDrawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.chart_gradient_fill)
            }

            val higherColor = ContextCompat.getColor(requireContext(), R.color.colorPriceHigher)
            val lowerColor = ContextCompat.getColor(requireContext(), R.color.colorPriceLower)

            val leftAxis = binding.priceChart.axisLeft
            leftAxis.removeAllLimitLines()
            targetPrices.filterNotNull().filter { it.targetPrice != 0.0 }.forEach {
                val limitLine =
                    LimitLine(it.targetPrice.toFloat(), it.targetPrice.toString()).apply {
                        lineWidth = 1f
                        lineColor = if (it.higherThenCurrent) higherColor else lowerColor
                        textColor = lineColor
                        textSize = 12f
                    }
                leftAxis.addLimitLine(limitLine)
            }

            with(binding.priceChart) {
                axisRight.isEnabled = false
                xAxis.isEnabled = false
                axisLeft.textColor = Color.WHITE
                data = LineData(priceHistoryDataSet)
                invalidate()
            }
        }
    }

    private fun showTargetPrices(name: String) {
        val (pricesIncrease, pricesDecrease) = targetPrices
            .filterNotNull()
            .filter { it.targetPrice != 0.0 }
            .partition { it.higherThenCurrent }

        with(binding) {
            targetPriceNotificationMessage.visibility =
                if (pricesIncrease.isNotEmpty() || pricesDecrease.isNotEmpty()) View.VISIBLE else View.GONE
            targetPriceName.text = name
            targetPriceName.visibility = targetPriceNotificationMessage.visibility
            targetPriceDeleteMessage.visibility = targetPriceNotificationMessage.visibility

            updateVisibilityAndSetData(
                pricesIncrease,
                targetPriceIncreaseContainer,
                targetPriceIncrease,
                true
            )
            updateVisibilityAndSetData(
                pricesDecrease,
                targetPriceDecreaseContainer,
                targetPriceDecrease,
                pricesIncrease.isNotEmpty()
            )
        }
    }

    private fun updateVisibilityAndSetData(
        prices: List<TargetPrice>,
        container: RecyclerView,
        textViewVisibilityView: View,
        pricesIncreaseNotEmpty: Boolean
    ) {
        val adapterTargetPrice = (container.adapter as? TargetPriceAdapter) ?: TargetPriceAdapter()
        if (container.layoutManager == null) {
            container.layoutManager = LinearLayoutManager(context)
        }
        container.adapter = adapterTargetPrice
        adapterTargetPrice.submitList(prices)

        if (prices.isNotEmpty()) {
            if (pricesIncreaseNotEmpty && textViewVisibilityView == binding.targetPriceDecrease) {
                binding.orTargetPriceDecrease.visibility = View.VISIBLE
                container.visibility = View.VISIBLE
                textViewVisibilityView.visibility = View.GONE
            } else {
                textViewVisibilityView.visibility = View.VISIBLE
                container.visibility = View.VISIBLE
            }
        } else {
            textViewVisibilityView.visibility = View.GONE
            container.visibility = View.GONE
            if (textViewVisibilityView == binding.targetPriceDecrease) {
                binding.orTargetPriceDecrease.visibility = View.GONE
            }
        }
    }

    private suspend fun addCoinToWatchList(): Deferred<Unit> {
        val targetPrice = binding.targetPrice.text.toString()
        return viewModel.addCoinToWatchList(fromSymbol, targetPrice)
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
                    addToWatchListAndStartWorker()
                }
            } else {
                addToWatchListAndStartWorker()
            }
        }
    }

    private fun addToWatchListAndStartWorker() {
        animateButton(binding.buttonAdd)
        viewLifecycleOwner.lifecycleScope.launch {
            addCoinToWatchList().await()
        }
        binding.targetPrice.setText("")
        settingPriceChart(definePeriod())
        viewModel.startWorker()
    }

    private fun setupSwipeListener() {
        val itemTouchHelperIncrease =
            ItemTouchHelper(createSwipeCallback(binding.targetPriceIncreaseContainer))
        itemTouchHelperIncrease.attachToRecyclerView(binding.targetPriceIncreaseContainer)

        val itemTouchHelperDecrease =
            ItemTouchHelper(createSwipeCallback(binding.targetPriceDecreaseContainer))
        itemTouchHelperDecrease.attachToRecyclerView(binding.targetPriceDecreaseContainer)
    }

    private fun createSwipeCallback(recyclerView: RecyclerView): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as TargetPriceAdapter
                val item = adapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteTargetPrice(item.fromSymbol, item.targetPrice)
                adapter.onItemDismiss(viewHolder.adapterPosition)
                settingPriceChart(definePeriod())
            }
        }
    }

    private fun definePeriod(): Int {
        return when (checkedRadioButton) {
            R.id.radioButtonOption1 -> DAY
            R.id.radioButtonOption2 -> WEEK
            R.id.radioButtonOption4 -> YEAR
            R.id.radioButtonOption5 -> FIVE_YEARS
            R.id.radioButtonOption6 -> ALL
            else -> {
                MONTH
            }
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

    interface ItemTouchHelperAdapter {
        fun onItemDismiss(position: Int)
    }
}