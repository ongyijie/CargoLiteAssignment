package my.edu.tarc.cargolite

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var email1: EditText
    private lateinit var password1: EditText

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email1 = findViewById(R.id.editTextEmail)
        password1 = findViewById(R.id.editTextPassword)

        val registerBtn : Button = findViewById(R.id.RegisterButton)
        val loginBtn : Button = findViewById(R.id.LoginButton)

        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        registerBtn.setOnClickListener{
            startActivity(Intent(this, Register::class.java))
        }

        loginBtn.setOnClickListener{
            val email = email1.text.toString()
            val password = password1.text.toString()
            var status = "true"

            if (TextUtils.isEmpty(email)) {
                email1.error = "Email is required"
                status = "false"
            }
            if (TextUtils.isEmpty(password)) {
                password1.error = "Password is required"
                status = "false"
            }
            if (status=="true") {
                progressBar.setVisibility(View.VISIBLE)
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        progressBar.setVisibility(View.GONE)
                        Toast.makeText(baseContext, "Signed in successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        progressBar.setVisibility(View.GONE)
                        Toast.makeText(
                            baseContext,
                            "Sign in failed, please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}