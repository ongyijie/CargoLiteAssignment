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

        val productID: String? = intent.getStringExtra("productID")
        val productName: String? = intent.getStringExtra("productName")
        val productPrice: String? = intent.getStringExtra("productPrice")
        val productLocation: String? = intent.getStringExtra("productLocation")
        val productQuantity: String? = intent.getStringExtra("productQuantity")

        val id: TextView = findViewById(R.id.ID)
        val name: TextView = findViewById(R.id.Name)
        val price: TextView = findViewById(R.id.Price)
        val location: TextView = findViewById(R.id.Location)
        val quantity: TextView = findViewById(R.id.Quantity)

        id.text = productID
        name.text = productName
        price.text = productPrice
        location.text = productLocation
        quantity.text = productQuantity

        val closeBtn: Button = findViewById(R.id.closeBtn)
        closeBtn.setOnClickListener {
            val intentProducts = Intent(this, Products::class.java)
            startActivity(intentProducts)
        }
    }
}