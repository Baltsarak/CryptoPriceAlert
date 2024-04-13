package com.baltsarak.cryptopricealert.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.baltsarak.cryptopricealert.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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

        createUserWithEmail(email, password, userName)
    }

    private fun createUserWithEmail(email: String, password: String, userName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    saveUserToDatabase(email, userName)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveUserToDatabase(email: String, userName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userInfo = hashMapOf("email" to email, "username" to userName)

        FirebaseDatabase.getInstance().reference.child("Users").child(userId).setValue(userInfo)
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save user info: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}