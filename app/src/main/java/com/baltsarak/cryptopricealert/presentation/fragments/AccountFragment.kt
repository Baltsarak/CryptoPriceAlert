package com.baltsarak.cryptopricealert.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.FragmentAccountBinding
import com.baltsarak.cryptopricealert.presentation.CryptoApp
import com.baltsarak.cryptopricealert.presentation.models.ViewModelFactory
import com.baltsarak.cryptopricealert.presentation.contract.CustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomAction
import com.baltsarak.cryptopricealert.presentation.contract.HasCustomTitle
import com.baltsarak.cryptopricealert.presentation.contract.navigator
import com.baltsarak.cryptopricealert.presentation.models.WatchListViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject


class AccountFragment : Fragment(), HasCustomTitle, HasCustomAction {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: WatchListViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _binding: FragmentAccountBinding? = null
    private val binding: FragmentAccountBinding
        get() = _binding ?: throw RuntimeException("FragmentAccountBinding is null")

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
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this, viewModelFactory)[WatchListViewModel::class.java]
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

    override fun getCustomAction(): CustomAction {
        return CustomAction(
            iconRes = R.drawable.icon_exit,
            textRes = R.string.log_out
        ) {
            viewModel.deleteAllFromWatchList()
            auth.signOut()
            navigator().goToLogin()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}