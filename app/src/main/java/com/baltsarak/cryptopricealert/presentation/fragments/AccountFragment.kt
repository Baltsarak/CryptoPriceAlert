package com.baltsarak.cryptopricealert.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.FragmentAccountBinding
import com.baltsarak.cryptopricealert.presentation.CoinViewModel
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomTitle
import com.baltsarak.cryptopricealert.presentation.contract.navigator
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AccountFragment : Fragment(), HasCustomTitle {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: CoinViewModel

    private var _binding: FragmentAccountBinding? = null
    private val binding: FragmentAccountBinding
        get() = _binding ?: throw RuntimeException("FragmentAccountBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(requireActivity())[CoinViewModel::class.java]
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth.currentUser?.let { user ->
            configureUserInterface(user)
        }
    }

    private fun configureUserInterface(user: FirebaseUser) {
        when {
            user.isAnonymous -> configureAnonymousUser()
            else -> configureAuthenticatedUser(user)
        }
        binding.exit.setOnClickListener {
            viewModel.deleteAllFromWatchList()
            auth.signOut()
            navigator().goToLogin()
        }
    }

    private fun configureAnonymousUser() {
        binding.apply {
            name.text = getString(R.string.anonymous)
            accountEmail.visibility = View.GONE
            registerMessage.visibility = View.VISIBLE
            buttonRegister.apply {
                visibility = View.VISIBLE
                setOnClickListener { navigator().goToRegister() }
            }
        }
    }

    private fun configureAuthenticatedUser(user: FirebaseUser) {
        binding.apply {
            buttonRegister.visibility = View.GONE
            user.photoUrl?.let { url ->
                ImageViewCompat.setImageTintList(image, null)
                Glide.with(this@AccountFragment)
                    .load(url)
                    .circleCrop()
                    .into(image)
            }
            accountEmail.text = user.email ?: ""
            name.text = user.displayName ?: ""
        }
    }

    override fun getTitleRes(): Int = R.string.profile

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}