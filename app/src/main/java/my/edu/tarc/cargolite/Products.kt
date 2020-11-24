package my.edu.tarc.cargolite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_addproduct.view.*


class Products: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fab_add: FloatingActionButton = findViewById(R.id.fab_add)

        //Button click to show dialog
        fab_add.setOnClickListener {
            //Inflate the dialog with custom view
            val DialogView = LayoutInflater.from(this).inflate(R.layout.dialog_addproduct, null)
            //AlertDialogBuilder
            val myBuilder = AlertDialog.Builder(this)
                    .setView(DialogView)
                    .setTitle("Add New Product")
            //Show dialog
            val  mAlertDialog = myBuilder.show()
            val productList = ArrayList<String>(5)

            val textViewProducts: TextView = findViewById(R.id.textViewProducts)
            //Add button click of custom layout
            DialogView.dialogAddBtn.setOnClickListener {
                //Dismiss dialog
                mAlertDialog.dismiss()

                //Show snackbar
                val snackBar = Snackbar.make(
                    findViewById(R.id.ConstraintLayout), "New product added", Snackbar.LENGTH_LONG
                ).setAction("Action", null)
                snackBar.show()

                //Get text from EditTexts of custom layout
                val productID = DialogView.dialogIDEt.text.toString()
                val productName = DialogView.dialogNameEt.text.toString()
                val unitPrice = DialogView.dialogPriceEt.text.toString()
                val stockLocation = DialogView.dialogLocationEt.text.toString()
                val quantity = DialogView.dialogQuantityEt.text.toString()

                //Defining database
                val database = Firebase.firestore

                val product = hashMapOf(
                    "productID" to productID,
                    "productName" to productName,
                    "unitPrice" to unitPrice,
                    "stockLocation" to stockLocation,
                    "quantity" to quantity
                )

                database.collection("products")
                        .add(product)
                        .addOnSuccessListener { documentReference ->
                        Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                        }
                textViewProducts.text = String.format(
                    "%20s %10s \n%20s %10s \n%20s %10s \n%20s %10s \n%20s %10s",
                    "Product ID: ",
                    productID,
                    "Product Name: ",
                    productName,
                    "Unit price (RM) :",
                    unitPrice,
                    "Stock location :",
                    stockLocation,
                    "Quantity: ",
                    quantity
                )
            }

            //Cancel button click of custom layout
            DialogView.dialogCancelBtn.setOnClickListener {
                //Dismiss dialog
                mAlertDialog.dismiss()
            }
        }
    }
}

