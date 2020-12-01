package my.edu.tarc.cargolite

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class ShipmentHistory: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipment_history)

        //Enabling up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Link UI to program
        val cardViewStockInHistory: CardView = findViewById(R.id.cardViewStockInHistory)
        val cardViewStockOutHistory : CardView = findViewById(R.id.cardViewStockOutHistory)

        cardViewStockInHistory.setOnClickListener() {
            val intentStockInHistory = Intent(this, StockInHistory::class.java)
            startActivity(intentStockInHistory)
        }
        cardViewStockOutHistory.setOnClickListener() {
            val intentStockOutHistory = Intent(this, StockOutHistory::class.java)
            startActivity(intentStockOutHistory)
        }
    }
}