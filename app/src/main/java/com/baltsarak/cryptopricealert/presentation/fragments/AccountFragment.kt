package com.baltsarak.cryptopricealert.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.baltsarak.cryptopricealert.databinding.FragmentAccountBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth


class AccountFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentAccountBinding? = null
    private val binding: FragmentAccountBinding
        get() = _binding ?: throw RuntimeException("FragmentAccountBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView: ImageView = binding.accountLogo
        Glide.with(this)
            .load(com.baltsarak.cryptopricealert.R.drawable.user)
            .into(imageView)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}