package com.baltsarak.cryptopricealert.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.baltsarak.cryptopricealert.databinding.FragmentCoinDetailInfoBinding
import kotlinx.coroutines.launch

class CoinDetailInfoFragment : Fragment() {

    private lateinit var viewModel: CoinViewModel

    private var _binding: FragmentCoinDetailInfoBinding? = null
    val binding: FragmentCoinDetailInfoBinding
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
            viewModel.getCoinDetailInfo(fromSymbol).observe(viewLifecycleOwner) {
                with(binding) {

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