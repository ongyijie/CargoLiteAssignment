package my.edu.tarc.cargolite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.*
import com.google.zxing.integration.android.IntentIntegrator

class ProductScanner : AppCompatActivity() {

    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionRef: CollectionReference = database.collection("products")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        scanCode()
    }

    private fun scanCode() {
        val integrator = IntentIntegrator(this)
        integrator.captureActivity = CaptureAct::class.java
        integrator.setOrientationLocked(false)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_128)
        integrator.setPrompt("Scanning barcode to view product")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val code = result.contents.toString()

                //Credit: Steffo Dimfelt https://stackoverflow.com/questions/48492993/firestore-get-documentsnapshots-fields-value
                val docRef: DocumentReference = collectionRef.document(code)
                docRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null) {
                            if (document.exists()) {
                                val productID = document.getString("productID")
                                val productName = document.getString("productName")
                                val productPrice = document.getString("productPrice")
                                val productLocation = document.getString("productLocation")
                                val productQuantity = document.getString("productQuantity")

                                val intentDetails = Intent(this, ProductDetails::class.java)
                                intentDetails.putExtra("productID", productID)
                                intentDetails.putExtra("productName", productName)
                                intentDetails.putExtra("productPrice", productPrice)
                                intentDetails.putExtra("productLocation", productLocation)
                                intentDetails.putExtra("productQuantity", productQuantity)
                                startActivity(intentDetails)
                            } else {
                                val builder = AlertDialog.Builder(this)
                                builder.setCancelable(false)
                                builder.setMessage("Product does not exist")
                                builder.setTitle("Not found")
                                builder.setPositiveButton("Again") { dialog, which -> scanCode() }.setNegativeButton("Finish") { dialog, which -> finish() }
                                val dialog = builder.create()
                                dialog.show()
                            }
                        } else {
                            //if product is null
                            val builder = AlertDialog.Builder(this)
                            builder.setCancelable(false)
                            builder.setMessage("This barcode does not contain any information")
                            builder.setTitle("Null")
                            builder.setPositiveButton("Again") { dialog, which -> scanCode() }.setNegativeButton("Finish") { dialog, which -> finish() }
                            val dialog = builder.create()
                            dialog.show()
                        }
                    } else {
                        Log.d("LOGGER", "get failed with ", task.exception)
                    }
                }
            } else {
                finish()
                Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

