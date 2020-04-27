package com.bodnick.skimate

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Explode
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var email: EditText

    private lateinit var fbDatabase: FirebaseDatabase

    private lateinit var signup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_register)

        password = findViewById(R.id.signup_password)
        confirmPassword = findViewById(R.id.signup_password_confirm)

        email = findViewById(R.id.signup_username)
        signup = findViewById(R.id.signup)

        signup.setOnClickListener {
            // Save user credentials to file
            val inputtedUsername: String = email.text.toString()
            val inputtedPassword: String = password.text.toString()
            val inputtedConfirmPassword: String = confirmPassword.text.toString()

            if (inputtedPassword == inputtedConfirmPassword) {

                firebaseAuth
                    .createUserWithEmailAndPassword(inputtedUsername, inputtedPassword)

                    .addOnCompleteListener { task: Task<AuthResult> ->
                        if (task.isSuccessful) {
                            val currentUser: FirebaseUser = firebaseAuth.currentUser!!
                            val email = currentUser.email
//                            addDefaultCourse()

                            Toast.makeText(
                                this,
                                "Welcome to Ski Mate!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent =
                                Intent(this@RegisterActivity, CourseManagerActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                            )
                        } else {
                            val exception = task.exception!!
                            Toast.makeText(
                                this,
                                "Failed to register: $exception!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this,
                    "Passwords must be the same, try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Kotlin shorthand for login.setEnabled(false).
        // If getter / setter is unambiguous, Kotlin lets you use the dot-style syntax
        signup.isEnabled = false

        email.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)

    }

    fun addDefaultCourse() {
        val currentUser = FirebaseAuth.getInstance().currentUser
//        val email = currentUser?.email as String
//        val filteredEmail = email.filter{ it.isLetterOrDigit() || it.isWhitespace() }
        val reference = fbDatabase.getReference("${currentUser?.uid}/courses/")

        val id = genID()
            val course = Course(
                "Your Slalom Course",
                " West Palm Beach, FL 33405, USA",
                "26.681755201359202",
                "-80.07176410406828",
                "",
                "",
                "",
                "",
                "",
                "0.0",
                id
            )
            reference.child(id).setValue(course)
    }

    fun genID(): String {
        return UUID.randomUUID().toString()
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
        val view = findViewById<TextView>(R.id.has_account)
        view.setTextColor(Color.WHITE)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}