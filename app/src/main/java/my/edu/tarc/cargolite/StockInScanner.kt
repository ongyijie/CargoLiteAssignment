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
    private val InHistoryRef: CollectionReference = database.collection("inHistory")

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
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scanning Stock In QR Code")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val code = result.contents.toString()
                val shipmentType = code.substring(0, 1)
                val shipmentID = code.substring(0, 7)
                val productID = code.substring(8, 15)
                val quantity = (code.substring(16)).toInt()
                //----------------------------------------------------------------------------------
                if (shipmentType == "I") {
                    val documentRef: DocumentReference = InHistoryRef.document("$shipmentID")
                    documentRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val myDocument = task.result
                            if (myDocument != null) {
                                //Check if the shipment already existed
                                if (myDocument.exists()) {
                                    Toast.makeText(baseContext, "This shipment already exist!", Toast.LENGTH_SHORT).show()
                                } else {
                                    //credits : https://firebase.google.com/docs/firestore/manage-data/add-data
                                    //Write to firestore inHistory

                                    //Credits: Steffo Dimfelt https://stackoverflow.com/questions/48492993/firestore-get-documentsnapshots-fields-value
                                    val docRef: DocumentReference = collectionRef.document("$productID")
                                    docRef.get().addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val document = task.result
                                            if (document != null) {
                                                var updatedQty = 0
                                                val currentQty = document.getString("productQuantity")
                                                if (document.exists()) {
                                                    updatedQty = currentQty!!.toInt() + quantity
                                                    val update = hashMapOf("productQuantity" to updatedQty.toString())
                                                    docRef.set(update, SetOptions.merge())

                                                    val check = shipmentID.isNotEmpty() && productID.isNotEmpty() && quantity.toString().isNotEmpty()
                                                    if (check) {
                                                        val shipmentIn = hashMapOf(
                                                            "shipmentID" to shipmentID,
                                                            "productID" to productID,
                                                            "quantity" to quantity.toString()
                                                        )
                                                        documentRef.set(shipmentIn)
                                                    }

                                                    val builder = AlertDialog.Builder(this)
                                                    builder.setMessage("$quantity units of $productID added to inventory")
                                                    builder.setTitle("Stock In")
                                                    builder.setCancelable(false)
                                                    builder.setPositiveButton("Again") { dialog, which -> scanCode() }.setNegativeButton("Finish") { dialog, which -> finish() }
                                                    val dialog = builder.create()
                                                    dialog.show()
                                                } else {
                                                    val builder = AlertDialog.Builder(this)
                                                    builder.setCancelable(false)
                                                    builder.setMessage("This product has not been registered into the inventory")
                                                    builder.setTitle("Error")
                                                    builder.setPositiveButton("Again") { dialog, which -> scanCode() }.setNegativeButton("Finish") { dialog, which -> finish() }
                                                    val dialog = builder.create()
                                                    dialog.show()
                                                }
                                                //}
                                            } else {
                                                Log.d("LOGGER", "No such document")
                                            }
                                        } else {
                                            Log.d("LOGGER", "get failed with ", task.exception)
                                        }
                                    }
                                }//end of else exist
                            }
                        }
                    }
                }//end of if shipmentType = I
                else if (shipmentType == "O") {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(R.string.stockInError)
                    builder.setTitle(R.string.invalidQR)
                    builder.setCancelable(false)
                    builder.setPositiveButton("Again") { dialog, which -> scanCode() }.setNegativeButton("Finish") { dialog, which -> finish() }
                    val dialog = builder.create()
                    dialog.show()
                }
                //----------------------------------------------------------------------------------
            } else {
                finish()
                Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
