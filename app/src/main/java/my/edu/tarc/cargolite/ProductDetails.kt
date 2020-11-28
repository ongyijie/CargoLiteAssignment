package my.edu.tarc.cargolite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ProductDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var productID: String? = intent.getStringExtra("productID")
        var productName: String? = intent.getStringExtra("productName")
        var productPrice: String? = intent.getStringExtra("productPrice")
        var productLocation: String? = intent.getStringExtra("productLocation")
        var productQuantity: String? = intent.getStringExtra("productQuantity")

        val ID: TextView = findViewById(R.id.ID)
        val Name: TextView = findViewById(R.id.Name)
        val Price: TextView = findViewById(R.id.Price)
        val Location: TextView = findViewById(R.id.Location)
        val Quantity: TextView = findViewById(R.id.Quantity)

        ID.text = productID
        Name.text = productName
        Price.text = productPrice
        Location.text = productLocation
        Quantity.text = productQuantity

        val closeBtn: Button = findViewById(R.id.closeBtn)
        closeBtn.setOnClickListener {
            val intentProducts = Intent(this, Products::class.java)
            startActivity(intentProducts)
        }
    }
}