package my.edu.tarc.cargolite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_editproduct.view.*
import java.util.*

class ProductDetails : AppCompatActivity() {

    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionRef: CollectionReference = database.collection("products")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val productID: String? = intent.getStringExtra("productID")
        val productName: String? = intent.getStringExtra("productName")
        val productPrice: String? = intent.getStringExtra("productPrice")
        val productLocation: String? = intent.getStringExtra("productLocation")
        val productQuantity: String? = intent.getStringExtra("productQuantity")

        val id: TextView = findViewById(R.id.editTextID)
        val name: TextView = findViewById(R.id.editTextName)
        val price: TextView = findViewById(R.id.editTextPrice)
        val location: TextView = findViewById(R.id.editTextLocation)
        val quantity: TextView = findViewById(R.id.editTextQuantity)

        id.text = productID
        name.text = productName
        price.text = productPrice
        location.text = productLocation
        quantity.text = productQuantity

        val editBtn: Button = findViewById(R.id.editBtn)
        editBtn.setOnClickListener {

            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_editproduct, null)

            val myBuilder = AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setCancelable(false)

            val mAlertDialog = myBuilder.show()

            dialogView.dialogID.setText(productID)
            dialogView.dialogName.setText(productName)
            dialogView.dialogPrice.setText(productPrice)
            dialogView.dialogLocation.setText(productLocation)

            dialogView.saveBtn.setOnClickListener {

                //Get text from EditTexts of custom layout
                val productID = dialogView.dialogID.text.toString()
                val productName = dialogView.dialogName.text.toString()
                val productPrice = dialogView.dialogPrice.text.toString()
                val productLocation = dialogView.dialogLocation.text.toString()

                if (productName.isEmpty()) {
                    dialogView.dialogName.error = "Name is required!"
                    dialogView.dialogName.requestFocus()
                }

                if (productPrice.isEmpty()) {
                    dialogView.dialogPrice.error = "Price is required!"
                    dialogView.dialogPrice.requestFocus()
                }

                if (productLocation.isEmpty()) {
                    dialogView.dialogLocation.error = "Location is required!"
                    dialogView.dialogLocation.requestFocus()
                }

                val flag =
                    productName.isNotEmpty() && productPrice.isNotEmpty() && productLocation.isNotEmpty()

                if (flag) {
                    //Defining database
                    val database = Firebase.firestore

                    val product = hashMapOf(
                        "productID" to productID,
                        "productName" to productName.toUpperCase(Locale.ROOT),
                        "productPrice" to productPrice,
                        "productLocation" to productLocation.toUpperCase(Locale.ROOT),
                        "productQuantity" to productQuantity
                    )

                    database.collection("products").document(productID)
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

                    val docRef: DocumentReference = collectionRef.document(productID)
                    docRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            if (document != null) {
                                val productID = document.getString("productID")
                                val productName = document.getString("productName")
                                val productPrice = document.getString("productPrice")
                                val productLocation = document.getString("productLocation")
                                val productQuantity = document.getString("productQuantity")

                                id.text = productID
                                name.text = productName
                                price.text = productPrice
                                location.text = productLocation
                                quantity.text = productQuantity

                            } else {
                                Log.d("LOGGER", "No such document")
                            }
                        } else {
                            Log.d("LOGGER", "get failed with ", task.exception)
                        }
                    }
                    val snackBar = Snackbar.make(
                        findViewById(R.id.productDetails),
                        "Product details updated",
                        Snackbar.LENGTH_LONG
                    ).setAction("Action", null)
                    snackBar.show()
                    mAlertDialog.dismiss()
                }
            }
            dialogView.cancelBtn.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.product_action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.scanner -> startActivity(Intent(this, ProductScanner::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Products::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}