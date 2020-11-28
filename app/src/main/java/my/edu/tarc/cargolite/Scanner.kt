package my.edu.tarc.cargolite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.*
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.dialog_viewproduct.*
import kotlinx.android.synthetic.main.dialog_viewproduct.view.*


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

                //Credits: Steffo Dimfelt https://stackoverflow.com/questions/48492993/firestore-get-documentsnapshots-fields-value
                val docRef: DocumentReference = collectionRef.document("$code")
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
                            }else {
                                val builder = AlertDialog.Builder(this)
                                builder.setMessage("Product does not exist")
                                builder.setTitle("Not found")
                                builder.setPositiveButton("Again") { dialog, which -> scanCode() }.setNegativeButton("Finish") { dialog, which -> finish() }
                                val dialog = builder.create()
                                dialog.show()
                            }
                        } else {
                            //if product is null
                            val builder = AlertDialog.Builder(this)
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
                Toast.makeText(this, "No results", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}