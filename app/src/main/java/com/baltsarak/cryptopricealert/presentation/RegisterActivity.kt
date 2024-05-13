package com.baltsarak.cryptopricealert.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.baltsarak.cryptopricealert.databinding.ActivityRegisterBinding
import com.baltsarak.cryptopricealert.presentation.models.ViewModelFactory
import com.baltsarak.cryptopricealert.presentation.models.WatchListViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: WatchListViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private val component by lazy {
        (application as CryptoApp).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this, viewModelFactory)[WatchListViewModel::class.java]
        binding.buttonRegister.setOnClickListener {
            registerUser()
        }
        binding.iconBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.passwordEditText.addTextChangedListener(afterTextChanged = {
            binding.iconRepeatPassword.visibility = View.VISIBLE
            binding.repeatPasswordEditText.visibility = View.VISIBLE
        })
    }

    private fun registerUser() {
        val email = binding.emailEditText.text.toString().trim()
        val userName = binding.usernameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val repeatPassword = binding.repeatPasswordEditText.text.toString().trim()

        if (email.isBlank() || userName.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != repeatPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (auth.currentUser?.isAnonymous == true) {
            linkAnonymousAccountToEmail(email, password, userName)
        } else {
            createUserWithEmail(email, password, userName)
        }
    }

    private fun linkAnonymousAccountToEmail(email: String, password: String, userName: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        auth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this,
                    "Account linked and registration successful",
                    Toast.LENGTH_SHORT
                ).show()
                val user = task.result?.user
                user?.let {
                    updateUserProfile(it, userName)
                    saveUserData(it.uid, userName, email)
                }
                navigateToMain(true)
            } else {
                errorProcessing(task)
            }
        }
    }

    private fun createUserWithEmail(email: String, password: String, userName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Registration successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    val user = task.result?.user
                    user?.let {
                        updateUserProfile(it, userName)
                        saveUserData(it.uid, userName, email)
                    }
                    viewModel.deleteAllFromWatchList()
                    navigateToMain(false)
                } else {
                    errorProcessing(task)
                }
            }
    }

    private fun updateUserProfile(user: FirebaseUser, fullName: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = fullName
        }
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("FirebaseAuth", "User profile updated.")
                } else {
                    Log.e("FirebaseAuth", "Error updating profile", task.exception)
                }
            }
    }

    private fun saveUserData(uid: String, fullName: String, email: String) {
        val db = FirebaseFirestore.getInstance()
        val userInfo = hashMapOf(
            "fullName" to fullName,
            "email" to email
        )
        db.collection("users").document(uid).set(userInfo)
            .addOnSuccessListener {
                Log.i("FirebaseAuth", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseAuth", "Error writing document", e)
            }
    }

    private fun errorProcessing(task: Task<AuthResult>) {
        val message = when (task.exception) {
            is FirebaseAuthUserCollisionException -> "This email is already used by another account"
            is FirebaseAuthWeakPasswordException -> "The provided password is too weak"
            is FirebaseAuthInvalidCredentialsException -> "The email address is malformed"
            else -> "Registration failed"
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.w("FirebaseAuth", message, task.exception)
    }

    private fun navigateToMain(showProfile: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("SHOW_PROFILE", showProfile)
        }
        startActivity(intent)
        finish()
    }
}