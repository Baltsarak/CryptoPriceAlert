package com.baltsarak.cryptopricealert.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.FragmentWatchlistBinding
import com.baltsarak.cryptopricealert.domain.entities.CoinInfo
import com.baltsarak.cryptopricealert.presentation.CryptoApp
import com.baltsarak.cryptopricealert.presentation.ViewModelFactory
import com.baltsarak.cryptopricealert.presentation.adapters.CoinInfoAdapter
import com.baltsarak.cryptopricealert.presentation.contract.CustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomTitle
import com.baltsarak.cryptopricealert.presentation.contract.navigator
import com.baltsarak.cryptopricealert.presentation.models.WatchListViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class WatchListFragment : Fragment(), HasCustomTitle, HasCustomAction {

    private lateinit var viewModel: WatchListViewModel
    private lateinit var adapter: CoinInfoAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _binding: FragmentWatchlistBinding? = null
    private val binding: FragmentWatchlistBinding
        get() = _binding ?: throw RuntimeException("FragmentWatchlistBinding is null")

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
        viewModel = ViewModelProvider(this, viewModelFactory)[WatchListViewModel::class.java]
        adapter = CoinInfoAdapter()
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewWatchList.visibility = View.GONE
        binding.progressBarWatchList.visibility = View.VISIBLE
        binding.recyclerViewWatchList.adapter = adapter
        val itemAnimator = binding.recyclerViewWatchList.itemAnimator
        if (itemAnimator is DefaultItemAnimator) {
            itemAnimator.supportsChangeAnimations = false
        }
        loadData()
        setupClickListener()
        setupSwipeAndMoveListener()
        binding.progressBarWatchList.visibility = View.GONE
        binding.recyclerViewWatchList.visibility = View.VISIBLE
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val watchList = viewModel.getWatchListCoins()
            if (watchList.isEmpty()) {
                binding.recyclerViewWatchList.visibility = View.GONE
                binding.textHint.visibility = View.VISIBLE
            } else {
                adapter.submitList(watchList)
                viewModel.watchListLiveData.observe(viewLifecycleOwner) {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun setupClickListener() {
        adapter.onCoinClickListener = object : CoinInfoAdapter.OnCoinClickListener {
            override fun onCoinClick(coinPriceInfo: CoinInfo) {
                navigator().showCoinInfo(coinPriceInfo.fromSymbol)
            }
        }
    }

    private fun setupSwipeAndMoveListener() {
        val callback = object : ItemTouchHelper.Callback() {

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return true
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.START
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val newList = adapter.onItemMove(
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )
                viewModel.rewriteWatchList(newList)
                return true
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewModel.rewriteWatchListInRemoteDatabase()
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.onItemDismiss(viewHolder.adapterPosition)
                val item = adapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteCoinFromWatchList(item.fromSymbol)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewWatchList)
    }

    override fun getTitleRes(): Int = R.string.watchlist

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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    interface ItemTouchHelperAdapter {
        fun onItemMove(fromPosition: Int, toPosition: Int): List<CoinInfo>
        fun onItemDismiss(position: Int)
    }
}