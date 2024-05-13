package com.baltsarak.cryptopricealert.presentation.fragments

import android.content.Context
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
import com.baltsarak.cryptopricealert.presentation.CryptoApp
import com.baltsarak.cryptopricealert.presentation.models.ViewModelFactory
import com.baltsarak.cryptopricealert.presentation.adapters.NewsAdapter
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomTitle
import com.baltsarak.cryptopricealert.presentation.contract.navigator
import com.baltsarak.cryptopricealert.presentation.models.NewsViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewsFragment : Fragment(), HasCustomTitle {

    private lateinit var viewModel: NewsViewModel
    private lateinit var adapter: NewsAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _binding: FragmentNewsBinding? = null
    private val binding: FragmentNewsBinding
        get() = _binding ?: throw RuntimeException("FragmentNewsBinding is null")

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
        viewModel = ViewModelProvider(this, viewModelFactory)[NewsViewModel::class.java]
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