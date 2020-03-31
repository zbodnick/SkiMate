package com.bodnick.skimate

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {

    /*

    We use `lateinit var` for our UI variables because they cannot be initialized until
    setContentView(...) is called in onCreate(...) below.

    For example, this line would cause a crash:
        private val username: EditText = findViewById(R.id.username)

    Alternatively, could also use a nullable variable, but it'd be inconvenient to do a null-check on each usage:
        private var username: EditText? = null

    So `lateinit var` acts as a "promise" to the compiler that we cannot initialize the variable right now,
    but we will later *and* when we do it'll be non-null.

    If you forget to initialize a `lateinit` and then try and use it, the app will crash.

    */

    private lateinit var firebaseAuth: FirebaseAuth

    //    private lateinit var fname: EditText
//    private lateinit var lname: EditText
//    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var email: EditText

    private lateinit var signup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        // Tells Android which layout file should be used for this screen.
        setContentView(R.layout.activity_register)

        // The IDs we are using here should match what was set in the "id" field in our XML layout
        // Note: findViewById only works here because we've already called setContentView above.
//        username = findViewById(R.id.username)
        password = findViewById(R.id.signup_password)
//        fname = findViewById(R.id.password)
//        lname = findViewById(R.id.password)
        email = findViewById(R.id.signup_username)
        signup = findViewById(R.id.signup)

        signup.setOnClickListener {
            // Save user credentials to file
            val inputtedUsername: String = email.text.toString()
            val inputtedPassword: String = password.text.toString()

            firebaseAuth
                .createUserWithEmailAndPassword(inputtedUsername, inputtedPassword)

                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        val currentUser: FirebaseUser = firebaseAuth.currentUser!!
                        val email = currentUser.email

                        Toast.makeText(
                            this,
                            "Welcome to Ski Mate!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val exception = task.exception!!
                        Toast.makeText(
                            this,
                            "Failed to register: $exception!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        // Kotlin shorthand for login.setEnabled(false).
        // If getter / setter is unambiguous, Kotlin lets you use the dot-style syntax
        signup.isEnabled = false

        email.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)

    }

    // Another example of explicitly implementing an interface (TextWatcher). We cannot use
    // a lambda in this case since there are multiple (3) functions we need to implement.
    //
    // We're defining an "anonymous class" here using the `object` keyword (basically created
    // a new, dedicated object to implement a TextWatcher for this variable assignment.
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // username.text == Kotlin shorthand for username.getText()
            val inputtedUsername: String = email.text.toString()
            val inputtedPassword: String = password.text.toString()
            val enable: Boolean = inputtedUsername.trim().isNotEmpty() && inputtedPassword.trim().isNotEmpty()

            signup.isEnabled = enable
        }
    }

    fun goToLogin(v: View?) {
        val tv = findViewById<TextView>(R.id.has_account)
        tv.setTextColor(Color.WHITE)

        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}