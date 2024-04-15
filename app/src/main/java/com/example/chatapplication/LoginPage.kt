package com.example.chatapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {

    private lateinit var buttonSignUp:  Button
    private lateinit var buttonLogin:  Button
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText

    private lateinit var savedUser: String

    private lateinit var sharedPreferences: SharedPreferences


    private  lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        buttonSignUp = findViewById(R.id.buttonSignUp)
        buttonLogin = findViewById(R.id.buttonLogin)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)

        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE)

        val savedUser = sharedPreferences.getString("email", "")

        if (!savedUser.isNullOrEmpty()) {
            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        } else {
            buttonSignUp.setOnClickListener {
                val intent = Intent(this, SignUpPage::class.java)
                startActivity(intent)
            }

            buttonLogin.setOnClickListener {
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                login(email, password)
            }
        }
    }
    private  fun login(email: String, password: String){

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val intent = Intent(this, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                    saveUser(email)


                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()

                }
            }
    }

    private fun saveUser(email: String){
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.apply()
    }
}