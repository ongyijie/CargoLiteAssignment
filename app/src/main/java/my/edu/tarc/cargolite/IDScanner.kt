package my.edu.tarc.cargolite

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator

class IDScanner : AppCompatActivity() {

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
        integrator.setPrompt("Scanning barcode to insert ID")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val productID: String = result.contents
                val intentAddProduct = Intent().apply {
                    putExtra("productID", productID)
                }
                setResult(Activity.RESULT_OK, intentAddProduct)
                finish()
            } else {
                finish()
                Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}