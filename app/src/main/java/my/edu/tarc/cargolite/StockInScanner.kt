package my.edu.tarc.cargolite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.*
import com.google.zxing.integration.android.IntentIntegrator

class StockInScanner : AppCompatActivity() {
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionRef: CollectionReference = database.collection("products")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        scanCode()
//        val scanner = IntentIntegrator(this)
//        scanner.initiateScan()

    }

    private fun scanCode() {
        val integrator = IntentIntegrator(this)
        integrator.captureActivity = CaptureAct::class.java
        integrator.setOrientationLocked(false)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scanning Stock In QR Code")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val code = result.contents.toString()
                val shipmentType = code.substring(0, 1)
                val product = code.substring(2, 9)
                val quantity = (code.substring(10)).toInt()

                //Credits: Steffo Dimfelt https://stackoverflow.com/questions/48492993/firestore-get-documentsnapshots-fields-value
                val docRef: DocumentReference = collectionRef.document("$product")
                docRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null) {
                            var updatedQty = 0
                            val currentQty = document.getString("productQuantity")
                            if (shipmentType == "0") {
                                val builder = AlertDialog.Builder(this)
                                builder.setMessage(R.string.stockInError)
                                builder.setTitle(R.string.invalidQR)
                                builder.setPositiveButton("Again") { dialog, which -> scanCode() }.setNegativeButton("Finish") { dialog, which -> finish() }
                                val dialog = builder.create()
                                dialog.show()
                            } else if (shipmentType == "1") {
                                updatedQty = currentQty!!.toInt() + quantity
                                val update = hashMapOf("productQuantity" to updatedQty.toString())
                                docRef.set(update, SetOptions.merge())

                                val builder = AlertDialog.Builder(this)
                                builder.setMessage(result.contents)
                                builder.setTitle("Scanning Results")
                                builder.setPositiveButton("Again") { dialog, which -> scanCode() }.setNegativeButton("Finish") { dialog, which -> finish() }
                                val dialog = builder.create()
                                dialog.show()
                            }
                        } else {
                            Log.d("LOGGER", "No such document")
                        }
                    } else {
                        Log.d("LOGGER", "get failed with ", task.exception)
                    }
                }
            } else {
                Toast.makeText(this, "No results", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}