package com.baltsarak.cryptopricealert.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val oneTapClient by lazy { Identity.getSignInClient(this) }
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        setupGoogleSignIn()
        setupListeners()
    }

    private fun setupGoogleSignIn() {
        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    lifecycleScope.launch {
                        result.data?.let {
                            signInWithIntent(it)
                        } ?: showError("Sign-in intent is null")
                        navigateToMain()
                    }
                }
                binding.progressBar.visibility = View.GONE
            }
    }

    private fun setupListeners() {
        binding.apply {
            buttonLogin.setOnClickListener { loginUserByEmail() }
            buttonLoginWithGoogle.setOnClickListener { initiateGoogleSignIn() }
            buttonAnonymousLogin.setOnClickListener { signInAnonymously() }
            signUpByEmail.setOnClickListener { navigateToRegister() }
        }
    }

    private fun initiateGoogleSignIn() {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                val signInIntent = oneTapClient.beginSignIn(buildSignInRequest()).await()
                val intentSender = signInIntent.pendingIntent.intentSender
                googleSignInLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            } catch (e: Exception) {
                showError("Google sign-in failed")
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private suspend fun signInWithIntent(intent: Intent) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val googleCredentials = GoogleAuthProvider.getCredential(credential.googleIdToken, null)
            auth.signInWithCredential(googleCredentials).await()
        } catch (e: Exception) {
            showError("Google sign-in failed: ${e.localizedMessage}")
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.Builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun loginUserByEmail() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showError("Fields cannot be empty")
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            binding.progressBar.visibility = View.GONE
            if (task.isSuccessful) {
                navigateToMain()
            } else {
                showError("Login failed: ${task.exception?.message}")
            }
        }
    }

    private fun signInAnonymously() {
        binding.progressBar.visibility = View.VISIBLE
        auth.signInAnonymously().addOnCompleteListener { task ->
            binding.progressBar.visibility = View.GONE
            if (task.isSuccessful) {
                navigateToMain()
            } else {
                showError("Anonymous login failed: ${task.exception?.message}")
            }
        }
    }

    private fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun navigateToMain() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}