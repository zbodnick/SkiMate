package com.bodnick.skimate

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

//import sun.jvm.hotspot.utilities.IntArray


//import sun.jvm.hotspot.utilities.IntArray


class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var username: EditText

    private lateinit var password: EditText

    private lateinit var login: Button

    override fun onStart() {
        super.onStart()

//        if (firebaseAuth.currentUser != null) {
//            val user = firebaseAuth.currentUser
//            Toast.makeText(this, "Welcome back, ${user!!.email}",
//                Toast.LENGTH_SHORT).show()
//
//            val intent = Intent(this, CourseManagerActivity::class.java)
//            startActivity(intent)
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val preferences: SharedPreferences = getSharedPreferences(
            "ski-mate",
            Context.MODE_PRIVATE
        )

        // Tells Android which layout file should be used for this screen.
        setContentView(R.layout.activity_main)

        // The IDs we are using here should match what was set in the "id" field in our XML layout
        // Note: findViewById only works here because we've already called setContentView above.
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)

        // Using a lambda to implement a View.OnClickListener interface. We can do this because
        // an OnClickListener is an interface that only requires *one* function.
        login.setOnClickListener { view: View ->
            Log.d("MainActivity", "onClick() called")

            // Save user credentials to file
            val inputtedUsername: String = username.text.toString()
            val inputtedPassword: String = password.text.toString()

            firebaseAuth
                .signInWithEmailAndPassword(inputtedUsername, inputtedPassword)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        val currentUser: FirebaseUser = firebaseAuth.currentUser!!
                        val email = currentUser.email

                        Toast.makeText(
                            this,
                            "Signed in as $email!",
                            Toast.LENGTH_SHORT
                        ).show()

                        preferences
                            .edit()
                            .putString("username", inputtedUsername)
                            .putString("password", inputtedPassword)
                            .apply()

                        // An Intent is used to start a new Activity and also send data to it (via `putExtra(...)`)
                        val intent = Intent(this@MainActivity, CourseManagerActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    } else {
                        val exception = task.exception!!
                        Toast.makeText(
                            this,
                            "Failed to sign in: $exception!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        // Kotlin shorthand for login.setEnabled(false).
        // If getter / setter is unambiguous, Kotlin lets you use the dot-style syntax
        login.isEnabled = false

        username.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)

        val savedUsername = preferences.getString("username", "")
        val savedPassword = preferences.getString("password", "")

        // By calling setText now, *after* having called addTextChangedListener above, causes my TextWatcher
        // code to execute. This is useful because it runs the logic to enable / disable the Login button,
        // so that it will be enabled if I fill the username / password from SharedPreferences.
        username.setText(savedUsername)
        password.setText(savedPassword)
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
            val inputtedUsername: String = username.text.toString()
            val inputtedPassword: String = password.text.toString()
            val enable: Boolean = inputtedUsername.trim().isNotEmpty() && inputtedPassword.trim().isNotEmpty()

            login.isEnabled = enable
        }
    }

    fun goToSignup(v: View?) {
        val view = findViewById<TextView>(R.id.needs_account)
        view.setTextColor(Color.WHITE)

        val intent = Intent(this@MainActivity, RegisterActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

    }
}