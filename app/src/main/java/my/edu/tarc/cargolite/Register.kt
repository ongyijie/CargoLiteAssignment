package my.edu.tarc.cargolite

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {
    //Global Variable
    private lateinit var username1: EditText
    private lateinit var password1: EditText
    private lateinit var email1: EditText
    private lateinit var phoneNumber1: EditText
    private lateinit var position1: EditText

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val profileRef: CollectionReference = database.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val imageViewBack: ImageView = findViewById(R.id.imageViewBack)

        imageViewBack.setOnClickListener {
            val intentLogin = Intent(this, Login::class.java)
            startActivity(intentLogin)
        }

        username1 = findViewById(R.id.editTextUsername)
        password1 = findViewById(R.id.editTextPassword)
        email1 = findViewById(R.id.editTextEmail)
        phoneNumber1 = findViewById(R.id.editTextPhoneNumber)
        position1 = findViewById(R.id.editTextPosition)

        val registerBtn : Button = findViewById(R.id.RegisterButton)

//        auth = Firebase.auth
        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)

        registerBtn.setOnClickListener {
            val userName = username1.text.toString()
            val email = email1.text.toString()
            val password = password1.text.toString()
            val phone = phoneNumber1.text.toString()
            val position = position1.text.toString()
            var status = "true"

            if (TextUtils.isEmpty(userName)) {
                username1.error = "Username is required"
                status = "false"
            }
            if (TextUtils.isEmpty(password) || (password.length <= 6)) {
                password1.error = "Password must be more than 6 characters"
                status = "false"
            }
            if (TextUtils.isEmpty(email)) {
                email1.error = "Email is required"
                status = "false"
            }
            if (TextUtils.isEmpty(phone)) {
                phoneNumber1.error = "Phone Number is required"
                status = "false"
            }
            if (TextUtils.isEmpty(position)) {
                position1.error = "Position is required"
                status = "false"
            }

            if (position.toUpperCase() !="WORKER" && position.toUpperCase() != "MANAGER") {
                position1.error = "Are you a manager or worker?"
                status = "false"
            }

            if (status=="true") {
                progressBar.setVisibility(View.VISIBLE)
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(baseContext, "Registration successful",
                                Toast.LENGTH_SHORT).show()

                            //Creating user in cloud firestore
                            val check = userName.isNotEmpty() && email.isNotEmpty() && phone.toString().isNotEmpty() && position.toString().isNotEmpty()
                            if (check) {
                                val users = hashMapOf(
                                    "userName" to userName,
                                    "email" to email,
                                    "phone" to phone,
                                    "position" to position
                                )
                                profileRef.document("$userName").set(users)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d("TAG", "DocumentSnapshot added")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("TAG", "Error adding document", e)
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d("TAG", "get failed with ", exception)
                                    }
                            }
                            val user = auth.currentUser
                            startActivity(Intent(this,Login::class.java))

                        } else {
                            Toast.makeText(baseContext, "Registration failed, please try again",
                                Toast.LENGTH_SHORT).show()
                            progressBar.setVisibility(View.GONE)
                        }
                    }

            }
        }
    }
}


