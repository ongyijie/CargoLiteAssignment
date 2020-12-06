package my.edu.tarc.cargolite

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.*
import com.google.zxing.integration.android.IntentIntegrator
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StockOutScanner : AppCompatActivity() {

    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionRef: CollectionReference = database.collection("products")
    private val OutHistoryRef: CollectionReference = database.collection("outHistory")

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
        integrator.setPrompt("Scanning Stock Out QR Code")
        integrator.initiateScan()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val date: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val time: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val code = result.contents.toString()
                val shipmentType = code.substring(0, 1)
                val shipmentID = code.substring(0, 7)
                val productID = code.substring(8, 15)
                val quantity = (code.substring(16)).toInt()
                //----------------------------------------------------------------------------------
                if (shipmentType == "O") {
                    val documentRef: DocumentReference = OutHistoryRef.document(shipmentID)
                    documentRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val myDocument = task.result
                            if (myDocument != null) {
                                //Check if the shipment already existed
                                if (myDocument.exists()) {
                                    val builder = AlertDialog.Builder(this)
                                    builder.setCancelable(false)
                                    builder.setMessage("This QR code has been scanned!")
                                    builder.setTitle("Error")
                                    builder.setPositiveButton("Noted") { dialog, which -> scanCode() }.setNegativeButton("Finish") { dialog, which -> finish() }
                                    val dialog = builder.create()
                                    dialog.show()
                                } else {

                                    //Credit: Steffo Dimfelt https://stackoverflow.com/questions/48492993/firestore-get-documentsnapshots-fields-value
                                    val docRef: DocumentReference = collectionRef.document(productID)
                                    docRef.get().addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val document = task.result
                                            if (document != null) {
                                                val updatedQty: Int
                                                val currentQty = document.getString("productQuantity")

                                                //} else if (shipmentType == "O") {
                                                if (document.exists()) {
                                                    if (quantity > currentQty!!.toInt()) {
                                                        val builder = AlertDialog.Builder(this)
                                                        builder.setCancelable(false)
                                                        builder.setMessage("Quantity needed: $quantity \nQuantity on hand: $currentQty")
                                                        builder.setTitle("Insufficient Stock")
                                                        builder.setPositiveButton("Noted") { dialog, which -> scanCode() }.setNegativeButton("Finish") { dialog, which -> finish() }
                                                        val dialog = builder.create()
                                                        dialog.show()
                                                    } else {
                                                        updatedQty = currentQty.toInt() - quantity
                                                        val update = hashMapOf("productQuantity" to updatedQty.toString())
                                                        docRef.set(update, SetOptions.merge())

                                                        val builder = AlertDialog.Builder(this)
                                                        builder.setCancelable(false)
                                                        builder.setMessage("$time \n$date \nProduct: $productID \nQuantity: $quantity units")
                                                        builder.setTitle("Stock Out")
                                                        builder.setPositiveButton("Again") { dialog, which -> scanCode() }.setNegativeButton("Finish") { dialog, which -> finish() }
                                                        val dialog = builder.create()
                                                        dialog.show()

                                                        val check = shipmentID.isNotEmpty() && productID.isNotEmpty() && quantity.toString().isNotEmpty()
                                                        if (check) {
                                                            val shipmentOut = hashMapOf(
                                                                "shipmentID" to shipmentID,
                                                                "productID" to productID,
                                                                "quantity" to quantity.toString(),
                                                                "date" to date,
                                                                "time" to time
                                                            )
                                                            documentRef.set(shipmentOut)
                                                        }
                                                    }
                                                } else {
                                                    val builder = AlertDialog.Builder(this)
                                                    builder.setMessage("Product does not exist in your inventory")
                                                    builder.setTitle("Error")
                                                    builder.setCancelable(false)
                                                    builder.setPositiveButton("Noted") { dialog, which -> scanCode() }.setNegativeButton("Finish") { dialog, which -> finish() }
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
                                }//end of else exist
                            }
                        }
                    }
                }//end of if shipmentType = I
                else if (shipmentType == "I") {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(R.string.stockOutError)
                    builder.setTitle(R.string.invalidQR)
                    builder.setPositiveButton("Noted") { dialog, which -> scanCode() }.setNegativeButton("Finish") { dialog, which -> finish() }
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
