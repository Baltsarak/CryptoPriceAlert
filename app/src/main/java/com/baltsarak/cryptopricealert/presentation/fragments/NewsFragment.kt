package com.baltsarak.cryptopricealert.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.FragmentNewsBinding
import com.baltsarak.cryptopricealert.domain.entities.News
import com.baltsarak.cryptopricealert.presentation.CoinViewModel
import com.baltsarak.cryptopricealert.presentation.adapters.NewsAdapter
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomTitle
import com.baltsarak.cryptopricealert.presentation.contract.navigator
import kotlinx.coroutines.launch

class NewsFragment : Fragment(), HasCustomTitle {

    private lateinit var viewModel: CoinViewModel
    private lateinit var adapter: NewsAdapter

    private var _binding: FragmentNewsBinding? = null
    private val binding: FragmentNewsBinding
        get() = _binding ?: throw RuntimeException("FragmentNewsBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[CoinViewModel::class.java]
        adapter = NewsAdapter()
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewNews.visibility = View.GONE
        binding.progressBarNews.visibility = View.VISIBLE
        binding.recyclerViewNews.adapter = adapter
        loadData()
        setClickListener()
        binding.progressBarNews.visibility = View.GONE
        binding.recyclerViewNews.visibility = View.VISIBLE
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val news = viewModel.getNewsList().filter { it.body.length < 1000 }
            adapter.submitList(news)
        }
    }

    private fun setClickListener() {
        adapter.onNewsClickListener = object : NewsAdapter.OnNewsClickListener {
            override fun onNewsClick(news: News) {
                if (news.url.isNotBlank()) {
                    navigator().showWebView(news.url)
                }
            }
        }
    }

    override fun getTitleRes(): Int = R.string.news

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}