package com.baltsarak.cryptopricealert.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.baltsarak.cryptopricealert.databinding.ActivityLoginBinding
import com.baltsarak.cryptopricealert.presentation.sign_in.GoogleAuthUiClient
import com.baltsarak.cryptopricealert.presentation.sign_in.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showExitConfirmationDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val viewModel = ViewModelProvider(this)[SignInViewModel::class.java]
        onBackPressedDispatcher.addCallback(this, callback)

        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    lifecycleScope.launch {
                        val signInResult = googleAuthUiClient.signInWithIntent(
                            intent = result.data ?: return@launch
                        )
                        viewModel.onSignInResult(signInResult)
                        navigateToMain()
                    }
                }
            }
        binding.signUpByEmail.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.buttonLogin.setOnClickListener { loginUserByEmail() }
        binding.buttonLoginWithGoogle.setOnClickListener {
            lifecycleScope.launch {
                val signInIntent = googleAuthUiClient.signIn() ?: return@launch
                val intentSenderRequest = IntentSenderRequest.Builder(signInIntent).build()
                googleSignInLauncher.launch(intentSenderRequest)
            }
        }
        binding.buttonAnonymousLogin.setOnClickListener { signInAnonymously() }
    }

    private fun loginUserByEmail() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            binding.progressBar.visibility = View.GONE
            if (task.isSuccessful) {
                navigateToMain()
            } else {
                Log.w("FirebaseAuth", "Login:failure", task.exception)
                Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    navigateToMain()
                } else {
                    Log.w("FirebaseAuth", "signInAnonymously:failure", task.exception)
                    Toast.makeText(baseContext, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToMain() {
        setResult(RESULT_OK)
        finish()
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                setResult(RESULT_CANCELED)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}