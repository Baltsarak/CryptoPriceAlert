package com.baltsarak.cryptopricealert.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.FragmentWebViewBinding
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomTitle
import com.baltsarak.cryptopricealert.presentation.contract.navigator

class WebViewFragment : Fragment(), HasCustomTitle {

    private var _binding: FragmentWebViewBinding? = null
    private val binding: FragmentWebViewBinding
        get() = _binding ?: throw RuntimeException("FragmentWebViewBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.visibility = View.GONE
        binding.progressBarNews.visibility = View.VISIBLE
        val url = requireArguments().getString(EXTRA_URL)
        if (url != null) {
            binding.webView.loadUrl(url)
        } else {
            navigator().goBack()
        }
        binding.progressBarNews.visibility = View.GONE
        binding.webView.visibility = View.VISIBLE
    }

    override fun getTitleRes(): Int = R.string.news

    companion object {
        private const val EXTRA_URL = "url"
        fun newInstance(url: String) =
            WebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_URL, url)
                }
            }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}