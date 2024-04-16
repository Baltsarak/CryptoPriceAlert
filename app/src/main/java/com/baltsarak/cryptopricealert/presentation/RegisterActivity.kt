package com.baltsarak.cryptopricealert.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.baltsarak.cryptopricealert.databinding.ActivityRegisterBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.buttonRegister.setOnClickListener {
            registerUser()
        }
        binding.iconBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.iconRepeatPassword.visibility = View.VISIBLE
                binding.repeatPasswordEditText.visibility = View.VISIBLE
            }
        })
    }

    private fun registerUser() {
        val email = binding.emailEditText.text.toString().trim()
        val userName = binding.usernameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val repeatPassword = binding.repeatPasswordEditText.text.toString().trim()

        if (listOf(email, userName, password, repeatPassword).any { it.isBlank() }) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != repeatPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isAnonymous) {
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
                navigateToMain()
            } else {
                Log.w("FirebaseAuth", "Link with credential:failure", task.exception)
                Toast.makeText(this, "Failed to link account", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createUserWithEmail(email: String, password: String, userName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    val user = task.result?.user
                    user?.let {
                        updateUserProfile(it, userName)
                        saveUserData(it.uid, userName, email)
                    }
                    navigateToMain()
                } else {
                    Log.w("FirebaseAuth", "Registration:failure", task.exception)
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_LONG).show()
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
                    Log.d("FirebaseAuth", "User profile updated.")
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
                Log.d("FirebaseAuth", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseAuth", "Error writing document", e)
            }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}