package my.edu.tarc.cargolite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.*
import com.google.zxing.integration.android.IntentIntegrator


class Scanner : AppCompatActivity() {

    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionRef: CollectionReference = database.collection("products")

//    private lateinit var codeScanner: CodeScanner
//    val MY_CAMERA_PERMISSION = 1111

//    override fun onCreate(savedInstanceState: Bundle?){
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_scanner)
//        val scannerView = findViewById<CodeScannerView>(R.id.scannerView)
//
//        codeScanner = CodeScanner(this, scannerView)
//
//        // Parameters (default values)
//        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
//        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
//        // ex. listOf(BarcodeFormat.QR_CODE)
//        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
//        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
//        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
//        codeScanner.isFlashEnabled = false // Whether to enable flash or not
//
//        // Callbacks
//        codeScanner.decodeCallback = DecodeCallback {
//            runOnUiThread {
//                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
//            }
//        }
//        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
//            runOnUiThread {
//                Toast.makeText(this, "Camera error: ${it.message}",
//                        Toast.LENGTH_LONG).show()
//            }
//        }
//
//        checkPermission()
//    }
//
//    fun checkPermission(){
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA),MY_CAMERA_PERMISSION)
//        }else{
//            codeScanner.startPreview()
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        if (requestCode==MY_CAMERA_PERMISSION && grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
//            codeScanner.startPreview()
//        }else{
//            Toast.makeText(this,"Cannot scan unless you give Camera Permission",Toast.LENGTH_LONG).show()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        codeScanner.startPreview()
//    }
//
//    override fun onPause() {
//        codeScanner.releaseResources()
//        super.onPause()
//    }
//}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        scanCode()
//        val scanner = IntentIntegrator(this)
//        scanner.initiateScan()

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == RESULT_OK) {
//            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//            if (result != null) {
//                if (result.contents == null) {
//                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
//                }
//            } else {
//                super.onActivityResult(requestCode, resultCode, data)
//            }
//        }
//    }

    private fun scanCode() {
        val integrator = IntentIntegrator(this)
        integrator.captureActivity = CaptureAct::class.java
        integrator.setOrientationLocked(false)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scanning Code")
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
                            if (shipmentType == "1") {
                                updatedQty = currentQty!!.toInt() + quantity
                                val update = hashMapOf("productQuantity" to updatedQty.toString())
                                docRef.set(update, SetOptions.merge())
                            } else if (shipmentType == "0") {
                                if (quantity > currentQty!!.toInt()) {
                                    val builder = AlertDialog.Builder(this)
                                    builder.setMessage("Insufiicient stock")
                                    builder.setTitle("Inventory Alert")
                                    builder.setPositiveButton("Noted") { dialog, which -> finish() }
                                    val dialog = builder.create()
                                    dialog.show()
                                } else {
                                    updatedQty = currentQty!!.toInt() - quantity
                                    val update = hashMapOf("productQuantity" to updatedQty.toString())
                                    docRef.set(update, SetOptions.merge())
                                }
                            }
                        } else {
                            Log.d("LOGGER", "No such document")
                        }
                    } else {
                        Log.d("LOGGER", "get failed with ", task.exception)
                    }
                }

                val builder = AlertDialog.Builder(this)
                builder.setMessage(result.contents)
                builder.setTitle("Scanning Results")
                builder.setPositiveButton("Again") { dialog, which -> scanCode() }.setNegativeButton("Finish") { dialog, which -> finish() }
                val dialog = builder.create()
                dialog.show()
            } else {
                Toast.makeText(this, "No results", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}