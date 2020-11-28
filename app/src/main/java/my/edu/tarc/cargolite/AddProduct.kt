package my.edu.tarc.cargolite

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_product.*

class AddProduct : AppCompatActivity() {

    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionRef: CollectionReference = database.collection("products")
    val START_IDSCANNER_REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imageViewBarcode: ImageView = findViewById(R.id.imageViewBarcode)

        imageViewBarcode.setOnClickListener {
            val intentBarcode = Intent(this, IDScanner::class.java)
            startActivityForResult(intentBarcode, START_IDSCANNER_REQUEST_CODE)
        }

        val addBtn: Button = findViewById(R.id.closeBtn)

        //Add button click of custom layout
        addBtn.setOnClickListener {
            //Get text from EditTexts of custom layout
            val productID = detailsID.text.toString()
            val productName = detailsName.text.toString().trim()
            val productPrice = detailsPrice.text.toString().trim()
            val productLocation = detailsLocation.text.toString().trim()
            val productQuantity = detailsQuantity.text.toString().trim()

            if (productID.isEmpty()) {
                detailsID.error = "ID is required!"
                detailsID.requestFocus()
            } else if (productID.isNotEmpty()) {
                val docRef: DocumentReference = collectionRef.document("$productID")
                docRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null) {
                            val referredID = document.getString("productID")
                            if (referredID == productID) {
                                detailsID.error = "Product exists!"
                                detailsID.requestFocus()
                            }
                        } else {
                            Log.d("LOGGER", "No such document")
                        }
                    } else {
                        Log.d("LOGGER", "get failed with ", task.exception)
                    }
                }
            }

            if (productName.isEmpty()) {
                detailsName.error = "Name is required!"
                detailsName.requestFocus()
            }

            if (productPrice.isEmpty()) {
                detailsPrice.error = "Price is required!"
                detailsPrice.requestFocus()
            }

            if (productLocation.isEmpty()) {
                detailsLocation.error = "Location is required!"
                detailsLocation.requestFocus()
            }

            if (productQuantity.isEmpty()) {
                detailsQuantity.error = "Quantity is required!"
                detailsQuantity.requestFocus()
            }

            val flag = productID.isNotEmpty() && productName.isNotEmpty() && productPrice.isNotEmpty() && productLocation.isNotEmpty() && productQuantity.isNotEmpty()
            if (flag) {
                val product = hashMapOf(
                    "productID" to productID,
                    "productName" to productName,
                    "productPrice" to productPrice,
                    "productLocation" to productLocation,
                    "productQuantity" to productQuantity
                )

                collectionRef.document("$productID")
                    .set(product)
                    .addOnSuccessListener { documentReference ->
                        Log.d("TAG", "DocumentSnapshot added")
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TAG", "get failed with ", exception)
                    }

                val intentProducts = Intent().apply {
                    putExtra("message", "New product added")
                }
                setResult(Activity.RESULT_OK, intentProducts)
                finish()
            }
        }
        //Cancel button click of custom layout
        cancelBtn.setOnClickListener {
            val intentProducts = Intent(this, Products::class.java)
            startActivity(intentProducts)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_IDSCANNER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val productID = data!!.getStringExtra("productID")
                detailsID.setText(productID)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}